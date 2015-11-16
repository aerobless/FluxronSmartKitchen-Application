package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Notifies the deletion of a device from an area
 */
public class DeviceFromAreaDeleted extends RequestResponseConnection {
    private String kitchenId;
    private int areaId;
    private String deviceId;

    /**
     * New device deletion request
     * @param kitchenId Id of the kitchen
     * @param areaId Id of the area relative to the kitchen
     * @param deviceId Id of the device
     */
    public DeviceFromAreaDeleted(String kitchenId, int areaId, String deviceId) {
        this.kitchenId = kitchenId;
        this.areaId = areaId;
        this.deviceId = deviceId;
    }

    /**
     * Returns the id of the kitchen
     * @return Id of the kitchen
     */
    public String getKitchenId() {
        return kitchenId;
    }

    /**
     * Returns the id of the area relative to the kitchen
     * @return Id of the area relative to the kitchen
     */
    public int getAreaId() {
        return areaId;
    }

    /**
     * Returns the id of the device
     * @return Device of the kitchen
     */
    public String getDeviceId() {
        return deviceId;
    }
}
