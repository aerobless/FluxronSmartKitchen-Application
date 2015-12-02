package ch.fluxron.fluxronapp.model;

import android.util.Log;

import java.util.Map;

import ch.fluxron.fluxronapp.data.MessageFactory;
import ch.fluxron.fluxronapp.data.generated.DeviceParameter;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;

/**
 * Used to validate parameter values before committing them to a device.
 */
public class ParamValidator {
    private Map<String, DeviceParameter> paramMap;

    /**
     * Instantiates a new ParamValidator.
     */
    public ParamValidator() {
        paramMap = new ParamManager().getParamMap();
    }

    /**
     * Check if the value is valid.
     *
     * @param param
     * @return
     */
    public boolean isValid(ParameterValue param) {
        Object converted = convertToObject(param);
        return converted != null;
    }

    /**
     * Converts a string value to it's corresponding datatype.
     * Returns null if the value isn't valid.
     *
     * @param param
     * @return
     */
    public Object convertToObject(ParameterValue param) {
        DeviceParameter parameter = paramMap.get(param.getName());
        if (parameter == null) {
            return null;
        }
        int dataType = parameter.getDataType();

        Object result;
        if (dataType == MessageFactory.DATA_TYPE_BOOLEAN) {
            result = Boolean.parseBoolean(param.getValue());
        } else if (isSignedInt(dataType)) {
            try {
                result = Integer.parseInt(param.getValue());
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (isUnsignedInt(dataType)) {
            try {
                result = Integer.parseInt(param.getValue());
                if ((Integer) result < 0) { //Shouldn't be negative since it's unsigned
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (dataType == MessageFactory.DATA_TYPE_FLOAT) {
            try {
                result = Float.parseFloat(param.getValue());
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (dataType == MessageFactory.DATA_TYPE_VISIBLE_STRING) {
            result = param.getValue();
        } else {
            Log.d("Fluxron", "The requested DataType has not been implemented in the ParamValidator.");
            return null;
        }
        return result;
    }

    /**
     * Check if dataType is signed. True if signed.
     *
     * @param dataType
     * @return
     */
    private static boolean isSignedInt(int dataType) {
        return dataType == MessageFactory.DATA_TYPE_8BIT_SIGNED_INT
                || dataType == MessageFactory.DATA_TYPE_16BIT_SIGNED_INT
                || dataType == MessageFactory.DATA_TYPE_32BIT_SIGNED_INT;
    }

    /**
     * Check if dataType is unsigned. True if unsigned.
     *
     * @param dataType
     * @return
     */
    private static boolean isUnsignedInt(int dataType) {
        return dataType == MessageFactory.DATA_TYPE_8BIT_UNSIGNED_INT
                || dataType == MessageFactory.DATA_TYPE_16BIT_UNSIGNED_INT
                || dataType == MessageFactory.DATA_TYPE_32BIT_UNSIGNED_INT;
    }
}
