package ch.fluxron.fluxronapp.events.modelUi.authenticationOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.AccessLevel;

/**
 * Used to respond to a AccessCommand. Informs the UI what access level the current user has.
 */
public class AccessGranted extends RequestResponseConnection{
    AccessLevel accessLevel;

    public AccessGranted(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }
}
