package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests a save of an object
 */
public class SaveObjectCommand extends RequestResponseConnection {
    private Object data;
    private String documentId;

    /**
     * Get the object to be saved
     * @return Object
     */
    public Object getData() {
        return data;
    }

    /**
     * Set the object to be saved
     * @param data Object data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Gets the document id that the object should be using
     * @return Document id
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Sets the document id that the object should be using
     * @param documentId Document id
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
