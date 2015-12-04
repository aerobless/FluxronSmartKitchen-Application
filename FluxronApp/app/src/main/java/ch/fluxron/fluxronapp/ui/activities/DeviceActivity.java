package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.CyclicRefreshCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceFailed;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.DeviceFragmentAdapter;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Displays the status and history views of a device
 */
public class DeviceActivity extends FluxronBaseActivity {
    private IEventBusProvider provider;
    private String address = "Unknown";
    private String deviceClass = Device.UNKNOWN_DEVICE_CLASS;
    private String name = "Unknown";
    private LinearLayout progressDeviceClass;
    private TextView deviceStatusMessage;

    /**
     * Creates this activity
     * @param savedInstanceState State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            address = extras.getString("DEVICE_ID");
            name = extras.getString("DEVICE_NAME");
        }

        provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider) getApplicationContext();

        ((TextView) findViewById(R.id.deviceStatusName)).setText(name);
        ((TextView) findViewById(R.id.deviceStatusDescription)).setText(address);
        deviceStatusMessage = ((TextView) findViewById(R.id.deviceStatusMessage));
        progressDeviceClass = (LinearLayout) findViewById(R.id.progressDeviceClass);
        progressDeviceClass.setVisibility(View.VISIBLE);
        provider.getUiEventBus().post(new CyclicRefreshCommand(address));
    }

    /**
     * Sets the status of the device to OK whenever it receives a DeviceChange message.
     *
     * @param inputMsg Message
     */
    public void onEventMainThread(DeviceChanged inputMsg) {
        if (inputMsg.getDevice().getAddress().equals(address)) {
            TextView statusOrb = (TextView) findViewById(R.id.statusOrb);
            statusOrb.setText(R.string.ok_check);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            deviceStatusMessage.setText(getResources().getString(R.string.device_status_connected)+" "+dateFormat.format(inputMsg.getDevice().getLastContact()));
            statusOrb.setBackground(getResources().getDrawable(R.drawable.status_ok_background));
            if(deviceClass.equals(Device.UNKNOWN_DEVICE_CLASS) && !inputMsg.getDevice().getDeviceClass().equals(Device.UNKNOWN_DEVICE_CLASS)){
                deviceClass = inputMsg.getDevice().getDeviceClass();
                progressDeviceClass.setVisibility(View.GONE);
                setupViewPagerTabs();
            }
        }
    }

    /**
     * Sets up the tab control
     */
    private void setupViewPagerTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.deviceViewPager);
        DeviceFragmentAdapter dfa = new DeviceFragmentAdapter(getSupportFragmentManager(), this);
        dfa.init(address, deviceClass);
        viewPager.setAdapter(dfa);

        TabLayout tabs = (TabLayout) findViewById(R.id.deviceTabs);
        tabs.setupWithViewPager(viewPager);
    }

    /**
     * Sets the status of the device to failure whenever it receives a DeviceFailed message.
     *
     * @param inputMsg Message
     */
    public void onEventMainThread(DeviceFailed inputMsg) {
        if (inputMsg.getAddress().equals(address)) {
            TextView statusOrb = (TextView) findViewById(R.id.statusOrb);
            statusOrb.setText(getResources().getString(R.string.fail_check));
            deviceStatusMessage.setText(R.string.device_status_failed);
            statusOrb.setBackground(getResources().getDrawable(R.drawable.status_failure_background));
        }
    }
}
