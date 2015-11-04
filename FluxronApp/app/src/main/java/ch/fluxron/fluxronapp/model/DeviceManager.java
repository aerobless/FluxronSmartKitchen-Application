package ch.fluxron.fluxronapp.model;

import android.util.Log;
import android.util.LruCache;

import java.util.Date;
import java.util.Map;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothTestCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceLoaded;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceParamRequestCommand;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.objectBase.DeviceParameter;

/**
 * Manages & caches bluetooth devices.
 */
public class DeviceManager {
    private IEventBusProvider provider;
    private LruCache<String, Device> deviceCache;
    //TODO: Proper Device Cache

    //Fluxron Demo Devices
    public static final String FLX_GTZ_196_ADDRESS = "00:13:04:12:06:20";
    public static final String FLX_BAX_5206_ADDRESS = "30:14:10:31:11:85";
    public static final String HMSoft_ADDRESS = "00:0E:0E:00:A8:A2";

    public DeviceManager(IEventBusProvider provider) {
        this.provider = provider;
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
        deviceCache = new LruCache<>(150);
    }

    public void onEventAsync(BluetoothTestCommand msg){
        String cmd = ParamManager.F_PRODUCT_CODE_1018SUB2;
        provider.getDalEventBus().post(new BluetoothReadRequest(msg.getDeviceID(), cmd));

        /*
        //Writing to device test
        String cmd = ParamManager.F_INHIBIT_TIME_1400SUB3;
        provider.getDalEventBus().post(new BluetoothWriteRequest(msg.getDeviceID(), cmd, 55));

        try {
            Thread.sleep(1000*2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        provider.getDalEventBus().post(new BluetoothReadRequest(msg.getDeviceID(), cmd));
        */
    }

    /**
     * Enables or disables the discovery of bluetooth devices.
     * @param msg
     */
    public void onEventAsync(ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothDiscoveryCommand msg){
        provider.getDalEventBus().post(new BluetoothDiscoveryCommand(msg.isEnabled()));

        //Send all cached devices up
        Map<String, Device> deviceCacheSnapshot;
        synchronized (deviceCache){
            deviceCacheSnapshot = deviceCache.snapshot();
        }
        for (Device d: deviceCacheSnapshot.values()){
           provider.getUiEventBus().post(new DeviceLoaded(d));
        }
    }

    /**
     * Handles BluetoothDeviceChanged event. Stores device to DB and notifies UI
     * @param msg
     */
    public void onEventAsync(BluetoothDeviceChanged msg){
        Log.d("FLUXRON", "Device " + msg.getAddress() + " has reported " + msg.getValue() + " for field " + msg.getField());
        Device device;
        synchronized (deviceCache){
            device = deviceCache.get(msg.getAddress());
            device.setDeviceParameter(new DeviceParameter(msg.getField(), msg.getValue()+""));
        }
        updateDeviceCache(device);
        provider.getUiEventBus().post(new DeviceChanged(deviceCache.get(msg.getAddress())));
    }

    /**
     * Handles BluetoothDeviceFound event. Validates devices, stores it to DB and notifies UI
     * @param msg
     */
    public void onEventAsync(BluetoothDeviceFound msg){
        Device device = msg.getDevice();
        if(device != null && isFluxronDevice(device.getName())){
            boolean cached;
            synchronized (deviceCache){
                cached = deviceCache.get(device.getAddress())!=null;
            }
            if(!cached){
                Log.d("FLUXRON", "New Device found: " + device.getName() + " " + device.getAddress());
                updateDeviceCache(device);
                provider.getUiEventBus().post(new DeviceLoaded(device));
                Log.d("FLUXRON", "BOND STATUS: " +device.isBonded());
                //provider.getDalEventBus().post(new BluetoothReadRequest(device.getAddress(), ParamManager.F_MANUFACTURER_DEVICE_NAME_1008));
            }
            /*synchronized (deviceCache){
                //TODO: handle unbonded devices?
                if(deviceCache.get(device.getAddress()).getDeviceType()==Device.UNKNOWN_DEVICE_TYPE && device.isBonded()){
                    provider.getDalEventBus().post(new BluetoothReadRequest(device.getAddress(), ParamManager.F_PRODUCT_CODE_1018SUB2));
                }
            }*/
        }
    }

    public void onEventAsync(DeviceParamRequestCommand cmd){
        //TODO: check if cached?
        //TODO: make sure that device is bonded first?
        provider.getDalEventBus().post(new BluetoothReadRequest(cmd.getDeviceID(), cmd.getParamID()));
    }

    /**
     * Save a device to Cache & DB.
     * @param device
     */
    private void updateDeviceCache(Device device) {
        device.setLastContact(new Date());

        //TODO: Device Cache
        synchronized (deviceCache){
            deviceCache.put(device.getAddress(), device);
        }
    }

    /**
     * Light filter to prevent non-fluxron devices from getting listed.
     * @param deviceName
     * @return true if deviceName starts with FLX, DGL, HC-06 or HM-Soft, which identifies it as a potential Fluxron Device.
     */
    public boolean isFluxronDevice(String deviceName){
        return deviceName.matches("(FLX|DGL|HC-06|HMSoft).*");
    }
}
