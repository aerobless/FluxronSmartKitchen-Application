package ch.fluxron.fluxronapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A model for a bluetooth device.
 */
public class Device {
    String name;

    @JsonProperty("_id")
    String address;

    public Device() {
    }

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
}
