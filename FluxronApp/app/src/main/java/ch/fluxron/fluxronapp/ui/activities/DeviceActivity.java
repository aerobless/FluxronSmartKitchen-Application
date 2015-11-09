package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.DeviceFragmentAdapter;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

public class DeviceActivity extends FluxronBaseActivity {
    private IEventBusProvider provider;
    private String address = "Unkown";
    private String type = "Unkown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        ViewPager viewPager = (ViewPager) findViewById(R.id.deviceViewPager);
        viewPager.setAdapter(new DeviceFragmentAdapter(getSupportFragmentManager(), this));

        TabLayout tabs = (TabLayout)findViewById(R.id.deviceTabs);
        tabs.setupWithViewPager(viewPager);
        provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider)getApplicationContext();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            address = extras.getString("DEVICE_ID");
            type = extras.getString("DEVICE_TYPE");
        }

        ((TextView)findViewById(R.id.deviceStatusName)).setText(type); //TODO: replace with Device Name once we have it
        ((TextView)findViewById(R.id.deviceStatusDescription)).setText(address);
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

    public void onSampleText(View btn){
        //TemperatureBar tb = (TemperatureBar) findViewById(R.id.barTemp);
        //tb.setMinMax(0,0);
    }

    /**
     * Sets the full name instead of just the type in the header.
     * @param inputMsg
     */
    public void onEventMainThread(DeviceChanged inputMsg){
        if(inputMsg.getDevice().getAddress().equals(address)){
            ((TextView)findViewById(R.id.deviceStatusName)).setText(inputMsg.getDevice().getName());
            ((TextView)findViewById(R.id.statusOrb)).setText(R.string.ok_check);
        }
    }
}
