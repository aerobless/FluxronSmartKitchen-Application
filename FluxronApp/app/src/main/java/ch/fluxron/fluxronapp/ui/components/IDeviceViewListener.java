package ch.fluxron.fluxronapp.ui.components;

/**
 * Listens to events happening on a device view
 */
public interface IDeviceViewListener {
    boolean moveRequested(DeviceView v, int dx, int dy, boolean finalPosition);
    void actionRequested(DeviceView v);
    void needsRepaint(boolean force);
    void deleteRequested(DeviceView v);
}
