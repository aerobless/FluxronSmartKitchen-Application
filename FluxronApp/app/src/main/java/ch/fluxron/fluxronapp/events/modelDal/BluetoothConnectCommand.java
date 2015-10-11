package ch.fluxron.fluxronapp.events.modelDal;

/**
 * Sends a command to connect to a specific bluetooth device.
 */
public class BluetoothConnectCommand {
    private String name;
    private String address;

    public BluetoothConnectCommand(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
