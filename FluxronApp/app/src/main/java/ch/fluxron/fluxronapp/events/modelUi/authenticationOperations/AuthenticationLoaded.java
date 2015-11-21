package ch.fluxron.fluxronapp.events.modelUi.authenticationOperations;

/**
 * Used to reply to a LoadAuthenticationCommand. Provides authentication information for the UI.
 */
public class AuthenticationLoaded {
    String username;
    String password;

    public AuthenticationLoaded(String username, String password) {
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
