package ch.fluxron.fluxronapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.LoadKitchenCommand;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;


public class KitchenActivity extends FluxronBaseActivity {
    public static final String PARAM_KITCHEN_ID = "KITCHEN_ID";
    private String kitchenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        kitchenId = getIntent().getExtras().getString(PARAM_KITCHEN_ID);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Request the load of the kitchen we are displaying
        postMessage(new LoadKitchenCommand(kitchenId));
    }

    public void onEventMainThread(KitchenLoaded msg){
        if(msg.getKitchen().getId().equals(kitchenId)) {
            ((TextView) findViewById(R.id.textView2)).setText(msg.getKitchen().getName());
        }
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
