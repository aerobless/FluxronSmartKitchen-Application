package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Loads a kitchen area
 */
public class LoadKitchenAreaCommand extends RequestResponseConnection {
    private String kitchenId;
    private int relativeId;

    /**
     * Request the load of an area
     * @param kitchenId Kitchen id
     * @param relativeId Area relative id
     */
    public LoadKitchenAreaCommand(String kitchenId, int relativeId) {
        this.kitchenId = kitchenId;
        this.relativeId = relativeId;
    }

    /**
     * Returns the kitchen Id
     * @return Id of the kitchen
     */
    public String getKitchenId() {
        return kitchenId;
    }

    /**
     * Returns the relative id of the area
     * @return Area relative id
     */
    public int getRelativeId() {
        return relativeId;
    }
}
