package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;

/**
 * Used to request changing a device parameter
 */
public class DeviceChangeCommand extends RequestResponseConnection{
    ParameterValue changeRequest;
    String address;

    public DeviceChangeCommand(String address, ParameterValue changeRequest) {
        this.changeRequest = changeRequest;
        this.address = address;
    }

    public ParameterValue getChangeRequest() {
        return changeRequest;
    }

    public void setChangeRequest(ParameterValue changeRequest) {
        this.changeRequest = changeRequest;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
