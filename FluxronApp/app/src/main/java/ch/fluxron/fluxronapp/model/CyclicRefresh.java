package ch.fluxron.fluxronapp.model;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothConnectionFailure;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Used to iterate through all registered devices and refresh their parameters.
 */
public class CyclicRefresh extends Thread{
    private AtomicBoolean enabled = new AtomicBoolean(false);
    private LruCache<String, Device> deviceCache;
    private IEventBusProvider provider;
    private String currentConnection;
    private final Object lock = new Object();
    private AtomicBoolean doNext = new AtomicBoolean(false);
    private Set<String> listOfInterestingParameters;

    public CyclicRefresh(IEventBusProvider provider, LruCache<String, Device> deviceCache) {
        this.provider = provider;
        this.deviceCache = deviceCache;
        this.currentConnection = new String();
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
        listOfInterestingParameters = initLoiP();
    }

    //TODO: differentiate between device type, currently all S-Class
    private Set<String> initLoiP(){
        Set<String> list = new HashSet<>();
        list.add(ParamManager.F_SCLASS_1018SUB2_PRODUCT_CODE);
        list.add(ParamManager.F_SCLASS_1008_MANUFACTURER_DEVICE_NAME);
        list.add(ParamManager.F_SCLASS_1009_MANUFACTURER_HARDWARE_VERSION);
        list.add(ParamManager.F_SCLASS_100A_MANUFACTURER_SOFTWARE_VERSION);
        list.add(ParamManager.F_SCLASS_3038_SAFETY);
        list.add(ParamManager.F_SCLASS_3035SUB7_FLX_ACTIVE_POWER);
        return list;
    }

    @Override
    public void run() {
        while(enabled.get()){
            Map<String, Device> deviceList = getUpdatedDeviceList();
            for(Device device: deviceList.values()){
                if(!enabled.get()){
                    break;
                }
                if(device.isBonded()){
                    //TODO: replace one param with list of interesting params
                    RequestResponseConnection req = new BluetoothReadRequest(device.getAddress(), listOfInterestingParameters);
                    synchronized (lock){
                        currentConnection = req.getConnectionId();
                        provider.getDalEventBus().post(req);
                        while(!doNext.get()){
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                Log.d("FLUXRON", "Interruped exception in CyclicRefresh");
                            }
                        }
                        doNext.set(false);
                    }
                }
            }
        }
    }

    /**
     * Returns an updated device list and makes sure that the CyclicRefresh doesn't uselessly
     * chew memory when there are no bonded devices.
     * @return
     */
    @NonNull
    private Map<String, Device> getUpdatedDeviceList() {
        try {
            Thread.sleep(1000); //Cooldown after a device-cycle.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, Device> deviceList;
        synchronized (deviceCache){
            deviceList = deviceCache.snapshot();
        }
        return deviceList;
    }

    /**
     * Checks BluetoothDeviceChanged messages to see if it originated from the CyclicRefresh.
     * If it did originate here, it wakes the CyclicRefresh to update the next device.
     * @param inputMsg
     */
    public void onEventAsync(BluetoothDeviceChanged inputMsg){
        String connectionID = inputMsg.getConnectionId();

        synchronized (lock){
            if(connectionID.equals(currentConnection)){
                doNext.set(true);
                lock.notifyAll();
            }
        }
    }

    /**
     * If the pipe broke or the connection could not be established for other reasons.
     * Skips the current device. It will be automatically retried in the next cycle.
     * @param inputMsg
     */
    public void onEventAsync(BluetoothConnectionFailure inputMsg){
        String connectionID = inputMsg.getConnectionId();

        synchronized (lock){
            if(connectionID.equals(currentConnection)){
                doNext.set(true);
                lock.notifyAll();
            }
        }
    }

    /**
     * Listens to RegisterParameterCommand and adds it to the List of interesting parameters.
     * @param inputMsg
     */
    public void onEventAsync(RegisterParameterCommand inputMsg){
        Log.d("FLUXRON","REGISTERED REGISTERED!!!!!!!");
        synchronized (listOfInterestingParameters){
            listOfInterestingParameters.add(inputMsg.getParameter());
        }
    }

    public void setEnabled(boolean val){
        enabled.set(val);
    }
}
