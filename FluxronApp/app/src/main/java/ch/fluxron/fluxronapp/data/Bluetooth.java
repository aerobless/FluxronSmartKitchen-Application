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

    // SPP UUID service (well-known UUID for SPP Boards)
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Bluetooth Device MAC
    private static String address = "00:13:04:12:06:20";

    public Bluetooth(IEventBusProvider provider) {
        this.provider = provider;
        this.provider.getDalEventBus().register(this);
    }

    public void onEventAsync(BluetoothDiscoveryRequest msg) {
        btAdapter = setupBluetooth();
        listPairedDevices();

        //Connection
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

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
        } catch (IOException ed) {
            ed.printStackTrace();
        }

        Log.d(TAG, "Sending Message");
        //Example Read Message
        //AA AA 40 01 30 01 00 00 00 00 72 00
        byte[] message = {(byte)0x40, (byte)0x01, (byte)0x30, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00};
        generateMessage(message);
        try {
            outStream.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "all done.");
    }

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