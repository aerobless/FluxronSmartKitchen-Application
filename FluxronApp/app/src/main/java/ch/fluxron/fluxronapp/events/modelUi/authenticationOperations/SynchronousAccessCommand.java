package ch.fluxron.fluxronapp.events.modelUi.authenticationOperations;

import java.io.InputStream;
import java.util.Map;

import ch.fluxron.fluxronapp.events.base.ITypedCallback;
import ch.fluxron.fluxronapp.events.base.SynchronousReplyEvent;
import ch.fluxron.fluxronapp.objectBase.AccessLevel;

/**
 * Used to request the access level of a user
 */
public class SynchronousAccessCommand extends SynchronousReplyEvent{
    public SynchronousAccessCommand(ITypedCallback<AccessLevel> callback) {
        super(callback);
    }
}
