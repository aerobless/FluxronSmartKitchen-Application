package ch.fluxron.fluxronapp.events.modelUi.authenticationOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to request authentication of a user.
 */
public class AuthenticationCommand extends RequestResponseConnection{
    String username;
    String password;

    public AuthenticationCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
