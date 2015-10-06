package ch.fluxron.fluxronapp.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ch.fluxron.fluxronapp.eventsbase.IEventBusProvider;
import ch.fluxron.fluxronapp.modelevents.BluetoothDiscoveryRequest;
import ch.fluxron.fluxronapp.modelevents.BluetoothDiscoveryResponse;
import ch.fluxron.fluxronapp.modelevents.SimpleMessage;
import ch.fluxron.fluxronapp.modelevents.SimpleMessageResponse;

/**
 * Responds to a message. FOR PROTOTYPE USAGE ONLY!!!
 */
public class PrototypeResponder {

    private IEventBusProvider provider;

    public PrototypeResponder(IEventBusProvider provider) {
        this.provider = provider;
        provider.getEventBus().register(this);
    }

    public void onEventAsync(SimpleMessage msg) throws InterruptedException {
        for (int i =0; i < 30; i++) {
            SimpleMessageResponse response = new SimpleMessageResponse();
            response.setMessageText("hello " + i);
            provider.getEventBus().post(response);
            Thread.sleep(500);
        }
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
            provider.getEventBus().post(response);
        }
        bluetoothAdapter.startDiscovery();
    }
}
