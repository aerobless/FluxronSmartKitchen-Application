package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to communicate a connection failure to the DeviceManager.
 */
public class BluetoothConnectionFailure extends RequestResponseConnection{
    public enum FailureType{
        GENERIC_CONECTION_FAILURE, DEVICE_NOT_FOUND, DEVICE_FOUND_UNABLE_TO_CONNECT
    }
    FailureType failuretype = FailureType.GENERIC_CONECTION_FAILURE;

    public BluetoothConnectionFailure() {
    }

    public BluetoothConnectionFailure(FailureType failuretype) {
        this.failuretype = failuretype;
    }

    public FailureType getFailuretype() {
        return failuretype;
    }

    public void setFailuretype(FailureType failuretype) {
        this.failuretype = failuretype;
    }
}
