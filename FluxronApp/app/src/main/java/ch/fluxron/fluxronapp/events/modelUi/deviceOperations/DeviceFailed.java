package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to notify the UI that the connection to a device has failed.
 */
public class DeviceFailed extends RequestResponseConnection {
    String address;

    /**
     * Instantiates a new DeviceFailed event.
     *
     * @param address
     */
    public DeviceFailed(String address) {
        this.address = address;
    }

    /**
     * Returns the address of the device that has failed.
     *
     * @return device address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the device that has failed.
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
