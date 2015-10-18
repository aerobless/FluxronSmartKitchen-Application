package ch.fluxron.fluxronapp.model;

/**
 * A model for a bluetooth device.
 */
public class Device {
    String name;
    String address;
    int lastState;

    public Device(String name, String address) {
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

    public int getLastState() {
        return lastState;
    }

    public void setLastState(int lastState) {
        this.lastState = lastState;
    }
}
