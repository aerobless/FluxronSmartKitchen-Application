package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import ch.fluxron.fluxronapp.events.base.ITypedCallback;
import ch.fluxron.fluxronapp.events.base.SynchronousReplyEvent;

/**
 * Requests the stream of an Object
 */
public class GetFileStreamFromAttachmentCommand extends SynchronousReplyEvent<IStreamProvider> {
    private String objectId;
    private String attachmentName;

    /**
     * Sets the object's id and the attachment name
     * @param objectId Id of the object the file is attached to
     * @param attachmentName Name of the attached file
     */
    public GetFileStreamFromAttachmentCommand(String objectId, String attachmentName, ITypedCallback<IStreamProvider> callback) {
        super(callback);
        this.objectId = objectId;
        this.attachmentName = attachmentName;
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
