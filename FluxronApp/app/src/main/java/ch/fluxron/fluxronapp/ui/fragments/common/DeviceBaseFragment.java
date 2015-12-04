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

    /**
     * Creates the fragment
     * @param savedInstanceState State
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            deviceAddress = savedInstanceState.getString(STATE_ADDRESS);
            deviceClass = savedInstanceState.getString(STATE_DEVICE_CLASS);
        }
    }

    /**
     * Saves the instance state
     * @param outState State
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_ADDRESS, deviceAddress);
        outState.putString(STATE_DEVICE_CLASS, deviceClass);
        super.onSaveInstanceState(outState);
    }

    /**
     * Starts the fragment
     */
    @Override
    public void onStart() {
        super.onStart();
        setUIEventBus();
        provider.getUiEventBus().register(this);
    }

    /**
     * Stops the fragment
     */
    @Override
    public void onStop() {
        super.onStop();
        provider.getUiEventBus().unregister(this);
    }

    /**
     * Gets the event bus
     */
    private void setUIEventBus() {
        provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider) getContext().getApplicationContext();
    }

    /**
     * Initializes this fragment with the device values
     * @param address Address
     * @param deviceClass Class name
     */
    public void init(String address, String deviceClass) {
        this.deviceAddress = address;
        this.deviceClass = deviceClass;
    }

    /**
     * Gets the device address
     * @return Device address
     */
    public String getDeviceAddress() {
        return deviceAddress;
    }

    /**
     * Gets the device class
     * @return Device class name
     */
    public String getDeviceClass(){
        return deviceClass;
    }

    /**
     * Sets the device address
     * @param deviceAddress Device address
     */
    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    /**
     * Sets the device class
     * @param deviceClass Device class
     */
    public void setDeviceClass(String deviceClass) {
        this.deviceClass = deviceClass;
    }
}
