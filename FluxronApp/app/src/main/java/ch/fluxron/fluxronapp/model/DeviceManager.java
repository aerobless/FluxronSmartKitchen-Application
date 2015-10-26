package ch.fluxron.fluxronapp.model;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelUi.BluetoothTestCommand;

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
        provider.getDalEventBus().post(new BluetoothDiscoveryCommand(true));
        String cmd = ParamManager.F_SERIAL_NUMBER1018SUB4;
        provider.getDalEventBus().post(new BluetoothReadRequest(FLX_GTZ_196_ADDRESS, cmd));
        provider.getDalEventBus().post(new BluetoothReadRequest(FLX_BAX_5206_ADDRESS, cmd));
    }

    public void onEventAsync(BluetoothDeviceChanged msg){
        Log.d("FLUXRON", "Device "+msg.getAddress()+" has reported "+msg.getValue()+" for field "+msg.getField());
    }

    public void onEventAsync(BluetoothDeviceFound msg){
        if(deviceMap.get(msg.getAddress())==null){
            deviceMap.put(msg.getAddress(), new Device(msg.getName(), msg.getAddress()));
            Log.d("FLUXRON", "New Device added: " + msg.getName() + " " + msg.getAddress());
        }
    }
}
