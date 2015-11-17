package ch.fluxron.fluxronapp.objectBase;

import android.util.Log;

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
    private int productCode = 0;
    private Map<String, DeviceParameter> deviceParameters = new HashMap<>();
    private DeviceTypeConverter dtConverter = new DeviceTypeConverter();

    public static final String UNKNOWN_DEVICE_TYPE = "Unknown Device Type";
    public static final String INVALID_DEVICE_TYPE = "Invalid Device Type";

    @JsonProperty("_id")
    String address;

    public Device() {}

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

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public DeviceParameter getDeviceParameter(String paramName) {
        return deviceParameters.get(paramName);
    }

    public void setDeviceParameter(DeviceParameter param) {
        deviceParameters.put(param.getName(), param);
    }

    /**
     * Returns the device type if "F_SCLASS_1018SUB2_PRODUCT_CODE" is stored for this device.
     * Otherwise it will return UNKNOWN_DEVICE_TYPE. If the value stored in param "F_SCLASS_1018SUB2_PRODUCT_CODE"
     * is invalid it will return INVALID_DEVICE_TYPE.
     * @return
     */
    public String getDeviceType(){
        if(productCode != 0){
            try{
                return dtConverter.toDeviceType(productCode);
            } catch(NumberFormatException e){
                Log.d("FLUXRON", "Attempt to use illegal characters as product_code");
                return INVALID_DEVICE_TYPE;
            }
        } else {
            return UNKNOWN_DEVICE_TYPE;
        }
    }

    /**
     * Returns the device class if "F_SCLASS_1018SUB2_PRODUCT_CODE" is stored for this device.
     * Otherwise it will return UNKNOWN_DEVICE_TYPE. If the value stored in param "F_SCLASS_1018SUB2_PRODUCT_CODE"
     * is invalid it will return INVALID_DEVICE_TYPE.
     * @return
     */
    public String getDeviceClass(){
        if(productCode != 0){
            try{
                return dtConverter.toDeviceClass(productCode);
            } catch(NumberFormatException e){
                Log.d("FLUXRON", "Attempt to use illegal characters as product_code");
                return INVALID_DEVICE_TYPE;
            }
        } else {
            return UNKNOWN_DEVICE_TYPE;
        }
    }

    /**
     * Prints all the parameters stored in this device object.
     * Used for debugging purposes.
     */
    public void printStoredParameters(){
        for(DeviceParameter dp: deviceParameters.values()){
            Log.d("FLUXRON DEBUG", "ParamName: "+dp.getName()+" Value: "+dp.getValue());
        }
    }
}
