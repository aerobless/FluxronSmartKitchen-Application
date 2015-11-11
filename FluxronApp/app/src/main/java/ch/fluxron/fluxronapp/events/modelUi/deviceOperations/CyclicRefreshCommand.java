package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to enable or disable the cyclic refresh of devices.
 */
public class CyclicRefreshCommand extends RequestResponseConnection{
    public static final String ALL_DEVICES = "refresh_all_devices";
    public static final String NONE = "stop_refreshing_devices";

    private String deviceToRefresh;

    public CyclicRefreshCommand(String deviceToRefresh) {
        this.deviceToRefresh = deviceToRefresh;
    }

    public String getDeviceToRefresh() {
        return deviceToRefresh;
    }

    public void setDeviceToRefresh(String deviceToRefresh) {
        this.deviceToRefresh = deviceToRefresh;
    }
}
