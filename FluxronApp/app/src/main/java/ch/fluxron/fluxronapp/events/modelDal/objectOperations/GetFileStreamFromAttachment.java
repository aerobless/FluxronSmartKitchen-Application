package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests the stream of an Object
 */
public class GetFileStreamFromAttachment extends RequestResponseConnection {
    private String objectId;
    private String attachmentName;

    /**
     * Sets the object's id and the attachment name
     * @param objectId
     * @param objectName
     */
    public GetFileStreamFromAttachment(String objectId, String objectName) {
        this.objectId = objectId;
        this.attachmentName = objectName;
    }

    /**
     * Returns the id of the object
     * @return Id of the object
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * Returns the name of the attachment
     * @return Name of the attachment
     */
    public String getAttachmentName() {
        return attachmentName;
    }
}
