package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Notifies subscribers that a device has been loaded
 */
public class DeviceLoaded extends RequestResponseConnection {
    private Device device;

    /**
     * Creates a new event containing device data
     *
     * @param device Device data
     */
    public DeviceLoaded(Device device) {
        this.device = device;
    }

    /**
     * Returns the device that was loaded. Keep in mind that this is just a copy of the original instance.
     *
     * @return Loaded device data
     */
    public Device getDevice() {
        return device;
    }
}
