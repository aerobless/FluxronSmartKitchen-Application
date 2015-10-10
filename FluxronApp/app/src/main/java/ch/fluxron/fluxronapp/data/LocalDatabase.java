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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.fluxron.fluxronapp.events.modelDal.DeleteObjectById;
import ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelDal.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelDal.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.SaveObjectCommand;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Listens to eventbus messages. Stores data persistently.
 */
public class LocalDatabase {
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
            properties.put("type",cmd.getData().getClass().getCanonicalName());
            documents.tryPutProperties(doc, properties);
        }
    }


    /**
     * Triggered when an object should be deleted by id.
     * @param cmd Command
     */
    public void onEventAsync(DeleteObjectById cmd) {
        Document doc = database.getExistingDocument(cmd.getId());
        if (doc!=null){
            try {
                doc.delete();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Triggered when an object should be loaded by id.
     * @param cmd Command
     */
    public void onEventAsync(LoadObjectByIdCommand cmd) {

        // load the document by Id
        Document doc = database.getExistingDocument(cmd.getId());
        if(doc!=null){
            // Document found, get the type
            Object typeProperty = doc.getProperty("type");
            String typeName = typeProperty instanceof String ? (String)typeProperty : null;

            // Convert to object using ObjectMapper
            if (typeName!=null) {
                try {
                    Class<?> objectType = Class.forName(typeName);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    Object result = mapper.convertValue(doc.getProperties(), objectType);
                    ObjectLoaded loadedEvent = new ObjectLoaded(doc.getId(), result);
                    provider.getDalEventBus().post(loadedEvent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            // TODO: Error message if not found
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
        View kitchenList = database.getView("objectsByType");
        if (kitchenList != null) {
            kitchenList.setMap(
                    new Mapper() {
                        @Override
                        public void map(Map<String, Object> document, Emitter emitter) {
                            emitter.emit((Object)document.get("type"), null);
                        }
                    }, "1"
            );
        }

        kitchenList = database.getView("objectsByType");
        Query orderedQuery = kitchenList.createQuery();
        orderedQuery.setStartKey("ch.fluxron.fluxronapp.objectBase.Kitchen");
        orderedQuery.setEndKey("ch.fluxron.fluxronapp.objectBase.Kitchen");
        QueryEnumerator results;

        try {
            results = orderedQuery.run();
            for (Iterator<QueryRow> it = results; it.hasNext();) {
                QueryRow row = it.next();

                String kitchenName = String.valueOf(row.getDocument().getProperty("name"));
                String kitchenId = String.valueOf(row.getDocumentId());
                String kitchenDescription = String.valueOf(row.getDocument().getProperty("description"));

                if(query == null || kitchenName.contains(query)) {
                    
                    Kitchen data = new Kitchen(kitchenId, kitchenName);
                    data.setDescription(kitchenDescription);
                    provider.getDalEventBus().post(new KitchenLoaded(data));
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
