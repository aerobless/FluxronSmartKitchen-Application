package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import java.util.ArrayList;
import java.util.List;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Sends a read request to a specific bluetooth device.
 */
public class BluetoothReadRequest extends RequestResponseConnection{
    private String address;
    private List<String> params;

    public BluetoothReadRequest(String address) {
        this.address = address;
        this.params = new ArrayList<>();
    }

    public BluetoothReadRequest(String address, String singleParam) {
        this.address = address;
        params = new ArrayList<>();
        params.add(singleParam);
    }

    public BluetoothReadRequest(String address, List<String> paramList) {
        this.address = address;
        this.params = paramList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getParameters() {
        return params;
    }

    public void addParam(String param) {
        params.add(param);
    }
}
