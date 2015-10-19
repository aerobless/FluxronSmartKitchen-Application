package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

/**
 * A raw byte message that was received by BTConnectionThread.
 */
public class BluetoothMessageReceived {
    String address;
    byte[] message;

    public BluetoothMessageReceived(String address, byte[] message) {
        this.address = address;
        this.message = message;
    }

    public String getAddress() {
        return address;
    }

    public byte[] getMessage() {
        return message;
    }
}
