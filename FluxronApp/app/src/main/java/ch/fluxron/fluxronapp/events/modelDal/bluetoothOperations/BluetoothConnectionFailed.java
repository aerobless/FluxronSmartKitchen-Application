package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to communicate a connection failure to the DeviceManager.
 */
public class BluetoothConnectionFailed extends RequestResponseConnection{
    public enum FailureType{
        GENERIC_CONECTION_FAILURE, DEVICE_NOT_FOUND, DEVICE_FOUND_UNABLE_TO_CONNECT
    }
    FailureType failuretype = FailureType.GENERIC_CONECTION_FAILURE;
    String address;

    public BluetoothConnectionFailed(FailureType failuretype, String address) {
        this.failuretype = failuretype;
        this.address = address;
    }

    public FailureType getFailuretype() {
        return failuretype;
    }

    public void setFailuretype(FailureType failuretype) {
        this.failuretype = failuretype;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
