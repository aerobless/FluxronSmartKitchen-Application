package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to inform the UI that the requested data can not be accessed. Permanently.
 * This is the case when the device doesn't contain the requested parameter.
 */
public class DeviceNotChanged extends RequestResponseConnection {
    String field;
    String address;

    /**
     * Instantiates a new DeviceNotChanged event.
     *
     * @param field
     * @param address
     */
    public DeviceNotChanged(String field, String address) {
        this.field = field;
        this.address = address;
    }

    /**
     * Returns the field/parameter id that couldn't be changed.
     *
     * @return field
     */
    public String getField() {
        return field;
    }

    /**
     * Sets the field/paramter id that couldn't be changed.
     *
     * @param field
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * Returns the device address.
     *
     * @return device address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the device address.
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
