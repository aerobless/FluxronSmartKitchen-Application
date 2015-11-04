package ch.fluxron.fluxronapp.objectBase;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.ui.util.DeviceTypeConverter;

/**
 * A model for a bluetooth device.
 */
public class Device {
    private String name;
    private boolean bonded;
    private Date lastContact;
    private Map<String, DeviceParameter> deviceParameters = new HashMap<>();
    private DeviceTypeConverter dtConverter = new DeviceTypeConverter();

    public final static String UNKNOWN_DEVICE_TYPE = "Unknown Device Type";
    public final static String INVALID_DEVICE_TYPE = "Invalid Device Type";

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

    /**
     * Returns the device type if "F_PRODUCT_CODE_1018SUB2" is stored for this device.
     * Otherwise it will return UNKNOWN_DEVICE_TYPE. If the value stored in param "F_PRODUCT_CODE_1018SUB2"
     * is invalid it will return INVALID_DEVICE_TYPE.
     * @return
     */
    public String getDeviceType(){
        DeviceParameter product_code = deviceParameters.get(ParamManager.F_PRODUCT_CODE_1018SUB2);
        if(product_code != null){
            try{
                int productCode = Integer.parseInt(product_code.getValue());
                return dtConverter.toDeviceType(productCode);
            } catch(NumberFormatException e){
                return INVALID_DEVICE_TYPE;
            }
        } else {
            return UNKNOWN_DEVICE_TYPE;
        }
    }
}
