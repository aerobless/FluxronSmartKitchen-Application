package ch.fluxron.fluxronapp.model;

import java.util.HashMap;

import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AuthenticationCommand;
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
    private IEventBusProvider provider;

    public UserManager(IEventBusProvider provider) {
        users = new HashMap<>();
        users.put("demo", new User("demo", "demo", AccessLevel.DEMO_USER));
        users.put("user", new User("user", "1234", AccessLevel.AUTHENTICATED_USER));
        users.put("developer", new User("developer", "fluxronDev", AccessLevel.AUTHENTICATED_USER));

        this.provider = provider;
        provider.getUiEventBus().register(this);
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
     * Checks whether the login information exists in the user manager
     *
     * @param username
     * @param password
     * @return
     */
    private boolean isAuthenticated(String username, String password) {
        User user = users.get(username);
        if (user != null) {
            return user.getPassword().equals(password);
        }
        return false;
    }
}
