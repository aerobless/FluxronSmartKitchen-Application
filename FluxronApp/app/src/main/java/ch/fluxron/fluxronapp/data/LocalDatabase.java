package ch.fluxron.fluxronapp.data;

import android.content.ContentResolver;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.View;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.base.ResponseFail;
import ch.fluxron.fluxronapp.events.base.ResponseOK;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.AttachFileToObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.AttachStreamToObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.DeleteObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetAllAttachmentStreamsFromObjectCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetFileStreamFromAttachmentCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.IStreamProvider;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.LoadObjectByTypeCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectCreated;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.SaveObjectCommand;
import ch.fluxron.fluxronapp.objectBase.Device;
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
    private ContentResolver resolver;

    public LocalDatabase(IEventBusProvider provider, Database database, ContentResolver resolver) {
        this.provider = provider;
        this.database = database;
        this.provider.getDalEventBus().register(this);
        this.converter = new ObjectConverter();
        this.documents = new DocumentFunctions(database);
        this.resolver = resolver;
    }

    /**
     * Triggered when saving an object
     * @param cmd Command message
     */
    public void onEventAsync(SaveObjectCommand cmd) {
        boolean created = !documents.exists(cmd.getDocumentId());
        Document doc = documents.createDocumentOnNull(cmd.getDocumentId());

        if(doc != null){
            Map<String, Object> properties = converter.convertObjectToMap(cmd.getData());
            properties.put(TYPE_PROPERTY, cmd.getData().getClass().getCanonicalName());
            documents.tryPutProperties(doc, properties);

        } else {
            doc = new Document(database, cmd.getDocumentId());
            Map<String, Object> properties = converter.convertObjectToMap(cmd.getData());
            properties.put(TYPE_PROPERTY, cmd.getData().getClass().getCanonicalName());
            documents.tryPutProperties(doc, properties);
        }

        // Fire an event if we created a new object
        if(created) {
            ObjectCreated event = new ObjectCreated(getObjectFromDocument(doc));
            event.setConnectionId(cmd);
            provider.getDalEventBus().post(event);
        } else
        {
            // Fire an event to signal that the save was successful
            ResponseOK ok = new ResponseOK();
            ok.setConnectionId(cmd);
            provider.getDalEventBus().post(ok);
        }
    }

    /**
     * Triggered when prompted to provide an attachment's stream
     * @param cmd Command message
     */
    public void onEventAsync(final GetFileStreamFromAttachmentCommand cmd) {
        final Document doc = database.getExistingDocument(cmd.getObjectId());
        if(doc != null) {
            cmd.notifyCompletion(new IStreamProvider() {
                @Override
                public InputStream openStream() {
                    return documents.getStreamFromAttachment(doc, cmd.getAttachmentName());
                }
            });
        }
    }

    /**
     * Triggered when attaching an image to an object
     * @param cmd Command
     */
    public void onEventAsync(AttachFileToObjectByIdCommand cmd) {
        Document doc = database.getExistingDocument(cmd.getDocumentId());
        if(doc != null) {
            documents.attachFileToDocument(doc, cmd.getFileUri(), cmd.getAttachmentName(), resolver.getType(cmd.getFileUri()));

            // Generic ok response if the attachment process went fine
            ResponseOK ok = new ResponseOK();
            ok.setConnectionId(cmd);
            provider.getDalEventBus().post(ok);
        }
        else {
            ResponseFail fail = new ResponseFail();
            fail.setConnectionId(cmd);
            provider.getDalEventBus().post(fail);
        }
    }

    /**
     * Triggered when attaching a stream to an object
     * @param cmd Command
     */
    public void onEventAsync(AttachStreamToObjectByIdCommand cmd) {
        Document doc = database.getExistingDocument(cmd.getDocumentId());
        if(doc != null) {
            documents.attachFileToDocument(doc, cmd.getStream(), cmd.getAttachmentName(), "application/data");

            // Generic ok response if the attachment process went fine
            ResponseOK ok = new ResponseOK();
            ok.setConnectionId(cmd);
            provider.getDalEventBus().post(ok);
        }
    }

    /**
     * Triggered when an object should be deleted by id.
     * @param cmd Command
     */
    public void onEventAsync(DeleteObjectByIdCommand cmd) {
        documents.deleteDocument(database.getExistingDocument(cmd.getId()));
    }

    /**
     * Triggered when an object should be loaded by id.
     * @param cmd Command
     */
    public void onEventAsync(LoadObjectByIdCommand cmd) {
        // load the document by Id
        Document doc = database.getExistingDocument(cmd.getId());

        loadObjectFromDocument(doc, cmd);
    }

    /**
     * Triggered when an object should be returned by id.
     * @param cmd Command
     */
    public void onEventAsync(GetObjectByIdCommand cmd) {
        // load the document by Id
        Document doc = database.getExistingDocument(cmd.getObjectId());

        Object obj = getObjectFromDocument(doc);
        if (obj != null) {
            cmd.notifyCompletion(obj);
        }
    }

     /**
     * Triggered when a LoadObjectByTypeCommand is issued.
     * @param cmd Command
     */
    public void onEventAsync(LoadObjectByTypeCommand cmd) {
        if(cmd.getType().equals("device")){
            retrieveDevices();
        }
    }

    /**
     * Retrieves all the devices and posts ObjectLoaded events.
     */
    public void retrieveDevices(){
        Query orderedQuery = createTypeQuery(Device.class);
        QueryEnumerator results;

        try {
            results = orderedQuery.run();
            for (Iterator<QueryRow> it = results; it.hasNext();) {
                QueryRow row = it.next();
                Device o = (Device)converter.convertMapToObject(row.getDocument().getProperties(), Device.class);
                ObjectLoaded loadedEvent = new ObjectLoaded(o.getAddress(), o);
                provider.getDalEventBus().post(loadedEvent);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to convert the document into an object and sends an ObjectLoaded message
     * @param doc Document
     * @param conn Connection
     */
    private void loadObjectFromDocument(Document doc, RequestResponseConnection conn){
        Object o = getObjectFromDocument(doc);

        if(o != null){
            ObjectLoaded msg = new ObjectLoaded(doc.getId(), o);
            msg.setConnectionId(conn);
            provider.getDalEventBus().post(msg);
        }
        else {
            ResponseFail fail = new ResponseFail();
            fail.setConnectionId(conn);
            provider.getDalEventBus().post(fail);
        }
    }

    /**
     * Tries to convert the document into an object and returns it
     * @param doc Document
     */
    private Object getObjectFromDocument(Document doc){
        if(doc != null){
            // Get the type information
            Object typeProperty = doc.getProperty(TYPE_PROPERTY);
            String typeName = typeProperty instanceof String ? (String)typeProperty : null;

            // Convert using ObjectMapper
            Class<?> objectType = converter.getClassFromName(typeName);
            return converter.convertMapToObject(doc.getProperties(), objectType);
        }
        return null;
    }

    /**
     * Triggered when a find kitchen command is issued
     * @param cmd Command
     */
    public void onEventAsync(FindKitchenCommand cmd) {
        retrieveKitchens(cmd.getQuery(), cmd);
    }

    /**
     * Reads all the kitchens containing the query text in their name
     * @param query Query, ignored if null
     */
    public void retrieveKitchens(String query, RequestResponseConnection originalMessage){
        Query orderedQuery = createTypeQuery(Kitchen.class);
        QueryEnumerator results;

        try {
            results = orderedQuery.run();
            for (Iterator<QueryRow> it = results; it.hasNext();) {
                QueryRow row = it.next();
                Kitchen o = (Kitchen)converter.convertMapToObject(row.getDocument().getProperties(), Kitchen.class);
                if(query == null || "".equals(query) || o.getName().toLowerCase().contains(query.toLowerCase())) {
                    ObjectLoaded loadedEvent = new ObjectLoaded(o.getId(), o);
                    loadedEvent.setConnectionId(originalMessage);
                    provider.getDalEventBus().post(loadedEvent);
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

    /**
     * All streams should be returned
     * @param cmd Command with parameters
     */
    public void onEventAsync(GetAllAttachmentStreamsFromObjectCommand cmd) {
        final Document doc = database.getExistingDocument(cmd.getObjectId());

        if(doc != null) {
            Map<String, InputStream> streams = documents.getAllStreams(doc);
            cmd.notifyCompletion(streams);
        }
    }
}
