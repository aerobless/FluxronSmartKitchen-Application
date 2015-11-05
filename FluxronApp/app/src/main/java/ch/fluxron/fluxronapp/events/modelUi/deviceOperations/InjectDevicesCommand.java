package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import java.util.List;

import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Used to inject devices loaded with a kitchen into the device manager.
 */
public class InjectDevicesCommand {
    List<Device> deviceList;

    public InjectDevicesCommand(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }
}
