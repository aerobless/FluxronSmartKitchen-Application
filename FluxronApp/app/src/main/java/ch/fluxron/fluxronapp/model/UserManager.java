package ch.fluxron.fluxronapp.model;

import java.util.HashMap;

import ch.fluxron.fluxronapp.events.modelDal.objectOperations.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AccessCommand;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AccessGranted;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AuthenticationCommand;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AuthenticationLoaded;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.LoadAuthenticationCommand;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.UserAuthenticated;
import ch.fluxron.fluxronapp.objectBase.AccessLevel;
import ch.fluxron.fluxronapp.objectBase.User;

/**
 * Manages users that can be used to restrict access to certain developer options & parameters.
 * Please be aware that this is a naive implementation and does not provide real security against
 * a malevolent hacker who decompiles this application. Such a hacker would in any case find the
 * default password "1234" of the Fluxron devices and could do whatever he wants. To provide real
 * security is not possible without firmware changes of the devices and therefor is not part of this
 * bachelor thesis.
 * The intent of this security solution is simply to prevent service technicians and other non Fluxron
 * staff to _accidentally_ change settings that might damage the device.
 */
public class UserManager {
    private HashMap<String, User> users;
    private User currentUser = null;
    private Object userLock = new Object();
    private IEventBusProvider provider;
    private static final String USER_DATA = "user_data";

    public UserManager(IEventBusProvider provider) {
        users = new HashMap<>();
        users.put("demo", new User("demo", "demo", AccessLevel.DEMO_USER));
        users.put("user", new User("user", "1234", AccessLevel.AUTHENTICATED_USER));
        users.put("developer", new User("developer", "fluxronDev", AccessLevel.DEVELOPER));

        this.provider = provider;
        provider.getUiEventBus().register(this);
        provider.getDalEventBus().register(this);
    }

    /**
     * Load the currently authenticated user from the DB
     */
    public void onEventAsync(LoadAuthenticationCommand inputCmd) {
        LoadObjectByIdCommand outputCmd = new LoadObjectByIdCommand(USER_DATA);
        outputCmd.setConnectionId(inputCmd);
        provider.getDalEventBus().post(outputCmd);
    }

    /**
     * Relays loaded user information to the UI.
     *
     * @param inputCmd
     */
    public void onEventAsync(ObjectLoaded inputCmd) {
        if (inputCmd.getId().equals(USER_DATA)) {
            User user = (User) inputCmd.getData();
            synchronized (userLock) {
                currentUser = user;
            }
            provider.getUiEventBus().post(new AuthenticationLoaded(user.getUsername(), user.getPassword()));
        }
    }

    /**
     * Responds to AuthenticationCommands and tells to UI whether the login information is correct
     *
     * @param cmd
     */
    public void onEventAsync(AuthenticationCommand cmd) {
        UserAuthenticated authResponse = new UserAuthenticated(cmd.getUsername(), isAuthenticated(cmd.getUsername(), cmd.getPassword()));
        authResponse.setConnectionId(cmd);
        provider.getUiEventBus().post(authResponse);
    }

    /**
     * Responds to AccessCommands with the currently active users access level. If there is no
     * user logged in the lowest possible level (DEMO_USER) is returned.
     *
     * @param cmd
     */
    public void onEventAsync(AccessCommand cmd) {
        AccessGranted accessGranted;
        if (currentUser != null) {
            accessGranted = new AccessGranted(currentUser.getAccessLevel());
        } else {
            //Return the lowest user level if no user is authenticated.
            accessGranted = new AccessGranted(AccessLevel.DEMO_USER);
        }
        accessGranted.setConnectionId(cmd);
        provider.getUiEventBus().post(accessGranted);
    }

    /**
     * Checks whether the login information exists in the user manager
     * if it does it stores the currently authenticated user in the DB
     *
     * @param username
     * @param password
     * @return
     */
    private boolean isAuthenticated(String username, String password) {
        User user = users.get(username);
        if (user != null) {
            synchronized (userLock) {
                currentUser = user;
            }
            saveToDB(user);
            return user.getPassword().equals(password);
        }
        return false;
    }

    /**
     * Save the authenticated user data in the DB
     *
     * @param user
     */
    private void saveToDB(User user) {
        SaveObjectCommand cmd = new SaveObjectCommand();
        cmd.setData(user);
        cmd.setDocumentId(USER_DATA);
        provider.getDalEventBus().post(cmd);
    }

}
