package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to notify the UI that the conenction to a device has failed.
 */
public class DeviceFailed extends RequestResponseConnection {
    String address;

    public DeviceFailed(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
