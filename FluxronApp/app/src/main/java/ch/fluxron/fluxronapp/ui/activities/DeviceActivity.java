package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;
import android.view.View;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;


public class DeviceActivity extends FluxronBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
    }

    public void onEventMainThread(Object msg){
        // TODO: Add event handling for device activity
        postMessage(null);
    }

    public void onBackButtonClicked(View button){
        // Close this activity and navigate back to the activity
        // that is below on the stack.
        finish();
    }
}
