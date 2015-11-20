package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to inform the UI that the requested data can not be accessed. Permanently.
 * This is the case when the device doesn't contain the requested parameter.
 */
public class DeviceNotChanged extends RequestResponseConnection{
    String field;
    String address;

    public DeviceNotChanged(String field, String address) {
        this.field = field;
        this.address = address;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
