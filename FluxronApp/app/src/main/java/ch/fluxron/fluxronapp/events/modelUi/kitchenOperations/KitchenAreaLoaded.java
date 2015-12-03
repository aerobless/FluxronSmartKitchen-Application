package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;

/**
 * Confirms the load of a kitchen area
 */
public class KitchenAreaLoaded extends RequestResponseConnection {
    private KitchenArea area;

    /**
     * Area was loaded event
     * @param area Area
     */
    public KitchenAreaLoaded(KitchenArea area) {
        this.area = area;
    }

    /**
     * Returns the loaded area
     * @return Area
     */
    public KitchenArea getArea() {
        return area;
    }
}
