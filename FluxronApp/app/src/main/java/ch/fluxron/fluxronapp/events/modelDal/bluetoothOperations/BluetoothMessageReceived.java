package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * A raw byte message that was received by BTConnectionThread.
 */
public class BluetoothMessageReceived extends RequestResponseConnection{
    String address;
    byte[] data;

    public BluetoothMessageReceived(String address, byte[] data) {
        this.address = address;
        this.data = data;
    }

    public String getAddress() {
        return address;
    }

    public byte[] getData() {
        return data;
    }
}
