package ch.fluxron.fluxronapp.ui.fragments.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Base class for all device UI fragments. Provides common functionality.
 */
public class DeviceBaseFragment extends Fragment {
    private static final String STATE_ADDRESS = "address";
    private String deviceAddress;
    private String deviceClass;
    private IEventBusProvider provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            deviceAddress = savedInstanceState.getString(STATE_ADDRESS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_ADDRESS, deviceAddress);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setUIEventBus();
        provider.getUiEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        provider.getUiEventBus().unregister(this);
    }

    private void setUIEventBus() {
        provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider) getContext().getApplicationContext();
    }

    public void init(String address, String deviceClass) {
        this.deviceAddress = address;
        this.deviceClass = deviceClass;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }
}
