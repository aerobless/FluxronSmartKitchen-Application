package ch.fluxron.fluxronapp.ui.adapters;

import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Listens to the click event on a device
 */
public interface IDeviceClickListener {
    /**
     * Device was clicked
     * @param d Device
     */
    void deviceClicked(Device d);

    /**
     * Device was requested to be paired / added
     * @param d Device
     */
    void deviceButtonPressed(Device d);
}
