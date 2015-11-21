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

    public AttachStreamToObjectByIdCommand(String documentId, InputStream stream, String attachmentName){
        this.documentId = documentId;
        this.stream = stream;
        this.attachmentName = attachmentName;
    }

    public InputStream getStream() {
        return stream;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getAttachmentName() {
        return attachmentName;
    }
}
