package ch.fluxron.fluxronapp.model;

import android.util.Log;
import android.util.LruCache;

import java.util.Date;
import java.util.Map;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothConnectionFailure;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothWriteRequest;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChangeCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceFailed;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceLoaded;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceParamRequestCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.InjectDevicesCommand;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.objectBase.DeviceParameter;
import ch.fluxron.fluxronapp.objectBase.DevicePosition;

/**
 * Manages & caches bluetooth devices.
 */
public class DeviceManager {
    private IEventBusProvider provider;
    private final LruCache<String, Device> deviceCache;
    private Map<String, ch.fluxron.fluxronapp.data.generated.DeviceParameter> paramMap;
    private CyclicRefresh cyclicRefresh;

    public DeviceManager(IEventBusProvider provider) {
        this.provider = provider;
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
        deviceCache = new LruCache<>(256);
        cyclicRefresh = new CyclicRefresh(provider, deviceCache);
        paramMap = new ParamManager().getParamMap();
    }

    /**
     * Used to inject devices loaded with a kitchen into the device manager.
     * @param cmd
     */
    public void onEventAsync(InjectDevicesCommand cmd){
        synchronized (deviceCache){
            deviceCache.evictAll();
            for(DevicePosition d:cmd.getDeviceList()){
                if(deviceCache.get(d.getDeviceId()) == null){
                    deviceCache.put(d.getDeviceId(), new Device(d.getName(), d.getDeviceId(), false));
                }
            }
        }
    }

    public void onEventAsync(DeviceChangeCommand cmd){
        //TODO: validate here
        String address = cmd.getAddress();
        String field = cmd.getChangeRequest().getName();
        int value = Integer.parseInt(cmd.getChangeRequest().getValue());
        //TODO: convert into correct object
        provider.getDalEventBus().post(new BluetoothWriteRequest(address, field, value));
    }

    /**
     * Enables or disables the discovery of bluetooth devices.
     * @param inputCmd
     */
    public void onEventAsync(ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothDiscoveryCommand inputCmd){
        RequestResponseConnection btDiscoveryCmd = new BluetoothDiscoveryCommand(inputCmd.isEnabled());
        btDiscoveryCmd.setConnectionId(inputCmd);
        provider.getDalEventBus().post(btDiscoveryCmd);

        //Send all cached devices up
        Map<String, Device> deviceCacheSnapshot;
        synchronized (deviceCache){
            deviceCacheSnapshot = deviceCache.snapshot();
        }
        for (Device d: deviceCacheSnapshot.values()){
            RequestResponseConnection deviceLoaded = new DeviceLoaded(d);
            deviceLoaded.setConnectionId(inputCmd);
           provider.getUiEventBus().post(deviceLoaded);
        }
    }

    /**
     * Handles BluetoothDeviceChanged event. Stores device in cache and notifies UI
     * @param inputMsg
     */
    public void onEventAsync(BluetoothDeviceChanged inputMsg){
        Log.d("FLUXRON", "Device " + inputMsg.getAddress() + " has reported " + inputMsg.getValue() + " for field " + inputMsg.getField());
        Device device;
        synchronized (deviceCache){
            device = deviceCache.get(inputMsg.getAddress());
            if(paramMap.get(device.getDeviceTypePrefix()+"_"+inputMsg.getField())!= null){
                device.setDeviceParameter(new DeviceParameter(device.getDeviceTypePrefix()+"_"+inputMsg.getField(), inputMsg.getValue() + ""));
            }
        }
        updateDeviceCache(device);
        RequestResponseConnection deviceChanged = new DeviceChanged(deviceCache.get(inputMsg.getAddress()));
        deviceChanged.setConnectionId(inputMsg);
        provider.getUiEventBus().post(deviceChanged);
    }

    /**
     * Handles BluetoothDeviceFound event. Validates devices, stores it to DB and notifies UI
     * @param inputMsg
     */
    public void onEventAsync(BluetoothDeviceFound inputMsg){
        Device device = inputMsg.getDevice();
        if(device != null && isFluxronDevice(device.getName())){
            boolean cached;
            synchronized (deviceCache){
                cached = deviceCache.get(device.getAddress())!=null;
            }
            if(!cached){
                Log.d("FLUXRON", "New Device found: " + device.getName() + " " + device.getAddress());
                updateDeviceCache(device);
                RequestResponseConnection deviceLoaded = new DeviceLoaded(device);
                deviceLoaded.setConnectionId(inputMsg);
                provider.getUiEventBus().post(deviceLoaded);
            }
        }
    }

    public void onEventAsync(DeviceParamRequestCommand inputCmd){
        //TODO: make sure that device is bonded first?
        BluetoothReadRequest readRequest = new BluetoothReadRequest(inputCmd.getDeviceID());
        readRequest.addParam(inputCmd.getParamID());
        readRequest.setConnectionId(inputCmd);
        provider.getDalEventBus().post(readRequest);
    }

    /**
     * Forwards device failures to the UI.
     * @param inputMsg
     */
    public void onEventAsync(BluetoothConnectionFailure inputMsg){
        provider.getUiEventBus().post(new DeviceFailed(inputMsg.getAddress()));
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
        return deviceName.matches("(FLX|DGL|HC-06|HMSoft|FAKE).*");
    }
}
