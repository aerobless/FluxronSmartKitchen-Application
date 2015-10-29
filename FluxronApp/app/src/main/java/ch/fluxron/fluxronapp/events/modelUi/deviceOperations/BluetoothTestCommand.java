package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

/**
 * Used for testing bluetooth functionality, will be removed later on.
 */
public class BluetoothTestCommand {
    String deviceID;

    public BluetoothTestCommand(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceID() {
        return deviceID;
    }
}
