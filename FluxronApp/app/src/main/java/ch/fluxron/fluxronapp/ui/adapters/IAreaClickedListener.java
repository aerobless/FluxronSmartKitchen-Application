package ch.fluxron.fluxronapp.ui.adapters;

import ch.fluxron.fluxronapp.objectBase.KitchenArea;

/**
 * Listens to clicks on kitchen areas
 */
public interface IAreaClickedListener {
    void areaClicked(KitchenArea a);
    void areaScrolled(int pos);
}
