package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Sends a read request to a specific bluetooth device.
 */
public class BluetoothReadRequest extends RequestResponseConnection{
    private String address;
    private String field;

    public BluetoothReadRequest(String address, String field) {
        this.address = address;
        this.field = field;
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
