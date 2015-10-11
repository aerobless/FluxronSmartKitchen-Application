package ch.fluxron.fluxronapp.events.modelDal;

public class BluetoothDiscoveryCommand {
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
