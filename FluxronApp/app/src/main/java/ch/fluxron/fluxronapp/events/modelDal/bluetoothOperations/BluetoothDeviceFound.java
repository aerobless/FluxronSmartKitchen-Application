package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import java.util.Date;

import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Notifies listeners that a bluetooth device has been found
 */
public class BluetoothDeviceFound {
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


