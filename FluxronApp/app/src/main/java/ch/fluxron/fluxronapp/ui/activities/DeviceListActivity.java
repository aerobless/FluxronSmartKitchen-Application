package ch.fluxron.fluxronapp.ui.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.model.Device;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.IDeviceClickListener;
import ch.fluxron.fluxronapp.ui.fragments.DeviceListFragment;

/**
 * Temporary activity, created to work on the deviceList fragment. Can be removed once
 * the fragment is working and added to its correct activity.
 */
public class DeviceListActivity extends FluxronBaseActivity implements IDeviceClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicelist);


        // Initialize the area fragment
        DeviceListFragment fragment = new DeviceListFragment();
        fragment.setEventBusProvider(this.busProvider);
        fragment.setClickListener(this);

        // Fade in the fragment for area
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.deviceListFragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack("blub");
        ft.commit();
    }

    public void onEventMainThread(Object msg){
        // TODO: Add event handling for device activity
       // postMessage(null);
    }

    public void onBackButtonClicked(View button){
        // Close this activity and navigate back to the activity
        // that is below on the stack.
        finish();
    }

    @Override
    public void deviceClicked(Device d) {
        Log.d("Fluxron", "CLICKED!!");
    }
}
