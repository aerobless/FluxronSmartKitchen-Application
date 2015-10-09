package ch.fluxron.fluxronapp.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ch.fluxron.fluxronapp.events.modelDal.BluetoothDiscoveryRequest;

/**
 * Listens to eventbus messages. Provides access to bluetooth devices.
 */
public class Bluetooth {
    private IEventBusProvider provider;
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private OutputStream outStream;

    private static final String TAG = "FLUXRON";

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Bluetooth Device MAC
    private static String address = "00:13:04:12:06:20";

    public Bluetooth(IEventBusProvider provider) {
        this.provider = provider;
        this.provider.getDalEventBus().register(this);
    }

    public void onEventAsync(BluetoothDiscoveryRequest msg) {
        btAdapter = setupBluetooth();
        /*Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        List<String> s = new ArrayList<String>();

        BluetoothDevice flux = null;
        if (pairedDevices != null) {
            for(BluetoothDevice device : pairedDevices){
                Log.d(TAG, device.getName()+" "+device.getAddress());
                //Example: FLX_GTZ_196 00:13:04:12:06:20
                s.add(device.getName());
                if(device.getAddress().equals(address)){
                    flux = device;
                    Log.d(TAG, "Device found");
                    break;
                }
            }
        } else {
            //TODO: Handle case when there are no paired devices
            Log.d(TAG, "No paird devices found");
        }*/

        //Connection
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //btAdapter.cancelDiscovery();

        Log.d(TAG, "Connecting");
        try {
            btSocket.connect();
            Log.d(TAG, "Connection ok");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
               e2.printStackTrace();
            }
        }

        Log.d(TAG, "Creating Socket");
        try {
            outStream = btSocket.getOutputStream();
            byte[] msgBuffer = "Hello world".getBytes();
            outStream.write(msgBuffer);
            outStream.flush();
            outStream.close();
            btSocket.close();
        } catch (IOException ed) {
            ed.printStackTrace();
        }

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private BluetoothAdapter setupBluetooth(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter != null) {
            // Continue with bluetooth setup.
        } else {
            //TODO: Handle error, no Bluetooth Adapter
            Log.d(TAG, "No Bluetooth adapter");
        }

        if (btAdapter.isEnabled()) {
            // Enabled. Work with Bluetooth.
        } else {
            //TODO: Offer user to enable Bluetooth.
            Log.d(TAG, "Bluetooth not enabled");
        }
        return btAdapter;
    }

}