package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;

/**
 * Used to request updating a device parameter with a new value.
 */
public class DeviceChangeCommand extends RequestResponseConnection {
    ParameterValue changeRequest;
    String address;

    /**
     * Instantiates a new DeviceChangeCommand.
     *
     * @param address
     * @param changeRequest
     */
    public DeviceChangeCommand(String address, ParameterValue changeRequest) {
        this.changeRequest = changeRequest;
        this.address = address;
    }

    /**
     * Returns the ParameterValue that should be changed on the device.
     *
     * @return
     */
    public ParameterValue getChangeRequest() {
        return changeRequest;
    }

    /**
     * Sets the ParameterValue that should be changed on the device.
     *
     * @param changeRequest
     */
    public void setChangeRequest(ParameterValue changeRequest) {
        this.changeRequest = changeRequest;
    }

    /**
     * Returns the address of the device that should be changed.
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the device that should be changed.
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
