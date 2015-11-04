package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Sends a command to load a specific device parameter.
 */
public class DeviceParamRequestCommand extends RequestResponseConnection{
    String deviceID;
    String paramID;

    public DeviceParamRequestCommand(String deviceID, String paramID) {
        this.deviceID = deviceID;
        this.paramID = paramID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getParamID() {
        return paramID;
    }
}
