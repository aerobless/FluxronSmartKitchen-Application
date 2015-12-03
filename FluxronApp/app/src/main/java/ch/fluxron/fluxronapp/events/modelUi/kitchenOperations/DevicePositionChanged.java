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
     * Position of the device changed
     * @param kitchenId Id of the kitchen
     * @param areaId Id of the area relative to the kitchen
     * @param position New position
     */
    public DevicePositionChanged(String kitchenId, int areaId, DevicePosition position) {
        this.kitchenId = kitchenId;
        this.areaId = areaId;
        this.position = position;
    }

    /**
     * Gets the kitchen id
     * @return Kitchen id
     */
    public String getKitchenId() {
        return kitchenId;
    }

    /**
     * Returns the area id
     * @return Id of the area
     */
    public int getAreaId() {
        return areaId;
    }

    /**
     * Gets the new position of the device
     * @return New Position
     */
    public DevicePosition getPosition() {
        return position;
    }
}
