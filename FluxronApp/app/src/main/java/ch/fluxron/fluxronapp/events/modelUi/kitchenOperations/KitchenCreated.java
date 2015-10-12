package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Notifies about creation of a kitchen
 */
public class KitchenCreated extends RequestResponseConnection {
    private Kitchen kitchen;

    /**
     * Sets the kitchen that was created
     * @param k Kitchen
     */
    public KitchenCreated(Kitchen k){
        kitchen = k;
    }

    /**
     * Returns the kitchen
     * @return Kitchen
     */
    public Kitchen getKitchen(){
        return kitchen;
    }
}
