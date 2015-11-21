package ch.fluxron.fluxronapp.events.modelUi.authenticationOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to notify the UI that a user has been authenticated (can be successful or unsuccessful)
 */
public class UserAuthenticated extends RequestResponseConnection {
    String username;
    boolean authenticated;

    public UserAuthenticated(String username, boolean authenticated) {
        this.username = username;
        this.authenticated = authenticated;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
