package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to tell the bluetooth module to enable or disable discovery.
 */
public class BluetoothDiscoveryCommand extends RequestResponseConnection {
    private boolean enabled;

    /**
     * Instantiates a new BluetoothDiscoveryCommand
     *
     * @param enabled
     */
    public BluetoothDiscoveryCommand(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return true if disabled
     */
    public boolean isDisabled() {
        return !enabled;
    }

    /**
     * Sets the discovery as enabled (true) or disabled (false).
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
