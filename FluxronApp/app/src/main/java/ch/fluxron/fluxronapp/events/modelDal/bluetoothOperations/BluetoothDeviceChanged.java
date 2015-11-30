package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * The bluetooth response of a device.
 */
public class BluetoothDeviceChanged extends RequestResponseConnection {
    String address;
    String field;
    int value;

    /**
     * Instantiates a new BluetoothDeviceChanged event.
     *
     * @param address
     * @param field
     * @param value
     */
    public BluetoothDeviceChanged(String address, String field, int value) {
        this.address = address;
        this.field = field;
        this.value = value;
    }

    /**
     * Returns the device address.
     *
     * @return address
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
     * Returns the field/parameter.
     *
     * @return field
     */
    public String getField() {
        return field;
    }

    /**
     * Sets the field/parameter.
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * Gets the value of the field/parameter.
     *
     * @return value
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value of the field/parameter.
     */
    public void setValue(int value) {
        this.value = value;
    }
}
