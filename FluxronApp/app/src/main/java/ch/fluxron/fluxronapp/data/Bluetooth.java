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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothConnectCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;

/**
 * Listens to eventbus messages. Provides access to bluetooth devices.
 */
public class Bluetooth {
    private IEventBusProvider provider;

    //Fluxron Demo Devices
    public static final String FLX_GTZ_196_ADDRESS = "00:13:04:12:06:20";
    public static final String FLX_BAX_5206_ADDRESS = "30:14:10:31:11:85";
    public static final String HMSoft_ADDRESS = "00:0E:0E:00:A8:A2";

    //Messages
    public static final byte[] DEMO_MESSAGE = new byte[]{
            (byte) 0x40, (byte) 0x18, (byte) 0x10, (byte) 0x04,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    public static final byte[] PRODUCT_CODE = new byte[]{
            (byte) 0x03, (byte) 0xFA, (byte) 0x02, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    public static final byte[] SERIAL_NUMBER = new byte[]{
            (byte) 0x40, (byte) 0xfa, (byte) 0x03, (byte) 0x04,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    private static final String TAG = "FLUXRON";
    private static final int READ_TIMEOUT_IN_SECONDS = 1;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //well-known

    private BluetoothAdapter btAdapter = null;
    public Bluetooth() {}

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
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothEnabled()){
            stopDeviceDiscovery();

            BluetoothDevice device = btAdapter.getRemoteDevice(cmd.getAddress());
            try {
                BluetoothSocket btSocket = createBluetoothSocket(device);
                connectSocket(btSocket);
                ConnectedThread mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();
                mConnectedThread.write(generateChecksum(cmd.getMessage()));

                setConnectionTimeout(mConnectedThread, READ_TIMEOUT_IN_SECONDS);
            } catch (IOException e) {
                Log.d(TAG, "In onResume() and socket create failed");
                e.printStackTrace();
            }
        }
    }

    private void connectSocket(BluetoothSocket btSocket) {
        Log.d(TAG, "Trying to connect to " + btSocket.getRemoteDevice().getAddress());
        try {
            btSocket.connect();
            Log.d(TAG, "Connection to "+btSocket.getRemoteDevice().getAddress()+" ok");
        } catch (IOException e) {
            Log.d(TAG, "Connection to "+btSocket.getRemoteDevice().getAddress()+" failed.");
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.d(TAG, "In onResume() and unable to close socket during connection failure");
                e.printStackTrace();
            }
        }
    }

    //Temporary setConnectionTimeout to keep reading thread from locking up the bluetooth adapter.
    private void setConnectionTimeout(ConnectedThread mConnectedThread, int time) {
        try {
            Thread.sleep(time*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mConnectedThread.keepRunning.set(false);
    }

    private void discoverPairedDevices(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothEnabled()) {
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            if (pairedDevices != null) {
                for (BluetoothDevice device : pairedDevices) {
                    provider.getDalEventBus().post(new BluetoothDeviceFound(new Date(), device.getName(), device.getAddress()));
                }
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

    /*
     * Message Format:
     * Byte 0: 0xAA Startsequence
     * Byte 1: 0xAA Startsequence
     * Byte 2..9: CAN 8 Byte CAN Message
     * Byte 10: Check LB Low Byte Checksum
     * Byte 11: Check HB High Byte Checksum
     */
    public byte[] generateChecksum(byte[] canMessage){
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

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, SPP_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
                e.printStackTrace();
            }
        }
        return  device.createRfcommSocketToServiceRecord(SPP_UUID);
    }

    private boolean bluetoothEnabled() {
        if(btAdapter==null) {
            Log.d(TAG, "Bluetooth not supported (Are you running on emulator?)");
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
        private final int msgLength = 12;
        private BluetoothSocket socket;
        public AtomicBoolean keepRunning = new AtomicBoolean(true);

        public ConnectedThread(BluetoothSocket btsocket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            socket = btsocket;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[128];
            int nofBytes;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

            while (keepRunning.get()) {
                try {
                    if(mmInStream.available() > 0){
                        nofBytes = mmInStream.read(buffer);
                        outputStream.write(buffer, 0, nofBytes);
                        Log.d(TAG, nofBytes + " Bytes received!!!");
                        if(outputStream.size() == msgLength){
                            printUnsignedByteArray(outputStream.toByteArray());
                            outputStream.reset();
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
            try {
                mmInStream.close();
                mmOutStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
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