package ch.fluxron.fluxronapp.objectBase;

import android.graphics.Point;

/**
 * Stores the position of a device
 */
public class DevicePosition {
    private String deviceId;
    private Point position;
    private String category;
    private String name;

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

    /**
     * Returns the device category
     * @return Category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the device category
     * @param category Category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the name of the device
     * @return Name of the device
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the device
     * @param name Name of the device
     */
    public void setName(String name) {
        this.name = name;
    }
}
