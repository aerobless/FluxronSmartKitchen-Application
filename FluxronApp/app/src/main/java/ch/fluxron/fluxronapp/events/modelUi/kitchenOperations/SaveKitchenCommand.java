package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Sends a command to save a kitchen
 */
public class SaveKitchenCommand extends RequestResponseConnection {
    private Kitchen kitchen;

    /**
     * Returns the kitchen to be saved
     * @return Kitchens
     */
    public Kitchen getKitchen() {
        return kitchen;
    }

    /**
     * Sets the kitchen to be saved
     * @param kitchen Kitchen to be saved
     */
    public SaveKitchenCommand(Kitchen kitchen) {
        this.kitchen = kitchen;
    }
}
