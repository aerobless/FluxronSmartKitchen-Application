package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import ch.fluxron.fluxronapp.events.base.ITypedCallback;
import ch.fluxron.fluxronapp.events.base.SynchronousReplyEvent;

/**
 * Provides direct access to an object via a callback
 */
public class GetObjectByIdCommand extends SynchronousReplyEvent<Object> {
    private String objectId;

    /**
     * Create a new direct access command
     * @param objectId Id of the object
     * @param callback Callback to notify when loaded
     */
    public GetObjectByIdCommand(String objectId, ITypedCallback<Object> callback) {
        super(callback);
        this.objectId = objectId;
    }

    /**
     * Object id that is accessed
     * @return objectId Object id as a string
     */
    public String getObjectId() {
        return this.objectId;
    }
}
