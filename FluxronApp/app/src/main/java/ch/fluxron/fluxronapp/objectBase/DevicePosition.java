package ch.fluxron.fluxronapp.objectBase;

import android.graphics.Point;

/**
 * Stores the position of a device
 */
public class DevicePosition {
    private String deviceId;
    private Point position;

    /**
     * Gets the id of the device
     * @return device id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the id of the device
     * @param deviceId Id of the device
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the position of the device
     * @return position of the device
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Sets the position
     * @param position position
     */
    public void setPosition(Point position) {
        this.position = position;
    }
}
