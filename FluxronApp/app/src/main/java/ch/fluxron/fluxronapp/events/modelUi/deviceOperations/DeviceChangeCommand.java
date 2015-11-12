package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.DeviceParameter;

/**
 * Used to request changing a device parameter
 */
public class DeviceChangeCommand extends RequestResponseConnection{
    DeviceParameter changeRequest;
    String address;

    public DeviceChangeCommand(String address, DeviceParameter changeRequest) {
        this.changeRequest = changeRequest;
        this.address = address;
    }

    public DeviceParameter getChangeRequest() {
        return changeRequest;
    }

    public void setChangeRequest(DeviceParameter changeRequest) {
        this.changeRequest = changeRequest;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
