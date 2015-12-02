package ch.fluxron.fluxronapp.events.modelUi.deviceOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Used to enable or disable the cyclic refresh of devices. <br><br>
 * Supports the following predefined strings: <br>
 * + <b>ALL_DEVICES:</b> Refresh all devices in this kitchen <br>
 * + <b>NONE:</b> Stop the cyclic refresh. No devices will be refreshed. <br>
 */
public class CyclicRefreshCommand extends RequestResponseConnection {
    public static final String ALL_DEVICES = "refresh_all_devices";
    public static final String NONE = "stop_refreshing_devices";
    private String deviceToRefresh;

    /**
     * Instantiates a new CyclicRefreshCommand.
     *
     * @param deviceToRefresh
     */
    public CyclicRefreshCommand(String deviceToRefresh) {
        this.deviceToRefresh = deviceToRefresh;
    }

    /**
     * Returns the address of the device that should be refreshed.
     * Can contain also "ALL_DEVICES" or "NONE".
     *
     * @return deviceAddress or ALL_DEVICES or NONE
     */
    public String getDeviceToRefresh() {
        return deviceToRefresh;
    }

    /**
     * Sets the address of the device that should be refreshed or "ALL_DEVICES" or "NONE".
     *
     * @param deviceToRefresh
     */
    public void setDeviceToRefresh(String deviceToRefresh) {
        this.deviceToRefresh = deviceToRefresh;
    }
}
