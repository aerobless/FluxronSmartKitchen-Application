package ch.fluxron.fluxronapp.model;

import android.util.Log;

import ch.fluxron.fluxronapp.data.Bluetooth;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothConnectCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelUi.BluetoothTestCommand;

/**
 * Manages bluetooth devices.
 */
public class DeviceManager {
    private IEventBusProvider provider;

    public DeviceManager(IEventBusProvider provider) {
        this.provider = provider;
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
    }

    public void onEventAsync(BluetoothTestCommand msg){
        //provider.getDalEventBus().post(new BluetoothDiscoveryCommand(true));
        provider.getDalEventBus().post(new BluetoothConnectCommand(Bluetooth.FLX_GTZ_196_ADDRESS, Bluetooth.DEMO_MESSAGE));
        //provider.getDalEventBus().post(new BluetoothConnectCommand(Bluetooth.FLX_BAX_5206_ADDRESS, Bluetooth.SERIAL_NUMBER));
    }

    public void onEventAsync(BluetoothDeviceFound msg){
        //TODO: send to GUI
        Log.d("FLUXRON", "Got BluetoothDeviceFound Message: " + msg.getName() + " " + msg.getAddress());
    }
}
