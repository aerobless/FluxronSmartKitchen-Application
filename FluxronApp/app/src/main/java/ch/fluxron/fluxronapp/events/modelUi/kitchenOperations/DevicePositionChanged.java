package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.DevicePosition;

/**
 * Notifies a listener about the change of position for a device
 */
public class DevicePositionChanged extends RequestResponseConnection {
    private String kitchenId;
    private int areaId;
    private DevicePosition position;

    public DevicePositionChanged(String kitchenId, int areaId, DevicePosition position) {
        this.kitchenId = kitchenId;
        this.areaId = areaId;
        this.position = position;
    }

    public String getKitchenId() {
        return kitchenId;
    }

    public int getAreaId() {
        return areaId;
    }

    public DevicePosition getPosition() {
        return position;
    }
}
