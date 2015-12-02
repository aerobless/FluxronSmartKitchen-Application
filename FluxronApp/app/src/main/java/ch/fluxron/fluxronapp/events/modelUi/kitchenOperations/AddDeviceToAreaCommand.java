package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import android.graphics.Point;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;

/**
 * Adds a device to a kitchen area
 */
public class AddDeviceToAreaCommand extends RequestResponseConnection{
    private KitchenArea kitchenArea;
    private Device device;
    private Point position;

    /**
     * Returns the device that should be added
     * @return Device
     */
    public Device getDevice() {
        return device;
    }

    /**
     * Sets the device that should be added
     * @param device Device
     */
    public void setDevice(Device device) {
        this.device = device;
    }

    /**
     * Returns the kitchen area that the device should be added to
     * @return Kitchen area
     */
    public KitchenArea getKitchenArea() {
        return kitchenArea;
    }

    /**
     * Sets the kitchen area that the device should be added to
     * @param kitchenArea Kitchen area
     */
    public void setKitchenArea(KitchenArea kitchenArea) {
        this.kitchenArea = kitchenArea;
    }

    /**
     * Gets the position at which this device should be added
     * @return Position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Sets the position at which this device should be added
     * @param position Position
     */
    public void setPosition(Point position) {
        this.position = position;
    }
}
