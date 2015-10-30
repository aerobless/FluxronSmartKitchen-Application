package ch.fluxron.fluxronapp.objectBase;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A model for a bluetooth device.
 */
public class Device {
    String name;
    String category;

    @JsonProperty("_id")
    String address;

    public Device() {
    }

    public Device(String name, String address, String category) {
        this.name = name;
        this.address = address;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
