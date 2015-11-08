package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Sends a command to save a kitchen
 */
public class ChangeKitchenSettings extends RequestResponseConnection {
    private String kitchenId;
    private String kitchenName;
    private String kitchenDescription;

    /**
     * Creates a new command to change kitchen settings
     * @param kitchenId Id of the kitchen
     * @param kitchenName Name of the kitchen
     * @param kitchenDescription Description of the kitchen
     */
    public ChangeKitchenSettings(String kitchenId, String kitchenName, String kitchenDescription) {
        this.kitchenId = kitchenId;
        this.kitchenName = kitchenName;
        this.kitchenDescription = kitchenDescription;
    }

    /**
     * Returns the id of the kitchen
     * @return Id of the kitchen
     */
    public String getKitchenId() {
        return kitchenId;
    }

    /**
     * Returns the name of the kitchen
     * @return Name of the kitchen
     */
    public String getKitchenName() {
        return kitchenName;
    }

    /**
     * Returns the description of the kitchen
     * @return Description of the kitchen
     */
    public String getKitchenDescription() {
        return kitchenDescription;
    }
}
