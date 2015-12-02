package ch.fluxron.fluxronapp.events.modelUi.authenticationOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.AccessLevel;

/**
 * Used to respond to a AccessCommand. Informs the UI what access level the current user has.
 */
public class AccessGranted extends RequestResponseConnection {
    AccessLevel accessLevel;

    /**
     * Instantiates a new AccessGranted event.
     *
     * @param accessLevel
     */
    public AccessGranted(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    /**
     * Returns the access contained by this event.
     *
     * @return accessLevel
     */
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    /**
     * Sets the access level to be contained by this event.
     *
     * @param accessLevel
     */
    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }
}
