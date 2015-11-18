package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import java.io.InputStream;
import java.util.Map;

import ch.fluxron.fluxronapp.events.base.ITypedCallback;
import ch.fluxron.fluxronapp.events.base.SynchronousReplyEvent;

/**
 *  Requests the load of all
 */
public class GetAllAttachmentStreamsFromObjectCommand extends SynchronousReplyEvent<Map<String, InputStream>> {
    private String objectId;

    public GetAllAttachmentStreamsFromObjectCommand(String objectId, ITypedCallback<Map<String, InputStream>> callback) {
        super(callback);
        this.objectId = objectId;
    }

    /**
     * Returns the object id
     * @return Identifier
     */
    public String getObjectId() {
        return objectId;
    }
}
