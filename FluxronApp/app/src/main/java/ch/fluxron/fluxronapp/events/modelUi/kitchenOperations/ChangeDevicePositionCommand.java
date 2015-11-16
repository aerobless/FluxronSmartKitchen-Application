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

    public ChangeDevicePositionCommand(Point pos, String kitchenId, int areaID, String deviceId) {
        this.pos = pos;
        this.deviceId = deviceId;
        this.areaId = areaID;
        this.kitchenId = kitchenId;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getKitchenId() {
        return kitchenId;
    }

    public void setKitchenId(String kitchenId) {
        this.kitchenId = kitchenId;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }
}
