package ch.fluxron.fluxronapp.events.modelDal;

public class BluetoothDiscoveryResponse {
    private String deviceName;
    private String deviceMAC;

    public BluetoothDiscoveryResponse(String deviceName, String deviceMAC) {
        this.deviceName = deviceName;
        this.deviceMAC = deviceMAC;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceMAC() {
        return deviceMAC;
    }
}
