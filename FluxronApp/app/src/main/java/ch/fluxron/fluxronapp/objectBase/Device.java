package ch.fluxron.fluxronapp.objectBase;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A model for a bluetooth device.
 */
public class Device {
    String name;
    String category;
    boolean bonded;

    @JsonProperty("_id")
    String address;

    public Device() {
    }

    public Device(String name, String address, boolean bonded) {
        this.name = name;
        this.address = address;
        this.category = "unkown";
        this.bonded = bonded;
    }

    public boolean isBonded() {
        return bonded;
    }

    public void setBonded(boolean bonded) {
        this.bonded = bonded;
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
