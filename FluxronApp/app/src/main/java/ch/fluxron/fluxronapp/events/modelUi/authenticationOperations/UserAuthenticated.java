package ch.fluxron.fluxronapp.events.modelUi.authenticationOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to notify the UI that a user has been authenticated (can be successful or unsuccessful)
 */
public class UserAuthenticated extends RequestResponseConnection {
    String username;
    boolean authenticated;

    /**
     * Instantiates a new UserAuthenticated event.
     *
     * @param username
     * @param authenticated
     */
    public UserAuthenticated(String username, boolean authenticated) {
        this.username = username;
        this.authenticated = authenticated;
    }

    /**
     * Returns the username contained by this UserAuthenticated event.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns true/false based on whether the user has been authenticated or not. True if authenticated.
     *
     * @return authentication status
     */
    public boolean isAuthenticated() {
        return authenticated;
    }
}
