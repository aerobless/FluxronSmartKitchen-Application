package ch.fluxron.fluxronapp.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ch.fluxron.fluxronapp.events.modelDal.BluetoothConnectCommand;
import ch.fluxron.fluxronapp.events.modelDal.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.BluetoothDiscoveryCommand;

/**
 * Listens to eventbus messages. Provides access to bluetooth devices.
 */
public class Bluetooth {
    private IEventBusProvider provider;

    private static final String TAG = "FLUXRON";

    //Bluetooth Device MAC
    private static final String FLX_GTZ_196_ADDRESS = "00:13:04:12:06:20";
    private static final String FLX_BAX_5206_ADDRESS = "30:14:10:31:11:85";
    private static final String HMSoft_ADDRESS = "00:0E:0E:00:A8:A2";

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private ConnectedThread mConnectedThread;
    private String connectedDeviceAddress = null;

    // SPP UUID service (well-known)
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public Bluetooth() {
    }

    public Bluetooth(IEventBusProvider provider, Context context) {
        this.provider = provider;
        this.provider.getDalEventBus().register(this);
        setupDiscovery(context);
    }

    private void setupDiscovery(Context context){
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    provider.getDalEventBus().post(new BluetoothDeviceFound(new Date(), device.getName(), device.getAddress()));
                }
            }
        };
        IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, ifilter);
    }

    public void onEventAsync(BluetoothDiscoveryCommand cmd) {
        if(cmd.isEnabled()){
            discoverPairedDevices();
            startDeviceDiscovery();
        } else {
            stopDeviceDiscovery();
        }
    }

    public void onEventAsync(BluetoothConnectCommand cmd) {
        byte[] message = generateMessage(new byte[]{
                (byte) 0x40, (byte) 0x18, (byte) 0x10, (byte) 0x04,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothEnabled() && (connectedDeviceAddress==null)){
            stopDeviceDiscovery();

            BluetoothDevice device = btAdapter.getRemoteDevice(cmd.getAddress());
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                errorExit(TAG, "In onResume() and socket create failed");
                e.printStackTrace();
            }

            Log.d(TAG, "Trying to connect");
            try {
                btSocket.connect();
                Log.d(TAG, "Connection ok");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    errorExit(TAG, "In onResume() and unable to close socket during connection failure");
                    e.printStackTrace();
                }
            }
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
            mConnectedThread.write(message);
        } else if(bluetoothEnabled() && (connectedDeviceAddress==cmd.getAddress()) && (mConnectedThread!=null)){
            Log.d(TAG, "Sending message to already connected device");
            mConnectedThread.write(message);
        } else if(bluetoothEnabled() && (connectedDeviceAddress!=cmd.getAddress())){
            //TODO: establish new connection because the device has changed.
        }
    }

    private void discoverPairedDevices(){
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        List<String> s = new ArrayList<String>();
        if (pairedDevices != null) {
            for(BluetoothDevice device : pairedDevices){
                provider.getDalEventBus().post(new BluetoothDeviceFound(new Date(), device.getName(), device.getAddress()));
            }
        }
    }

    private void startDeviceDiscovery(){
        stopDeviceDiscovery();
        btAdapter.startDiscovery();
    }

    private void stopDeviceDiscovery(){
        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }
    }

    //TODO: add unit test
    /*
     * Message Format:
     * Byte 0: 0xAA Startsequence
     * Byte 1: 0xAA Startsequence
     * Byte 2..9: CAN 8 Byte CAN Message
     * Byte 10: Check LB Low Byte Checksum
     * Byte 11: Check HB High Byte Checksum
     */
    public byte[] generateMessage(byte[] canMessage){
        byte[] message = new byte[12];
        message[0] = (byte)0xAA;
        message[1] = (byte)0xAA;
        int checksumLow = 0;
        int checksumHigh = 0;
        for (int c = 0; c < canMessage.length; c++) {
            message[c+2] = canMessage[c];
            if(c<4){
                checksumLow += canMessage[c];
            } else {
                checksumHigh += canMessage[c];
            }
        }
        message[10] = (byte)checksumLow;
        message[11] = (byte)checksumHigh;

        printUnsignedByteArray(message);
        return message;
    }

    private void printUnsignedByteArray(byte[] message) {
        String hexMessage = "";
        for (int i = 0; i < message.length; i++) {
            hexMessage = hexMessage+Integer.toHexString(0xFF & message[i])+" ";
        }
        Log.d(TAG, hexMessage);
    }

    private void errorExit(String title, String message){
        Log.d(TAG, title+" "+message);
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
                e.printStackTrace();
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private boolean bluetoothEnabled() {
        if(btAdapter==null) {
            errorExit(TAG, "Bluetooth not supported (Are you running on emulator?)");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "Bluetooth is on");
                return true;
            } else {
                //TODO: Prompt user to turn on Bluetooth
                Log.d(TAG, "Bluetooth not turned on");
            }
        }
        return false;
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[16]; //TODO: size
            int nofBytes;

            while (true) {
                try {
                    nofBytes = mmInStream.read(buffer);
                    Log.d(TAG, nofBytes+" Bytes received!!!");
                    printUnsignedByteArray(buffer);
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(byte[] message) {
            Log.d(TAG, "Sending message");
            try {
                mmOutStream.write(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}