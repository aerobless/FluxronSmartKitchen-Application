package ch.fluxron.fluxronapp.data;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.View;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.Iterator;
import java.util.Map;

import ch.fluxron.fluxronapp.events.modelDal.DeleteObjectById;
import ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelDal.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.SaveObjectCommand;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Listens to eventbus messages. Stores data persistently.
 */
public class LocalDatabase {
    private static final String TYPE_PROPERTY = "type";

    private IEventBusProvider provider;
    private Database database;
    private ObjectConverter converter;
    private DocumentFunctions documents;

    public LocalDatabase(IEventBusProvider provider, Database database) {
        this.provider = provider;
        this.database = database;
        this.provider.getDalEventBus().register(this);
        this.converter = new ObjectConverter();
        this.documents = new DocumentFunctions(database);
    }

    public void onEventAsync(SaveObjectCommand cmd) {
        Document doc = documents.createDocumentOnNull(cmd.getDocumentId());

        //TODO: change type to constant
        if(doc != null){
            Map<String, Object> properties = converter.convertObjectToMap(cmd.getData());
            properties.put(TYPE_PROPERTY,cmd.getData().getClass().getCanonicalName());
            documents.tryPutProperties(doc, properties);
        }
    }


    /**
     * Triggered when an object should be deleted by id.
     * @param cmd Command
     */
    public void onEventAsync(DeleteObjectById cmd) {
        documents.deleteDocument(database.getExistingDocument(cmd.getId()));
    }

    /**
     * Triggered when an object should be loaded by id.
     * @param cmd Command
     */
    public void onEventAsync(LoadObjectByIdCommand cmd) {
        // load the document by Id
        Document doc = database.getExistingDocument(cmd.getId());

        loadObjectFromDocument(doc);
    }

    /**
     * Tries to convert the document into an object and sends an ObjectLoaded message
     * @param doc Document
     */
    private void loadObjectFromDocument(Document doc){
        if(doc != null){
            // Get the type information
            Object typeProperty = doc.getProperty(TYPE_PROPERTY);
            String typeName = typeProperty instanceof String ? (String)typeProperty : null;

            // Convert using ObjectMapper
            Class<?> objectType = converter.getClassFromName(typeName);
            if (objectType != null) {
                ObjectLoaded msg = new ObjectLoaded(doc.getId(), converter.convertMapToObject(doc.getProperties(), objectType));
                provider.getDalEventBus().post(msg);
            }
        }
    }

    /**
     * Triggered when a find kitchen command is issued
     * @param cmd Command
     */
    public void onEventAsync(FindKitchenCommand cmd) {
        retrieveKitchens(cmd.getQuery());
    }

    /**
     * Reads all the kitchens containing the query text in their name
     * @param query Query, ignored if null
     */
    public void retrieveKitchens(String query){
        Query orderedQuery = createTypeQuery(Kitchen.class);
        QueryEnumerator results;

        try {
            results = orderedQuery.run();
            for (Iterator<QueryRow> it = results; it.hasNext();) {
                QueryRow row = it.next();
                Kitchen o = (Kitchen)converter.convertMapToObject(row.getDocument().getProperties(), Kitchen.class);
                if(query == null || o.getName().contains(query)) {
                    provider.getDalEventBus().post(new ObjectLoaded(o.getId(), o));
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a query that reads all objects of a certain type from the database
     * @param type Class of the type to be read
     * @param <T> Type
     * @return Query
     */
    private <T> Query createTypeQuery(Class<T> type){
        Query q = createViewObjectsByType().createQuery();

        String fullClassName = type.getCanonicalName();
        q.setStartKey(fullClassName);
        q.setEndKey(fullClassName);

        return q;
    }

    /**
     * Creates or retrieves a view that maps all stored objects to their "type" attribute
     * @return View
     */
    private View createViewObjectsByType(){
        View objectsByType = database.getView("objectsByType");

        Mapper mapper = new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                emitter.emit((Object)document.get(TYPE_PROPERTY), null);
            }
        };

        if (objectsByType != null) {
            objectsByType.setMap(mapper, "1");
        }

        return objectsByType;
    }
}
