package ch.fluxron.fluxronapp.model;

import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothWriteRequest;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.LoadObjectByTypeCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothTestCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceLoaded;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.objectBase.DeviceParameter;

/**
 * Manages & caches bluetooth devices.
 */
public class DeviceManager {
    private IEventBusProvider provider;
    private Map<String, Device> deviceMap;
    //TODO: Proper Device Cache

    //Fluxron Demo Devices
    public static final String FLX_GTZ_196_ADDRESS = "00:13:04:12:06:20";
    public static final String FLX_BAX_5206_ADDRESS = "30:14:10:31:11:85";
    public static final String HMSoft_ADDRESS = "00:0E:0E:00:A8:A2";

    public DeviceManager(IEventBusProvider provider) {
        this.provider = provider;
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
        deviceMap = new HashMap<String, Device>();
    }

    public void onEventAsync(BluetoothTestCommand msg){
        //String cmd = ParamManager.F_MANUFACTURER_DEVICE_NAME_1008;
        //provider.getDalEventBus().post(new BluetoothReadRequest(msg.getDeviceID(), cmd));

        String cmd = ParamManager.F_INHIBIT_TIME_1400SUB3;
        provider.getDalEventBus().post(new BluetoothWriteRequest(msg.getDeviceID(), cmd, 55));

        try {
            Thread.sleep(1000*2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        provider.getDalEventBus().post(new BluetoothReadRequest(msg.getDeviceID(), cmd));

    }

    /**
     * Enables or disables the discovery of bluetooth devices.
     * @param msg
     */
    public void onEventAsync(ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothDiscoveryCommand msg){
        loadDevices();
        provider.getDalEventBus().post(new BluetoothDiscoveryCommand(msg.isEnabled()));
        //Send all stored devices up
        for (Device d:deviceMap.values()){
           // provider.getUiEventBus().post(new DeviceLoaded(d));
        }
    }

    /**
     * Handles BluetoothDeviceChanged event. Stores device to DB and notifies UI
     * @param msg
     */
    public void onEventAsync(BluetoothDeviceChanged msg){
        Log.d("FLUXRON", "Device " + msg.getAddress() + " has reported " + msg.getValue() + " for field " + msg.getField());
        Device device = deviceMap.get(msg.getAddress());
        device.setDeviceParameter(new DeviceParameter(msg.getField(), msg.getValue()+""));
        saveDevice(device);
        provider.getUiEventBus().post(new DeviceChanged(deviceMap.get(msg.getAddress())));

        /*
        if(msg.getField().equals(ParamManager.F_MANUFACTURER_DEVICE_NAME_1008)){
            Device device = deviceMap.get(msg.getAddress());
            device.setCategory(msg.getValue()+"");
            saveDevice(device);
            provider.getUiEventBus().post(new DeviceChanged(deviceMap.get(msg.getAddress())));
        }*/
    }

    /**
     * Handles BluetoothDeviceFound event. Validates devices, stores it to DB and notifies UI
     * @param msg
     */
    public void onEventAsync(BluetoothDeviceFound msg){
        Device device = msg.getDevice();
        if(device != null && isFluxronDevice(device.getName())){
            Log.d("FLUXRON", "New Device found: " + device.getName() + " " + device.getAddress());
            saveDevice(device);
            provider.getUiEventBus().post(new DeviceLoaded(device));
            //provider.getDalEventBus().post(new BluetoothReadRequest(device.getAddress(), ParamManager.F_MANUFACTURER_DEVICE_NAME_1008));
        }
    }

    /**
     * Save a device to Cache & DB.
     * @param device
     */
    private void saveDevice(Device device) {
        device.setLastContact(new Date());

        //TODO: Device Cache
        synchronized (deviceMap){
            deviceMap.put(device.getAddress(), device);
        }
        //Send to DB
        SaveObjectCommand cmd = new SaveObjectCommand();
        cmd.setData(device);
        cmd.setDocumentId(device.getAddress());
        provider.getDalEventBus().post(cmd);
    }

    /**
     * Request to load devices from DB.
     */
    private void loadDevices(){
        provider.getDalEventBus().post(new LoadObjectByTypeCommand("device"));
    }

    /**
     * Handles devices loaded from the DB.
     * @param msg
     */
    public void onEventAsync(ObjectLoaded msg){
        if (msg.getData() instanceof Device) {
            Log.d("FLUXRON", "Device loaded from DB "+((Device) msg.getData()).getName());
            synchronized (deviceMap){
                deviceMap.put(((Device) msg.getData()).getAddress(), (Device) msg.getData());
            }
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
