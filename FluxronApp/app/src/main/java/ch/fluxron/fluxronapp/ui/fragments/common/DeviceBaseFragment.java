package ch.fluxron.fluxronapp.ui.fragments.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Base class for all device UI fragments. Provides common functionality.
 */
public class DeviceBaseFragment extends Fragment {
    protected static final String STATE_ADDRESS = "address";
    protected static final String STATE_DEVICE_CLASS = "class";
    private String deviceAddress;
    private String deviceClass;
    protected IEventBusProvider provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            deviceAddress = savedInstanceState.getString(STATE_ADDRESS);
            deviceClass = savedInstanceState.getString(STATE_DEVICE_CLASS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_ADDRESS, deviceAddress);
        outState.putString(STATE_DEVICE_CLASS, deviceClass);
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

    /**
     * Sets the UI event bus.
     */
    private void setUIEventBus() {
        provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider) getContext().getApplicationContext();
    }

    /**
     * Initializes address and device class.
     *
     * @param address
     * @param deviceClass
     */
    public void init(String address, String deviceClass) {
        this.deviceAddress = address;
        this.deviceClass = deviceClass;
    }

    /**
     * Returns the device address.
     *
     * @return device address
     */
    public String getDeviceAddress() {
        return deviceAddress;
    }

    /**
     * Returns the device class.
     *
     * @return device class
     */
    public String getDeviceClass() {
        return deviceClass;
    }

    /**
     * Sets the device address.
     *
     * @param deviceAddress
     */
    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    /**
     * Sets the device class.
     *
     * @param deviceClass
     */
    public void setDeviceClass(String deviceClass) {
        this.deviceClass = deviceClass;
    }
}
