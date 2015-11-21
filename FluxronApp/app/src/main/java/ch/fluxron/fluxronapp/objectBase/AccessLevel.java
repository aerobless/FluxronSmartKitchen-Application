package ch.fluxron.fluxronapp.objectBase;

/**
 * Access levels that a user can obtain by using the correct combination of username & password.
 * <p/>
 * Be sure to also update AccessLevel.xml if you make changes to the access levels.
 */
public enum AccessLevel {
    DEMO_USER, AUTHENTICATED_USER, DEVELOPER
}
