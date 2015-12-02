package ch.fluxron.fluxronapp.events.modelUi.authenticationOperations;

import ch.fluxron.fluxronapp.events.base.ITypedCallback;
import ch.fluxron.fluxronapp.events.base.SynchronousReplyEvent;
import ch.fluxron.fluxronapp.objectBase.AccessLevel;

/**
 * Used to request the access level of a user synchronously.
 */
public class SynchronousAccessCommand extends SynchronousReplyEvent {

    /**
     * Instantiates a new SynchronousAccessCommand.
     *
     * @param callback
     */
    public SynchronousAccessCommand(ITypedCallback<AccessLevel> callback) {
        super(callback);
    }
}
