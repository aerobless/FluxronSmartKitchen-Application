package ch.fluxron.fluxronapp.data;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import java.util.Map;

/**
 * Provides functions to safely access / modify documents
 */
public class DocumentFunctions {
    private Database database;

    /**
     * Creates a new instance of document functions
     * @param database Database to operate on
     */
    public DocumentFunctions(Database database) {
        this.database = database;
    }

    /**
     * Loads the document based on its id. If the id supplied is null, a new document is created
     * @param id Id to find the document by
     * @return Document if found or created, null if not found
     */
    public Document createDocumentOnNull(String id){
        return id == null ? database.createDocument() : database.getExistingDocument(id);
    }

    /**
     * Tries to save a documents properties
     * @param doc Document
     * @param properties New or updatable properties
     */
    public void tryPutProperties(Document doc, Map<String, Object> properties){
        try {
            doc.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the document, null documents are ignored
     * @param doc Document, can be null
     */
    public void deleteDocument(Document doc){
        if (doc!=null){
            try {
                doc.delete();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tests wether a document exists or not
     * @param documentId Id to search for
     * @return True if found, otherwise false
     */
    public boolean exists(String documentId) {
        return database.getExistingDocument(documentId) != null;
    }
}
