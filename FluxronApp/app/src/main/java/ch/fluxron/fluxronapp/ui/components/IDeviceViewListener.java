package ch.fluxron.fluxronapp.ui.components;

/**
 * Listens to events happening on a device view
 */
public interface IDeviceViewListener {
    boolean moveRequested(DeviceView v, int dx, int dy);
    void openRequested(DeviceView v);
}