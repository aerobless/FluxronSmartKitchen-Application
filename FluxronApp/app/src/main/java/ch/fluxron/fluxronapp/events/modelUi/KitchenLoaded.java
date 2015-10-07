package ch.fluxron.fluxronapp.events.modelUi;

import ch.fluxron.fluxronapp.objectBase.Kitchen;

public class KitchenLoaded {
    private Kitchen kitchen;

    public KitchenLoaded(Kitchen kitchen) {
        this.kitchen = kitchen;
    }

    public Kitchen getKitchen() {
        return kitchen;
    }

    public void setKitchen(Kitchen kitchen) {
        this.kitchen = kitchen;
    }
}
