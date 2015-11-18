package ch.fluxron.fluxronapp.data;

import android.net.Uri;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Revision;
import com.couchbase.lite.UnsavedRevision;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
    public void tryPutProperties(Document doc, final Map<String, Object> properties){
        try {
            doc.update(new Document.DocumentUpdater() {
                @Override
                public boolean update(UnsavedRevision newRevision) {
                    Map<String,Object> props = newRevision.getUserProperties();
                    for (Map.Entry<String, Object> property : properties.entrySet()) {
                        props.put(property.getKey(), property.getValue());
                    }
                    newRevision.setUserProperties(props);
                    return true;
                }
            });
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

    /**
     * Attaches a file's data to a document
     * @param doc Document
     * @param fileUri File pointing to the data
     * @param attachmentName Name of the attachment
     * @param contentType Content type of the attachment (i.e. image/jpeg)
     */
    public void attachFileToDocument(Document doc, Uri fileUri, String attachmentName, String contentType){
        try {
            InputStream stream = new BufferedInputStream(new FileInputStream(fileUri.getPath()));

            UnsavedRevision newRev = doc.getCurrentRevision().createRevision();
            newRev.setAttachment(attachmentName, contentType, stream);
            newRev.save();
        } catch (IOException | CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a stream pointing to a document's attachment
     * @param doc Document
     * @param attachmentName Name of the attachment
     * @return Stream or null if not found
     */
    public InputStream getStreamFromAttachment(Document doc, String attachmentName){
        Revision rev = doc.getCurrentRevision();
        Attachment att = rev.getAttachment(attachmentName);
        if (att != null) {
            InputStream is = null;
            try {
                is = att.getContent();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
            return is;
        }
        return null;
    }

    /**
     * Gets a map of all attachment streams belonging to a document FileName - InputStream
     * @param doc Document
     * @return Map
     */
    public Map<String, InputStream> getAllStreams(Document doc) {
        Revision rev = doc.getCurrentRevision();
        Map<String, InputStream> result = new HashMap<>();

        for(Attachment cur : rev.getAttachments()){
            try {
                result.put(cur.getName(), cur.getContent());
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

        }

        return result;
    }
}
