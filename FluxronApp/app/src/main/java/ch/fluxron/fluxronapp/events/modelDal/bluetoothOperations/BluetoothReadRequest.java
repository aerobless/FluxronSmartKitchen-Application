package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import java.util.HashSet;
import java.util.Set;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Sends a read request to a specific bluetooth device.
 */
public class BluetoothReadRequest extends RequestResponseConnection {
    private String address;
    private Set<String> params;

    /**
     * Instantiates a new BluetoothReadRequest without content. You'll need to use addParam().
     *
     * @param address
     */
    public BluetoothReadRequest(String address) {
        this.address = address;
        this.params = new HashSet<>();
    }

    /**
     * Instantiates a new BluetoothReadRequest with a single parameter.
     *
     * @param address
     */
    public BluetoothReadRequest(String address, String singleParam) {
        this.address = address;
        params = new HashSet<>();
        params.add(singleParam);
    }

    /**
     * Instantiates a new BluetoothReadRequest with a list of parameters.
     *
     * @param address
     */
    public BluetoothReadRequest(String address, Set<String> paramList) {
        this.address = address;
        this.params = paramList;
    }

    /**
     * Returns the address of the device that is accessed.
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the address of the device that is accessed.
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns a set of parameters.
     *
     * @return
     */
    public Set<String> getParameters() {
        return params;
    }

    /**
     * Add a paramater to this request.
     *
     * @param param
     */
    public void addParam(String param) {
        params.add(param);
    }
}
