package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import android.graphics.Point;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Request the storage of a changed device position
 */
public class ChangeDevicePositionCommand extends RequestResponseConnection {
    private Point pos;
    private String deviceId;
    private String kitchenId;
    private int areaId;

    /**
     * Position change command
     * @param pos Position
     * @param kitchenId Id of the kitchen
     * @param areaID Id of the area
     * @param deviceId Id of the device
     */
    public ChangeDevicePositionCommand(Point pos, String kitchenId, int areaID, String deviceId) {
        this.pos = pos;
        this.deviceId = deviceId;
        this.areaId = areaID;
        this.kitchenId = kitchenId;
    }

    /**
     * Returns the position of the device
     * @return Position
     */
    public Point getPos() {
        return pos;
    }

    /**
     * Returns the id of the device
     * @return Device id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Returns the id of the kitchen
     * @return Id of the kitchen
     */
    public String getKitchenId() {
        return kitchenId;
    }

    /**
     * Sets the id of the kitchen
     * @param kitchenId Id of the kitchen
     */
    public void setKitchenId(String kitchenId) {
        this.kitchenId = kitchenId;
    }

    /**
     * Gets the id of the area
     * @return Id of the area
     */
    public int getAreaId() {
        return areaId;
    }
}
