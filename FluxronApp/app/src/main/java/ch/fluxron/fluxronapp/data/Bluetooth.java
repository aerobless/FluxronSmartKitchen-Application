package ch.fluxron.fluxronapp.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import ch.fluxron.fluxronapp.events.modelDal.BluetoothDiscoveryRequest;

/**
 * Listens to eventbus messages. Provides access to bluetooth devices.
 */
public class Bluetooth {
    private IEventBusProvider provider;
    private BluetoothAdapter btAdapter;
    private BluetoothSPP btSPP;

    private static final String TAG = "FLUXRON";

    // SPP UUID service (well-known UUID for SPP Boards)
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Bluetooth Device MAC
    private static String address = "00:13:04:12:06:20";

    public Bluetooth(IEventBusProvider provider, BluetoothSPP bt) {
        this.provider = provider;
        this.provider.getDalEventBus().register(this);
        this.btSPP = bt;
    }

    public void onEventAsync(BluetoothDiscoveryRequest msg) {

        btSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Log.d(TAG, "connected");
            }

            public void onDeviceDisconnected() {
                Log.d(TAG, "disconnected");
                // Do something when connection was disconnected
            }

            public void onDeviceConnectionFailed() {
                Log.d(TAG, "connection failed");
                // Do something when connection failed
            }
        });

        btSPP.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if (state == BluetoothState.STATE_CONNECTED) {
                    Log.d(TAG, "successfully connected");
                    //AA AA 40 18 10 04 00 00 00 00 6C 00
                    Log.d(TAG, "sending message");
                    btSPP.send(new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0x18,
                            (byte) 0x10, (byte) 0x04, (byte) 0x00, (byte) 0x00,
                            (byte) 0x00, (byte) 0x00, (byte) 0x6C, (byte) 0x00}, true);
                    btSPP.send("Hello world, plz answer..", true);
                    btSPP.send(new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0x18,
                            (byte) 0x10, (byte) 0x04, (byte) 0x00, (byte) 0x00,
                            (byte) 0x00, (byte) 0x00, (byte) 0x6C, (byte) 0x00}, false);
                    Log.d(TAG, "message sent.. awaiting response");
                }
                // Do something when successfully connected
                else if (state == BluetoothState.STATE_CONNECTING)
                    Log.d(TAG, "trying to connect");
                    // Do something while connecting
                else if (state == BluetoothState.STATE_LISTEN)
                    Log.d(TAG, "waiting for connection");
                    // Do something when device is waiting for connection
                else if (state == BluetoothState.STATE_NONE)
                    Log.d(TAG, "no connection");
                // Do something when device don't have any connection
            }
        });

        btSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                // Do something when data incoming
                Log.d(TAG, "data incomming: " + message + " " + data);
            }
        });

        btSPP.setupService();
        btSPP.startService(BluetoothState.DEVICE_OTHER);

        btSPP.connect(address);
    }

    //TODO: add unit test
    //TODO: need byte[] instead of Byte[] ..
    private Byte[] generateMessage(byte[] canMessage){
        List<Byte> msgBuilder = new ArrayList<Byte>();
        msgBuilder.add((byte)0xAA);
        msgBuilder.add((byte)0xAA);
        int checksum = 0;
        for (byte b : canMessage) {
            msgBuilder.add(b);
            checksum += b;
        }
        msgBuilder.add((byte)checksum);
        Byte[] msgArray = msgBuilder.toArray(new Byte[msgBuilder.size()]);
        return msgArray;
    }

    private BluetoothAdapter setupBluetooth(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter == null) {
            //TODO: Handle error, no Bluetooth Adapter
            Log.d(TAG, "No Bluetooth adapter");
        }

        if (!btAdapter.isEnabled()) {
            //TODO: Offer user to enable Bluetooth.
            Log.d(TAG, "Bluetooth not enabled");
        }
        return btAdapter;
    }

    private List<String> listPairedDevices(){
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        List<String> s = new ArrayList<String>();

        if (pairedDevices != null) {
            for(BluetoothDevice device : pairedDevices){
                //Example: FLX_GTZ_196 00:13:04:12:06:20
                Log.d(TAG, device.getName()+" "+device.getAddress());
                s.add(device.getName()+" "+device.getAddress());
            }
            return s;
        } else {
            Log.d(TAG, "No paird devices found");
            return null;
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

}