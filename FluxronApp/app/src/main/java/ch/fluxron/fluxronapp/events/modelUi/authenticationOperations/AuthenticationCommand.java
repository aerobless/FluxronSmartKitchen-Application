package ch.fluxron.fluxronapp.events.modelUi.authenticationOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to request authentication of a user.
 */
public class AuthenticationCommand extends RequestResponseConnection {
    String username;
    String password;

    /**
     * Instantiates a new AuthenticationCommand.
     *
     * @param username
     * @param password
     */
    public AuthenticationCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the username contained by this AuthenticationCommand.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username to be contained by this AuthenticationCommand.
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password contained by this AuthenticationCommand.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password to be contained by this AuthenticationCommand.
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
