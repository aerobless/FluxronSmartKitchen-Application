package ch.fluxron.fluxronapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ch.fluxron.fluxronapp.R;


public class KitchenActivity extends AppCompatActivity {
    ch.fluxron.fluxronapp.ui.util.IEventBusProvider busProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);
    }

    @Override
    public void onStart() {
        super.onStart();

        busProvider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider)getApplication();
        busProvider.getUiEventBus().register(this);
    }

    @Override
    public void onStop() {
        busProvider.getUiEventBus().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(Object msg){
        busProvider.getUiEventBus();
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
