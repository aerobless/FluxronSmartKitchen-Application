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

    /**
     * Notifies a listener about the change of position for a device
     * @param kitchenId Id of the kitchen
     * @param areaId Area id
     * @param position New position for the device
     */
    public DevicePositionChanged(String kitchenId, int areaId, DevicePosition position) {
        this.kitchenId = kitchenId;
        this.areaId = areaId;
        this.position = position;
    }

    /**
     * Gets the id of the kitchen
     * @return Id of the kitchen
     */
    public String getKitchenId() {
        return kitchenId;
    }

    /**
     * Gets the id of the area relative to the kitchen
     * @return Area id relative to the kitchen
     */
    public int getAreaId() {
        return areaId;
    }

    /**
     * Gets the new position of the device
     * @return Position of the device
     */
    public DevicePosition getPosition() {
        return position;
    }
}
