package ch.fluxron.fluxronapp.events.modelUi.authenticationOperations;

/**
 * Used to reply to a LoadAuthenticationCommand. Provides authentication information for the UI.
 */
public class AuthenticationLoaded {
    String username;
    String password;

    /**
     * Instantiates a new AuthenticationLoaded event.
     *
     * @param username
     * @param password
     */
    public AuthenticationLoaded(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the username contained by this AuthenticationLoaded event.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username to be contained by this AuthenticationLoaded event.
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password contained by this AuthenticationLoaded event.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password contained by this AuthenticationLoaded event.
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
