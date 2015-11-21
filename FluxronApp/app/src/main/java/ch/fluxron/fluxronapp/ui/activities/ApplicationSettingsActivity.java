package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AuthenticationCommand;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.UserAuthenticated;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;

/**
 * Activity to change application settings, such as username & password.
 */
public class ApplicationSettingsActivity extends FluxronBaseActivity {
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_settings);
        username = (EditText) findViewById(R.id.editTextUsername);
        password = (EditText) findViewById(R.id.editTextPassword);
    }

    public void onSaveButtonClicked(View button) {
        String user = username.getText().toString();
        String pw = password.getText().toString();
        if (!"".equals(user) && !"".equals(pw)) {
            busProvider.getUiEventBus().post(new AuthenticationCommand(user, pw));
        } else if ("".equals(user + pw)) {
            showToast(getResources().getString(R.string.emptyUserAndPassword));
        } else if ("".equals(user)) {
            showToast(getResources().getString(R.string.emptyUser));
        } else if ("".equals(pw)) {
            showToast(getResources().getString(R.string.emptyPassword));
        }
    }

    public void onEventMainThread(UserAuthenticated inputMsg) {
        if(inputMsg.isAuthenticated()){
            showToast("Successfully authenticated!");
        } else {
            showToast("Wrong username or password!");
        }
    }
}
