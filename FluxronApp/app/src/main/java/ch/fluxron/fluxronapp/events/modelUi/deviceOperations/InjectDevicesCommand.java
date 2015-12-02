package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import java.util.Set;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.DevicePosition;

/**
 * Used to inject devices loaded with a kitchen into the device manager.
 */
public class InjectDevicesCommand extends RequestResponseConnection {
    Set<DevicePosition> deviceList;

    /**
     * Instantiates a new InjectDevicesCommand.
     *
     * @param deviceList
     */
    public InjectDevicesCommand(Set<DevicePosition> deviceList) {
        this.deviceList = deviceList;
    }

    /**
     * Returns a list of DevicePositions that should be injected.
     *
     * @return
     */
    public Set<DevicePosition> getDeviceList() {
        return deviceList;
    }

    /**
     * Sets the list of DevicePositions that should be injected.
     *
     * @param deviceList
     */
    public void setDeviceList(Set<DevicePosition> deviceList) {
        this.deviceList = deviceList;
    }
}
