package ch.fluxron.fluxronapp.model;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;

import ch.fluxron.fluxronapp.events.modelDal.objectOperations.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AccessCommand;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AccessGranted;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AuthenticationCommand;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AuthenticationLoaded;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.LoadAuthenticationCommand;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.SynchronousAccessCommand;
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
    private final Object userLock = new Object();
    private IEventBusProvider provider;
    private static final String USER_DATA = "user_data";
    private static final String SALT = "ch.fluxron.fluxronapp.model.UserManager.SALT";

    /**
     * Instantiates a new UserManager.
     *
     * @param provider EventBus
     */
    public UserManager(IEventBusProvider provider) {
        /**
         * TODO: Fluxron should change the passwords of these example users.
         *
         * User: demo      Pw: demo
         * User: user      Pw: 1234
         * User: developer Pw: fluxronDev
         *
         * The password is a SHA256 hash value based on the SALT+password.
         * So "ch.fluxron.fluxronapp.model.UserManager.SALT"+"demo" equals "974758b327b4cb49c6e0d8e121267ddca7258f54".
         */
        users = new HashMap<>();
        users.put("demo", new User("demo", "d72d6aaa8c56313afdb46686d6feb87970040c0fc283f7ccadb56784e286afbd", AccessLevel.DEMO_USER));
        users.put("user", new User("user", "3ef35c03eb8ce650bcf94a0005a077089b222a233105791d4b07b1378b8f7fcc", AccessLevel.AUTHENTICATED_USER));
        users.put("developer", new User("developer", "668a0bfef4ea747d0f817075ca3badb57a73508caab09bf98762ee32bf280882", AccessLevel.DEVELOPER));

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
     * @param inputCmd ObjectLoaded event
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
     * @param cmd AuthenticationCommand
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
     * @param cmd AccessCommand
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

    public void onEventAsync(SynchronousAccessCommand inputCmd) {
        AccessLevel accessGranted;
        if (currentUser != null) {
            accessGranted = currentUser.getAccessLevel();
        } else {
            //Return the lowest user level if no user is authenticated.
            accessGranted = AccessLevel.DEMO_USER;
        }
        inputCmd.notifyCompletion(accessGranted);
    }

    /**
     * Checks whether the login information exists in the user manager
     * if it does it stores the currently authenticated user in the DB
     *
     * @param username Username
     * @param password Password
     * @return authentication success
     */
    private boolean isAuthenticated(String username, String password) {
        String passwordHash = DigestUtils.sha256Hex(SALT+password);
        User user = users.get(username);
        if (user != null) {
            synchronized (userLock) {
                currentUser = user;
            }
            saveToDB(user);
            return user.getPassword().equals(passwordHash);
        }
        return false;
    }

    /**
     * Save the authenticated user data in the DB
     *
     * @param user a User of this application
     */
    private void saveToDB(User user) {
        SaveObjectCommand cmd = new SaveObjectCommand();
        cmd.setData(user);
        cmd.setDocumentId(USER_DATA);
        provider.getDalEventBus().post(cmd);
    }

}
