package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * A raw byte message that was received by BluetoothConnectionThread.
 */
public class BluetoothMessageReceived extends RequestResponseConnection {
    String address;
    byte[] data;

    /**
     * Instantiates a new BluetoothMessageReceived event.
     *
     * @param address
     * @param data
     */
    public BluetoothMessageReceived(String address, byte[] data) {
        this.address = address;
        this.data = data;
    }

    /**
     * Gets the device address.
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the transmitted data.
     *
     * @return data byte[]
     */
    public byte[] getData() {
        return data;
    }
}
