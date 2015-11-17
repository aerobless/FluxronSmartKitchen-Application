package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to request pairing with a bluetooth device.
 */
public class BluetoothBondingCommand extends RequestResponseConnection {
    private String address;

    public BluetoothBondingCommand(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
