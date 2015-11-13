package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.CyclicRefreshCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceFailed;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.DeviceFragmentAdapter;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

public class DeviceActivity extends FluxronBaseActivity {
    private IEventBusProvider provider;
    private String address = "Unkown";
    private String name = "Unkown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            address = extras.getString("DEVICE_ID");
            name = extras.getString("DEVICE_NAME");
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.deviceViewPager);
        DeviceFragmentAdapter dfa = new DeviceFragmentAdapter(getSupportFragmentManager(), this);
        dfa.setAddress(address);
        viewPager.setAdapter(dfa);

        TabLayout tabs = (TabLayout)findViewById(R.id.deviceTabs);
        tabs.setupWithViewPager(viewPager);
        provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider)getApplicationContext();

        ((TextView)findViewById(R.id.deviceStatusName)).setText(name);
        ((TextView)findViewById(R.id.deviceStatusDescription)).setText(address);
        provider.getUiEventBus().post(new CyclicRefreshCommand(address));
    }

    public void onEventMainThread(Object msg){
        // TODO: Add event handling for device activity
        postMessage(null);
    }

    public void onBackButtonClicked(View button){
        // Close this activity and navigate back to the activity
        // that is below on the stack.
        provider.getUiEventBus().post(new CyclicRefreshCommand(CyclicRefreshCommand.ALL_DEVICES));
        finish();
    }

    /**
     * Sets the status of the device to OK whenever it receives a DeviceChange message.
     * @param inputMsg
     */
    public void onEventMainThread(DeviceChanged inputMsg){
        if(inputMsg.getDevice().getAddress().equals(address)){
            TextView statusOrb = (TextView)findViewById(R.id.statusOrb);
            statusOrb.setText(R.string.ok_check);
            statusOrb.setBackground(getResources().getDrawable(R.drawable.status_ok_background));
        }
    }

    /**
     * Sets the status of the device to failure whenever it receives a DeviceFailed message.
     * @param inputMsg
     */
    public void onEventMainThread(DeviceFailed inputMsg){
        if(inputMsg.getAddress().equals(address)){
            TextView statusOrb = (TextView)findViewById(R.id.statusOrb);
            statusOrb.setText(R.string.fail_check);
            statusOrb.setBackground(getResources().getDrawable(R.drawable.status_failure_background));
        }
    }
}
