package ch.fluxron.fluxronapp.objectBase;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.fluxron.fluxronapp.ui.util.DeviceTypeConverter;

/**
 * Detailed model of bluetooth devices. Is not intended for long term persistence.
 * Used to keep track of a devices parameters during runtime.
 */
public class Device {
    private String name;
    String address;
    private boolean bonded;
    private Date lastContact;
    private int productCode = 0;
    private Map<String, ParameterValue> deviceParameters = new HashMap<>();
    private DeviceTypeConverter dtConverter = new DeviceTypeConverter();

    public static final String UNKNOWN_DEVICE_TYPE = "Unknown Device Type";
    public static final String INVALID_DEVICE_TYPE = "Invalid Device Type";
    public static final String UNKNOWN_DEVICE_CLASS = "Unknown Device Class";
    public static final String INVALID_DEVICE_CLASS = "Invalid Device Class";


    /**
     * Instantiates a empty device.
     */
    public Device() {
    }

    /**
     * Instantiates a new device with the most important settings.
     *
     * @param name
     * @param address
     * @param bonded
     */
    public Device(String name, String address, boolean bonded) {
        this.name = name;
        this.address = address;
        this.bonded = bonded;
    }

    /**
     * Returns whether the device is bonded or not.
     *
     * @return
     */
    public boolean isBonded() {
        return bonded;
    }

    /**
     * Sets whether the device is bonded or not.
     *
     * @param bonded
     */
    public void setBonded(boolean bonded) {
        this.bonded = bonded;
    }

    /**
     * Returns the name of the device.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the device.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the address of the device.
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the device.
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the last contact.
     *
     * @return
     */
    public Date getLastContact() {
        return lastContact;
    }

    /**
     * Sets the last contact.
     *
     * @param lastContact
     */
    public void setLastContact(Date lastContact) {
        this.lastContact = lastContact;
    }

    /**
     * Returns the product code.
     *
     * @return
     */
    public int getProductCode() {
        return productCode;
    }

    /**
     * Sets the product code.
     *
     * @param productCode
     */
    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    /**
     * Returns the ParameterValue of the parameter/field specified. If the parameter doesn't exist
     * it returns null.
     *
     * @param paramName
     * @return ParameterValue
     */
    public ParameterValue getDeviceParameter(String paramName) {
        return deviceParameters.get(paramName);
    }

    /**
     * Sets the ParameterValue for a specific parameter/field.
     *
     * @param param
     */
    public void setDeviceParameter(ParameterValue param) {
        deviceParameters.put(param.getName(), param);
    }

    /**
     * Returns the device type if "F_SCLASS_1018SUB2_PRODUCT_CODE" is stored for this device.
     * Otherwise it will return UNKNOWN_DEVICE_TYPE. If the value stored in param "F_SCLASS_1018SUB2_PRODUCT_CODE"
     * is invalid it will return INVALID_DEVICE_TYPE.
     *
     * @return
     */
    public String getDeviceType() {
        if (productCode != 0) {
            try {
                return dtConverter.toDeviceType(productCode);
            } catch (NumberFormatException e) {
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
     *
     * @return
     */
    public String getDeviceClass() {
        if (productCode != 0) {
            try {
                return dtConverter.toDeviceClass(productCode);
            } catch (NumberFormatException e) {
                Log.d("FLUXRON", "Attempt to use illegal characters as product_code");
                return INVALID_DEVICE_CLASS;
            }
        } else {
            return UNKNOWN_DEVICE_CLASS;
        }
    }

    /**
     * Prints all the parameters stored in this device object.
     * Used for debugging purposes.
     */
    public void printStoredParameters() {
        for (ParameterValue dp : deviceParameters.values()) {
            Log.d("FLUXRON DEBUG", "ParamName: " + dp.getName() + " Value: " + dp.getValue());
        }
    }
}
