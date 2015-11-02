package ch.fluxron.fluxronapp.objectBase;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.fluxron.fluxronapp.data.generated.ParamManager;

/**
 * A model for a bluetooth device.
 */
public class Device {
    private String name;
    private boolean bonded;
    private Date lastContact;
    private Map<String, DeviceParameter> deviceParameters = new HashMap<>();

    @JsonProperty("_id")
    String address;

    public Device() {
    }

    public Device(String name, String address, boolean bonded) {
        this.name = name;
        this.address = address;
        this.bonded = bonded;
        initDevice();
    }

    /**
     * Sets initial values for certain parameters
     */
    private void initDevice(){
        setDeviceParameter(new DeviceParameter(ParamManager.F_MANUFACTURER_DEVICE_NAME_1008, "Unkown Device Type"));
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

    public Date getLastContact() {
        return lastContact;
    }

    public void setLastContact(Date lastContact) {
        this.lastContact = lastContact;
    }

    public DeviceParameter getDeviceParameter(String paramName) {
        return deviceParameters.get(paramName);
    }

    public void setDeviceParameter(DeviceParameter param) {
        deviceParameters.put(param.getName(), param);
    }
}
