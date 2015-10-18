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
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;

/**
 * Listens to eventbus messages. Provides access to bluetooth devices.
 */
public class Bluetooth {

    //Known Fields
    public final static String F_DEVICE_TYPE = "1000";
    public final static String F_ERROR_REGISTER = "1001";
    public final static String F_IDENTITY = "1018";
    public final static String F_NOF_ENTRIES_IDENTITY = "1018sub0";
    public final static String F_VENDOR_ID = "1018sub1";
    public final static String F_PRODUCT_CODE = "1018sub2";
    public final static String F_REVISION_NUMBER = "1018sub3";
    public final static String F_SERIAL_NUMBER = "1018sub4";
    public final static String F_UNSIGNED8_0to15 = "A0";
    public final static String F_NOF_ENTRIES_UNSIGNED8 = "A0sub0";
    public final static String F_TYPE = "A0sub1";
    public final static String F_MINIMUM_VALUE = "A0sub2";
    public final static String F_MAXIMUM_VALUE = "A0sub3";
    public final static String F_MANUFACTURER_DEVICE_NAME = "1008";
    public final static String F_MANUFACTURER_HARDWARE_VERSION = "1009";
    public final static String F_MANUFACTURER_SOFTWARE_VERSION = "100A";
    public final static String F_KNOB_ANGLE_DIGITAL = "3001";
    public final static String F_NOF_ENTRIES_KNOB_ANGLE_DIGITAL = "3001sub0";
    public final static String F_KNOB_A_DIGITAL = "3001sub1";
    public final static String F_KNOB_B_DIGITAL = "3001sub2";
    public final static String F_KNOB_STATUS = "3001sub3";
    public final static String F_TEMPERATURE_SENSOR = "3028";
    public final static String F_NOF_ENTRIES_TEMPERATURE_SENSOR = "3028sub0";
    public final static String F_KMX_TEMPERATURE_0 = "3028sub1";
    public final static String F_KMX_TEMPERATURE_1 = "3028sub2";
    public final static String F_KMX_TEMPERATURE_2 = "3028sub3";
    public final static String F_KMX_TEMPERATURE_3 = "3028sub4";
    private IEventBusProvider provider;
    private MessageFactory messageFactory;
    private BluetoothAdapter btAdapter = null;

    //Messages
    public static final byte[] DEMO_MESSAGE = new byte[]{
            (byte) 0xAA, (byte) 0xAA,
            (byte) 0x40, (byte) 0x01, (byte) 0x30, (byte) 0x01,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x72, (byte) 0x00 };

    private static final String TAG = "FLUXRON";
    private static final int READ_TIMEOUT_IN_SECONDS = 1;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //well-known

    public Bluetooth(IEventBusProvider provider, Context context, Map<String, DeviceParameter> parameterList) {
        this.provider = provider;
        this.provider.getDalEventBus().register(this);
        messageFactory = new MessageFactory(parameterList);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        setupDiscovery(context);
    }

    private void setupDiscovery(Context context){
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //TODO: check if it's really a Fluxron device
                    provider.getDalEventBus().post(new BluetoothDeviceFound(new Date(), device.getName(), device.getAddress()));
                }
            }
        };
        IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, ifilter);
    }

    public void onEventAsync(BluetoothDiscoveryCommand cmd) {
        if(cmd.isEnabled()){
            //discoverPairedDevices();
            startDeviceDiscovery();
        } else {
            stopDeviceDiscovery();
        }
    }

    public void onEventAsync(BluetoothReadRequest cmd) {
        if(bluetoothEnabled()){
            stopDeviceDiscovery();

            BluetoothDevice device = btAdapter.getRemoteDevice(cmd.getAddress());
            try {
                BluetoothSocket btSocket = createBluetoothSocket(device);
                if(connectSocket(btSocket)){
                    ConnectedThread mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();
                    byte[] message = messageFactory.makeReadRequest(cmd.getField());
                    messageFactory.printUnsignedByteArray(message);
                    mConnectedThread.write(message);

                    setConnectionTimeout(mConnectedThread, READ_TIMEOUT_IN_SECONDS);
                } else {
                    Log.d(TAG, "Unable to connect to remote device. Are you sure it is turned on and noone else is connected to it?");
                }
            } catch (IOException e) {
                Log.d(TAG, "In onResume() and socket create failed");
                e.printStackTrace();
            }
        }
    }

    private boolean connectSocket(BluetoothSocket btSocket) {
        Log.d(TAG, "Trying to connect to " + btSocket.getRemoteDevice().getAddress());
        try {
            btSocket.connect();
            Log.d(TAG, "Connection to "+btSocket.getRemoteDevice().getAddress()+" ok");
            return true;
        } catch (IOException e) {
            Log.d(TAG, "Connection to "+btSocket.getRemoteDevice().getAddress()+" failed.");
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.d(TAG, "In onResume() and unable to close socket during connection failure");
                e.printStackTrace();
            }
        }
        return false;
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
        private final int msgLength = 17;
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
                            messageFactory.printUnsignedByteArray(outputStream.toByteArray());
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