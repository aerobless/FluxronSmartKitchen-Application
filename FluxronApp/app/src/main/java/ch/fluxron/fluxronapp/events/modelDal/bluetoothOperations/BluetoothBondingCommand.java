package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to request pairing with a bluetooth device.
 */
public class BluetoothBondingCommand extends RequestResponseConnection {
    private String address;

    /**
     * Instantiates a new BluetoothBondingCommand.
     *
     * @param address
     */
    public BluetoothBondingCommand(String address) {
        this.address = address;
    }

    /**
     * Returns the address of the device..
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the device.
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
