package ch.fluxron.fluxronapp.ui.adapters;

import ch.fluxron.fluxronapp.model.Device;

/**
 * Listens to the click event on a device
 */
public interface IDeviceClickListener {
    void deviceClicked(Device d);
}
