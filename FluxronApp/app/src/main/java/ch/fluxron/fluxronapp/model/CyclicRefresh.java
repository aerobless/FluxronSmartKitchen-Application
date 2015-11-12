package ch.fluxron.fluxronapp.model;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothConnectionFailure;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.CyclicRefreshCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Used to iterate through all registered devices and refresh their parameters.
 */
public class CyclicRefresh {
    private AtomicBoolean enabled = new AtomicBoolean(false);
    private final LruCache<String, Device> deviceCache;
    private final Set<String> refreshList;
    private IEventBusProvider provider;
    private String currentConnection;
    private final Object lock = new Object();
    private AtomicBoolean doNext = new AtomicBoolean(false);
    private final Set<String> listOfInterestingParameters;

    public CyclicRefresh(IEventBusProvider provider, LruCache<String, Device> deviceCache) {
        this.provider = provider;
        this.deviceCache = deviceCache;
        this.refreshList = new HashSet<>();
        this.currentConnection = "";
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
        listOfInterestingParameters = initLoiP();
    }

    private static Set<String> initLoiP(){
        Set<String> list = new HashSet<>();
        list.add(ParamManager.F_CCLASS_1018SUB2_PRODUCT_CODE);
        list.add(ParamManager.F_CCLASS_1008_MANUFACTURER_DEVICE_NAME);
        list.add(ParamManager.F_CCLASS_1009_MANUFACTURER_HARDWARE_VERSION);
        list.add(ParamManager.F_CCLASS_100A_MANUFACTURER_SOFTWARE_VERSION);
        list.add(ParamManager.F_CCLASS_3030SUBD_BAX_GRADIENT_LIMIT_LOW_TEMP);
        return list;
    }

    private void run() {
        while(enabled.get()){
            Set<String> localRefreshList;
            synchronized (refreshList){
                localRefreshList = refreshList;
            }
            for(String device: localRefreshList){
                if(!enabled.get()){
                    break;
                }
                postRequest(device);
            }
            cooldown(1000);
        }
    }

    private void postRequest(String device) {
        RequestResponseConnection req = new BluetoothReadRequest(device, listOfInterestingParameters);
        synchronized (lock){
            while(!doNext.get()){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Log.d("FLUXRON", "Interruped exception in CyclicRefresh");
                }
            }
            currentConnection = req.getConnectionId();
            provider.getDalEventBus().post(req);
            doNext.set(false);
        }
    }

    private static void cooldown(int timeInMs) {
        try {
            Thread.sleep(timeInMs);
        } catch (InterruptedException e) {
            Log.d("FLUXRON", "InterruptedException during Cooldown in CyclicRefresh");
        }
    }



    /**
     * Copy a snapshot of the current deviceCache to the refreshList.
     * @return
     */
    @NonNull
    private Set<String> copyDeviceCacheToRefreshList() {
        Map<String, Device> deviceMap;
        synchronized (deviceCache){
            deviceMap = deviceCache.snapshot();
        }
        Set<String> deviceSet = new HashSet<>();
        for (Device device:deviceMap.values()){
            deviceSet.add(device.getAddress());
        }
        return deviceSet;
    }

    /**
     * Used to inject devices loaded with a kitchen into the device manager.
     * @param cmd
     */
    public void onEventAsync(CyclicRefreshCommand cmd){
        if(cmd.getDeviceToRefresh().equals(CyclicRefreshCommand.ALL_DEVICES)){
            copyDeviceCacheToRefreshList();
            start();
        } else if(cmd.getDeviceToRefresh().equals(CyclicRefreshCommand.NONE)){
            refreshList.clear();
            skipToNext();
            enabled.set(false);
        } else{
            refreshList.clear();
            refreshList.add(cmd.getDeviceToRefresh());
            start();
        }
    }

    private void start() {
        skipToNext();
        if(!enabled.getAndSet(true)){
            run();
        }
    }

    /**
     * Checks BluetoothDeviceChanged messages to see if it originated from the CyclicRefresh.
     * If it did originate here, it wakes the CyclicRefresh to update the next device.
     * @param inputMsg
     */
    public void onEventAsync(BluetoothDeviceChanged inputMsg){
        String connectionID = inputMsg.getConnectionId();
        if(connectionID.equals(currentConnection)){
            skipToNext();
        }
    }

    private void skipToNext() {
        synchronized (lock){
            doNext.set(true);
            lock.notifyAll();
        }
    }

    /**
     * If the pipe broke or the connection could not be established for other reasons.
     * Skips the current device. It will be automatically retried in the next cycle.
     * @param inputMsg
     */
    public void onEventAsync(BluetoothConnectionFailure inputMsg){
        String connectionID = inputMsg.getConnectionId();
        if(connectionID.equals(currentConnection)){
            skipToNext();
        }
    }

    /**
     * Listens to RegisterParameterCommand and adds it to the List of interesting parameters.
     * @param inputMsg
     */
    public void onEventAsync(RegisterParameterCommand inputMsg){
        synchronized (listOfInterestingParameters){
            listOfInterestingParameters.add(inputMsg.getParameter());
        }
    }
}
