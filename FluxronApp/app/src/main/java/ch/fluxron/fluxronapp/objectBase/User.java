package ch.fluxron.fluxronapp.objectBase;

/**
 * A value object for users of this application.
 */
public class User {
    String username;
    String password;
    AccessLevel accessLevel;

    /**
     * Instantiates a new, empty user.
     */
    public User() {
    }

    /**
     * Instantiates a new user containing the most important values.
     *
     * @param username
     * @param password
     * @param accessLevel
     */
    public User(String username, String password, AccessLevel accessLevel) {
        this.username = username;
        this.password = password;
        this.accessLevel = accessLevel;
    }

    /**
     * Returns the username of this user.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of this user.
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password of this user.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of this user.
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the access level of this user.
     *
     * @return
     */
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    /**
     * Sets the access level of this user.
     *
     * @param accessLevel
     */
    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }
}
