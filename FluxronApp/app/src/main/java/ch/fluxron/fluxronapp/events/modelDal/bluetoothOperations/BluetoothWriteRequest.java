package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

/**
 * Sends a read request to a specific bluetooth device.
 */
public class BluetoothWriteRequest {
    private String address;
    private String field;
    private Object value;

    public BluetoothWriteRequest(String address, String field, int value) {
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
