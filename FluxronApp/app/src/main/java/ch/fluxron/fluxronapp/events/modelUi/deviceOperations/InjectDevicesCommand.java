package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import java.util.List;
import java.util.Set;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Used to inject devices loaded with a kitchen into the device manager.
 */
public class InjectDevicesCommand extends RequestResponseConnection{
    Set<String> deviceList;

    public InjectDevicesCommand(Set<String> deviceList) {
        this.deviceList = deviceList;
    }

    public Set<String> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(Set<String> deviceList) {
        this.deviceList = deviceList;
    }
}
