package ch.fluxron.fluxronapp.data;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import ch.fluxron.fluxronapp.events.modelDal.BluetoothDiscoveryRequest;

/**
 * Listens to eventbus messages. Provides access to bluetooth devices.
 */
public class Bluetooth {
    private IEventBusProvider provider;

    public Bluetooth(IEventBusProvider provider) {
        this.provider = provider;
        this.provider.getDalEventBus().register(this);
    }

    public void onEventAsync(BluetoothDiscoveryRequest msg) {
        Log.d("FLUXRON","Received BT Message");
        /*
        BluetoothAdapter btAdapter = setupBluetooth();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        List<String> s = new ArrayList<String>();
        for(BluetoothDevice device : pairedDevices){
            s.add(device.getName());
            BluetoothDiscoveryResponse response = new BluetoothDiscoveryResponse(device.getName(), device.getAddress());
            provider.getDalEventBus().post(response);
        }
        btAdapter.startDiscovery();*/
    }

    private BluetoothAdapter setupBluetooth(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter != null) {
            // Continue with bluetooth setup.
        } else {
            //TODO: Handle error, no Bluetooth Adapter
            Log.d("FLUXRON", "NO BLUETOOTH ADAPTER");
        }

        if (btAdapter.isEnabled()) {
            // Enabled. Work with Bluetooth.
        } else {
            //TODO: Offer user to enable Bluetooth.
            Log.d("FLUXRON", "BLUETOOTH NOT ENABLED");
        }
        return btAdapter;
    }

}