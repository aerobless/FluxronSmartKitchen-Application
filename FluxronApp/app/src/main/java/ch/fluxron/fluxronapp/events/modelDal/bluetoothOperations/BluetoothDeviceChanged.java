package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * The bluetooth response of a device.
 */
public class BluetoothDeviceChanged extends RequestResponseConnection{
    String address;
    String field;
    int value;

    public BluetoothDeviceChanged(String address, String field, int value) {
        this.address = address;
        this.field = field;
        this.value = value;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
