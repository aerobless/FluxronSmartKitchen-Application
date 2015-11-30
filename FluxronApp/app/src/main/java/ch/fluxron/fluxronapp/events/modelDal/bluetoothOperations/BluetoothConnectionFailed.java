package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to communicate a connection failure to the DeviceManager.
 */
public class BluetoothConnectionFailed extends RequestResponseConnection {
    public enum FailureType {
        GENERIC_CONECTION_FAILURE, DEVICE_NOT_FOUND, DEVICE_FOUND_UNABLE_TO_CONNECT
    }

    FailureType failuretype = FailureType.GENERIC_CONECTION_FAILURE;
    String address;

    /**
     * Instantiates a new BluetoothConnectionFailed event.
     *
     * @param failuretype
     * @param address
     */
    public BluetoothConnectionFailed(FailureType failuretype, String address) {
        this.failuretype = failuretype;
        this.address = address;
    }

    /**
     * Returns the failure type.
     *
     * @return
     */
    public FailureType getFailuretype() {
        return failuretype;
    }

    /**
     * Sets the failure type.
     *
     * @param failuretype
     */
    public void setFailuretype(FailureType failuretype) {
        this.failuretype = failuretype;
    }

    /**
     * Returns the device address.
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the device address.
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
