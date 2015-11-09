package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Loads a kitchen area
 */
public class LoadKitchenArea extends RequestResponseConnection {
    private String kitchenId;
    private int relativeId;

    public LoadKitchenArea(String kitchenId, int relativeId) {
        this.kitchenId = kitchenId;
        this.relativeId = relativeId;
    }

    public String getKitchenId() {
        return kitchenId;
    }

    public int getRelativeId() {
        return relativeId;
    }
}
