package ch.fluxron.fluxronapp.objectBase;

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

    public String getDeviceType(){
        DeviceParameter product_code = deviceParameters.get(ParamManager.F_PRODUCT_CODE_1018SUB2);
        if(product_code != null){
            if(product_code.getValue().equals("2573")){
                return "BAX-3500-C";
            } else if(product_code.getValue().equals("12815")){
                return "REX-5000-C";
            } else if(product_code.getValue().equals("2575")){
                return "BAC-5000-C";
            } else {
                return "Unknown Product ID: "+product_code.getValue();
            }
        } else {
            return "Unkown Device Type";
        }
    }
}
