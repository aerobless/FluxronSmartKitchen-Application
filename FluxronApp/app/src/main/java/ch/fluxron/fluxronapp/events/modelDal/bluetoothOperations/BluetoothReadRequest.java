package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Sends a read request to a specific bluetooth device.
 */
public class BluetoothReadRequest extends RequestResponseConnection{
    private String address;
    private Set<String> params;

    public BluetoothReadRequest(String address) {
        this.address = address;
        this.params = new HashSet<>();
    }

    public BluetoothReadRequest(String address, String singleParam) {
        this.address = address;
        params = new HashSet<>();
        params.add(singleParam);
    }

    public BluetoothReadRequest(String address, Set<String> paramList) {
        this.address = address;
        this.params = paramList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<String> getParameters() {
        return params;
    }

    public void addParam(String param) {
        params.add(param);
    }
}
