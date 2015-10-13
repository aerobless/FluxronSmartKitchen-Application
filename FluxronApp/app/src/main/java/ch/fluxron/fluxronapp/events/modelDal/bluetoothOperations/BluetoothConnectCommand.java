package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

/**
 * Sends a command to connect to a specific bluetooth device.
 */
public class BluetoothConnectCommand {
    private String address;
    private byte[] message;

    public BluetoothConnectCommand(String address, byte[] message) {
        this.address = address;
        this.message = message;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
}
