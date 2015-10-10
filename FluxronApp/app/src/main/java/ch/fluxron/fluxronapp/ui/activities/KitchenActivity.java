package ch.fluxron.fluxronapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;


public class KitchenActivity extends FluxronBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);
    }

    public void onEventMainThread(Object msg){
        postMessage(null);
    }

    public void onBackButtonClicked(View button){
        // Close this kitchen and move back one view on the stack.
        finish();
    }

    public void onEditDeviceClicked(View button){
        // Edit this device
        Intent editDevice = new Intent(this, DeviceActivity.class);
        editDevice.putExtra("DEVICE_ID", "xxx-dsf-er22-34234-d00");
        startActivity(editDevice);
    }
}
