package ch.fluxron.fluxronapp.objectBase;

/**
 * A user of this application
 */
public class User {
    String username;
    String password;
    AccessLevel accessLevel;

    /**
     * Creates a new user
     * @param username Username
     * @param password Password
     * @param accessLevel Access level
     */
    public User(String username, String password, AccessLevel accessLevel) {
        this.username = username;
        this.password = password;
        this.accessLevel = accessLevel;
    }

    /**
     * Gets the username
     * @return Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password
     * @return Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the access level
     * @return Acess level
     */
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }
}
