package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;

/**
 * Activity to change application settings, such as username & password.
 */
public class ApplicationSettingsActivity extends FluxronBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_settings);
    }

}
