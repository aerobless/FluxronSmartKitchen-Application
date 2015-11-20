package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.data.RequestError;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to communicate a failed request. This means that we were able
 * to communicate with the device, but the device sent a 80-Error message
 * as response instead of a write confirmation or the requested read data.
 */
public class BluetoothRequestFailed extends RequestResponseConnection{
    RequestError error;
    String address;
    String field;

    public BluetoothRequestFailed(RequestError error, String address, String field) {
        this.error = error;
        this.address = address;
        this.field = field;
    }

    public RequestError getError() {
        return error;
    }

    public void setError(RequestError error) {
        this.error = error;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
