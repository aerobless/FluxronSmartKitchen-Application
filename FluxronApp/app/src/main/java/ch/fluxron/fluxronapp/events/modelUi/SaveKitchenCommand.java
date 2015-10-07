package ch.fluxron.fluxronapp.events.modelUi;

import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * A simple message containing a text. PROTOTYPE USAGE ONLY!
 */
public class SaveKitchenCommand {
    private Kitchen kitchen;

    public Kitchen getKitchen() {
        return kitchen;
    }

    public void setKitchen(Kitchen kitchen) {
        this.kitchen = kitchen;
    }

}
