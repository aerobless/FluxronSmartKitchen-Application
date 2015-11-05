package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Notifies listeners that a bluetooth device has been found
 */
public class BluetoothDeviceFound extends RequestResponseConnection{
    private Device device;

    public BluetoothDeviceFound(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}


