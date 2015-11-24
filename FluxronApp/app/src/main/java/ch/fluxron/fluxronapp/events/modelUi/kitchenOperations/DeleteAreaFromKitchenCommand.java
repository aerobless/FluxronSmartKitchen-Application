package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests the deletion of a kitchen area
 */
public class DeleteAreaFromKitchenCommand extends RequestResponseConnection {
    private String kitchenId;
    private int areaId;

    /**
     * New area deletion request
     * @param kitchenId Id of the kitchen
     * @param areaId Id of the area relative to the kitchen
     */
    public DeleteAreaFromKitchenCommand(String kitchenId, int areaId) {
        this.kitchenId = kitchenId;
        this.areaId = areaId;
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
}
