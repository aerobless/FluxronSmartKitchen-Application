package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.data.RequestError;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to communicate a failed request. This means that we were able
 * to communicate with the device, but the device sent a 80-Error message
 * as response instead of a write confirmation or the requested read data.
 */
public class BluetoothRequestFailed extends RequestResponseConnection {
    RequestError error;
    String address;
    String field;

    /**
     * Instantiate a new BluetoothRequestFailed event.
     *
     * @param error
     * @param address
     * @param field
     */
    public BluetoothRequestFailed(RequestError error, String address, String field) {
        this.error = error;
        this.address = address;
        this.field = field;
    }

    /**
     * Gets the error contained by this event.
     *
     * @return
     */
    public RequestError getError() {
        return error;
    }

    /**
     * Sets a RequestError.
     *
     * @param error
     */
    public void setError(RequestError error) {
        this.error = error;
    }

    /**
     * Gets the device address.
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

    /**
     * Get the field/parameter where the error occurred.
     *
     * @return
     */
    public String getField() {
        return field;
    }

    /**
     * Set the field/parameter where the error occurred.
     *
     * @param field
     */
    public void setField(String field) {
        this.field = field;
    }
}
