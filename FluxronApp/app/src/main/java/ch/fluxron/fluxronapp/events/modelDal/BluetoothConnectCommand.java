package ch.fluxron.fluxronapp.events.modelDal;

/**
 * Sends a command to connect to a specific bluetooth device.
 */
public class BluetoothConnectCommand {
    private String address;

    public BluetoothConnectCommand(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
