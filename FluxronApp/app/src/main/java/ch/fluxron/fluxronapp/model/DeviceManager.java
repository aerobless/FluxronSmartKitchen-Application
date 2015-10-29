package ch.fluxron.fluxronapp.model;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.LoadObjectByTypeCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothTestCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceLoaded;
import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Manages bluetooth devices.
 */
public class DeviceManager {
    private IEventBusProvider provider;
    private Map<String, Device> deviceMap;

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
        String cmd = ParamManager.F_SERIAL_NUMBER_1018SUB4;
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
            Log.d("FLUXRON","SENT DEVICE UP!");
            provider.getUiEventBus().post(new DeviceLoaded(d));
        }
    }

    public void onEventAsync(BluetoothDeviceChanged msg){
        Log.d("FLUXRON", "Device " + msg.getAddress() + " has reported " + msg.getValue() + " for field " + msg.getField());
    }

    /**
     * Handles BluetoothDeviceFound event. Validates device and stores it in the DB.
     * @param msg
     */
    public void onEventAsync(BluetoothDeviceFound msg){
        if(isFluxronDevice(msg.getName())){
            Device device = new Device(msg.getName(), msg.getAddress());
            Log.d("FLUXRON", "New Device found: " + msg.getName() + " " + msg.getAddress());
            SaveObjectCommand cmd = new SaveObjectCommand();
            cmd.setData(device);
            cmd.setDocumentId(msg.getAddress());
            provider.getDalEventBus().post(cmd);
            synchronized (deviceMap){
                deviceMap.put(msg.getAddress(), device);
            }
            provider.getUiEventBus().post(new DeviceLoaded(device));
        }
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
        if(deviceName.matches("(FLX|DGL|HC-06|HMSoft).*")){
            return true;
        }
        return false;
    }
}
