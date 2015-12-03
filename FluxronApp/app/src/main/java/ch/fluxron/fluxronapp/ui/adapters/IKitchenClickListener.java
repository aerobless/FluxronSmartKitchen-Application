package ch.fluxron.fluxronapp.ui.adapters;

import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Listens to the click event on a kitchen
 */
public interface IKitchenClickListener {
    /**
     * A kitchen was clicked
     * @param k Kitchen
     */
    void kitchenClicked(Kitchen k);
}
