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
     * Request the storage of a changed device position
     * @param pos Location of the device
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
     * Gets the position of the device
     * @return Position
     */
    public Point getPos() {
        return pos;
    }

    /**
     * Sets the position of the device
     * @param pos Position
     */
    public void setPos(Point pos) {
        this.pos = pos;
    }

    /**
     * Gets the device ID
     * @return Device ID
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the id of the device
     * @param deviceId Device ID
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the id of the kitchen
     * @return Kitchen id
     */
    public String getKitchenId() {
        return kitchenId;
    }

    /**
     * Sets the id of the kitchen
     * @param kitchenId Kitchen id
     */
    public void setKitchenId(String kitchenId) {
        this.kitchenId = kitchenId;
    }

    /**
     * Gets the id of the area relative to the kitchen
     * @return Area id relative to the kitchen
     */
    public int getAreaId() {
        return areaId;
    }

    /**
     * Sets the id of the area relative to the kitchen
     * @param areaId Area id relative to the kitchen
     */
    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }
}
