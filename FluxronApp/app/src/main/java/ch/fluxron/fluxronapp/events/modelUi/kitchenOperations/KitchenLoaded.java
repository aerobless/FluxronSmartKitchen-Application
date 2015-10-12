package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Notifies subscribers that a kitchen has been loaded
 */
public class KitchenLoaded extends RequestResponseConnection{
    private Kitchen kitchen;

    /**
     * Creates a new event containing kitchen data
     * @param kitchen Kitchen data
     */
    public KitchenLoaded(Kitchen kitchen) {
        this.kitchen = kitchen;
    }

    /**
     * Returns the kitchen that was loaded. Keep in mind that this is just a copy of the original instance.
     * @return Loaded kitchen data
     */
    public Kitchen getKitchen() {
        return kitchen;
    }
}
