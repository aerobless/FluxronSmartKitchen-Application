package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Sends a read request to a specific bluetooth device.
 */
public class BluetoothWriteRequest extends RequestResponseConnection {
    private String address;
    private String field;
    private Object value;

    /**
     * Instantiates a new BluetoothWriteRequest.
     *
     * @param address
     * @param field
     * @param value
     */
    public BluetoothWriteRequest(String address, String field, Object value) {
        this.address = address;
        this.field = field;
        this.value = value;
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
     * Gets the field/parameter.
     *
     * @return
     */
    public String getField() {
        return field;
    }

    /**
     * Sets the field/parameter.
     *
     * @param field
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * Gets the value that should be sent over bluetooth.
     *
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value that should be sent over bluetooth.
     *
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }
}
