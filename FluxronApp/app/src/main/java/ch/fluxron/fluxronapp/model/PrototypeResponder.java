package ch.fluxron.fluxronapp.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ch.fluxron.fluxronapp.events.modelDal.BluetoothDiscoveryRequest;
import ch.fluxron.fluxronapp.events.modelDal.BluetoothDiscoveryResponse;
import ch.fluxron.fluxronapp.events.modelDal.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.SaveKitchenCommand;

/**
 * Responds to a message. FOR PROTOTYPE USAGE ONLY!!!
 */
public class PrototypeResponder {

    private IEventBusProvider provider;

    public PrototypeResponder(IEventBusProvider provider) {
        this.provider = provider;
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
    }

    public void onEventAsync(SaveKitchenCommand msg) {
        SaveObjectCommand cmd = new SaveObjectCommand();
        cmd.setData(msg.getKitchen());
        provider.getDalEventBus().post(cmd);
    }

    public void onEventAsync(BluetoothDiscoveryRequest msg) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            //MSG: Device does not support Bluetooth
        }
        if (!bluetoothAdapter.isEnabled()) {
            //MSG: Please enable Bluetooth
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        List<String> s = new ArrayList<String>();
        for(BluetoothDevice device : pairedDevices){
            s.add(device.getName());
            BluetoothDiscoveryResponse response = new BluetoothDiscoveryResponse(device.getName(), device.getAddress());
            provider.getDalEventBus().post(response);
        }
        bluetoothAdapter.startDiscovery();
    }
}
