package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used for testing bluetooth functionality, will be removed later on.
 */
public class BluetoothTestCommand extends RequestResponseConnection{
    String deviceID;

    public BluetoothTestCommand(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceID() {
        return deviceID;
    }
}
