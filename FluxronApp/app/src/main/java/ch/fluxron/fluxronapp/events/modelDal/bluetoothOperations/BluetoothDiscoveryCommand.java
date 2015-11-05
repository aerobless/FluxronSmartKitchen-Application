package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

public class BluetoothDiscoveryCommand extends RequestResponseConnection{
    private boolean enabled;

    public BluetoothDiscoveryCommand(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
