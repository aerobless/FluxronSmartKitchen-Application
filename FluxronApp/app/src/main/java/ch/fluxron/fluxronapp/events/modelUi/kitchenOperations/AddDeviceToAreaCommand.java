package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;

/**
 * Adds a device to a kitchen area
 */
public class AddDeviceToAreaCommand extends RequestResponseConnection{
    private KitchenArea kitchenArea;
    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public KitchenArea getKitchenArea() {
        return kitchenArea;
    }

    public void setKitchenArea(KitchenArea kitchenArea) {
        this.kitchenArea = kitchenArea;
    }
}
