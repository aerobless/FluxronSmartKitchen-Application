package ch.fluxron.fluxronapp.events.modelDal.objectOperations;


import java.io.InputStream;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests the storage of a stream's content
 */
public class AttachStreamToObjectByIdCommand extends RequestResponseConnection {
    private InputStream stream;
    private String documentId;
    private String attachmentName;

    /**
     * Requests the storage of a stream's content
     * @param documentId Id of the document
     * @param stream Stream to read from, must be readable
     * @param attachmentName Name of the attachment
     */
    public AttachStreamToObjectByIdCommand(String documentId, InputStream stream, String attachmentName){
        this.documentId = documentId;
        this.stream = stream;
        this.attachmentName = attachmentName;
    }

    /**
     * Returns the stream to read from
     * @return Stream to read from
     */
    public InputStream getStream() {
        return stream;
    }

    /**
     * Returns the id of the document
     * @return Document ID
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Returns the name of the attachment
     * @return Name of the attachment
     */
    public String getAttachmentName() {
        return attachmentName;
    }
}
