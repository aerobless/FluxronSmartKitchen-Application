package ch.fluxron.fluxronapp.model;

import android.util.LruCache;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Used to iterate through all registered devices and refresh their parameters.
 */
public class CyclicRefresh extends Thread{
    private AtomicBoolean enabled = new AtomicBoolean(false);
    private LruCache<String, Device> deviceCache;
    private IEventBusProvider provider;
    private String currentConnection;
    private Object lock = new Object();
    private AtomicBoolean doNext = new AtomicBoolean(false);

    public CyclicRefresh(IEventBusProvider provider, LruCache<String, Device> deviceCache) {
        this.provider = provider;
        this.deviceCache = deviceCache;
        this.currentConnection = new String();
        provider.getDalEventBus().register(this);
    }

    public void run() {
        while(enabled.get()){
            Map<String, Device> deviceList;
            synchronized (deviceCache){
                deviceList = deviceCache.snapshot();
            }
            for(Device device: deviceList.values()){
                if(!enabled.get()){
                    break;
                }
                if(device.isBonded()){
                    //TODO: replace one param with list of interesting params
                    RequestResponseConnection req = new BluetoothReadRequest(device.getAddress(), ParamManager.F_SCLASS_1018SUB2_PRODUCT_CODE);
                    synchronized (lock){
                        currentConnection = req.getConnectionId();
                        provider.getDalEventBus().post(req);
                        while(!doNext.get()){
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        doNext.set(false);
                    }
                }
            }
        }
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

    public void setEnabled(boolean val){
        enabled.set(val);
    }
}
