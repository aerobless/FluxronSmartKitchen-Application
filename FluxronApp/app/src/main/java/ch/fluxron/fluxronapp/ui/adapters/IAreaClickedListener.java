package ch.fluxron.fluxronapp.ui.adapters;

import ch.fluxron.fluxronapp.objectBase.KitchenArea;

/**
 * Listens to clicks on kitchen areas
 */
public interface IAreaClickedListener {
    /**
     * A kitchen area was clicked
     * @param a Area that was clicked
     */
    void areaClicked(KitchenArea a);

    /**
     * The user scrolled to an areas position
     * @param pos Position
     */
    void areaScrolled(int pos);
}
