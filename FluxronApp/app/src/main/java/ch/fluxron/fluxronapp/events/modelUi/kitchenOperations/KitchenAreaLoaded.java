package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;

/**
 * Confirms the load of a kitchen area
 */
public class KitchenAreaLoaded extends RequestResponseConnection {
    private KitchenArea area;

    /**
     * Confirms the load of a kitchen area
     * @param area Kitchen area
     */
    public KitchenAreaLoaded(KitchenArea area) {
        this.area = area;
    }

    /**
     * Gets the loaded area
     * @return Loaded area
     */
    public KitchenArea getArea() {
        return area;
    }
}
