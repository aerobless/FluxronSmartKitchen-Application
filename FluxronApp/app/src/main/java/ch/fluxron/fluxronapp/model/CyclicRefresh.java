package ch.fluxron.fluxronapp.model;

import android.util.LruCache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
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

    public CyclicRefresh(IEventBusProvider provider, LruCache<String, Device> deviceCache) {
        this.provider = provider;
        this.deviceCache = deviceCache;
//        provider.getDalEventBus().register(this);
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
                    synchronized (currentConnection){
                        currentConnection = req.getConnectionId();
                        provider.getDalEventBus().post(req);
                        try {
                            currentConnection.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }



    public void setEnabled(){
        enabled.set(true);
    }

    public void setDisabled(){
        enabled.set(false);
    }
}
