package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Notifies listeners that a bluetooth device has been found
 */
public class BluetoothDeviceFound extends RequestResponseConnection {
    private Device device;

    /**
     * Instantiates a new BluetoothDeviceFound event.
     *
     * @param device
     */
    public BluetoothDeviceFound(Device device) {
        this.device = device;
    }

    /**
     * Returns the device value object.
     *
     * @return device
     */
    public Device getDevice() {
        return device;
    }

    /**
     * Sets the device value object.
     *
     * @param device
     */
    public void setDevice(Device device) {
        this.device = device;
    }
}


