package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to enable or disable the discovery of bluetooth devices.
 */
public class BluetoothDiscoveryCommand extends RequestResponseConnection {
    private boolean enabled;

    /**
     * Instantiates a new BluetoothDiscoverycommand.
     *
     * @param enabled
     */
    public BluetoothDiscoveryCommand(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns whether the discovery should be enabled or not. True if enabled.
     *
     * @return is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the discovery should be enabled or not. True if enabled.
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
