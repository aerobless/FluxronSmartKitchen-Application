package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

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
     * Returns the address contained by this BluetoothBondingCommand.
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address to be contained by this BluetoothBondingCommand.
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
