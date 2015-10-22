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
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothMessageReceived;
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

    private static final String TAG = "FLUXRON";
    private static final int READ_TIMEOUT_IN_SECONDS = 1;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //well-known

    public Bluetooth(IEventBusProvider provider, Context context) {
        this.provider = provider;
        this.provider.getDalEventBus().register(this);

        messageFactory = new MessageFactory();
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
                    BTConnectionThread connectionThread = new BTConnectionThread(btSocket, provider);
                    connectionThread.start();
                    byte[] message = messageFactory.makeReadRequest(cmd.getField());
                    messageFactory.printUnsignedByteArray(message);
                    connectionThread.write(message);

                    setConnectionTimeout(connectionThread, READ_TIMEOUT_IN_SECONDS);
                } else {
                    Log.d(TAG, "Unable to connect to remote device. Are you sure it is turned on and noone else is connected to it?");
                }
            } catch (IOException e) {
                Log.d(TAG, "In onResume() and socket create failed");
                e.printStackTrace();
            }
        }
    }

    public void onEventAsync(BluetoothMessageReceived msg) {
        Log.d(TAG, "Message from " + msg.getAddress());
        byte[] data = msg.getData();
        byte[] dataPayload = null;
        messageFactory.printUnsignedByteArray(data);
        if(messageFactory.isChecksumValid(data)){
            Log.d(TAG, "and its checksum is valid.");
            if(data[2] == MessageFactory.CCD_READ_RESPONSE_1B){
                dataPayload = new byte[]{data[6]};
            } else if (data[2] == MessageFactory.CCD_READ_RESPONSE_2B){
                dataPayload = new byte[]{data[6],data[7]};
            } else if (data[2] == MessageFactory.CCD_READ_RESPONSE_3B){
                dataPayload = new byte[]{data[6],data[7],data[8]};
            } else if (data[2] == MessageFactory.CCD_READ_RESPONSE_4B){
                dataPayload = new byte[]{data[6],data[7],data[8],data[9]};
            } else if (data[2] == MessageFactory.CCD_WRITE_RESPONSE){
                //Doesn't contain data
            } else if (data[2] == MessageFactory.CCD_READ_REQUEST){
                dataPayload = retriveBigData(data);
            } else if (data[2] == MessageFactory.CCD_ERROR_RESPONSE){
                Log.d(TAG, "Received ERROR bluetooth message");
            } else {
                Log.d(TAG, "Unkown Command Code"+ data[2]);
            }
            if (dataPayload != null){
                //messageFactory.printUnsignedByteArray(dataPayload);
                //Log.d(TAG, "INT "+decodeByteArray(dataPayload));
                //TODO: nicer method to generate/lookup field name
                provider.getDalEventBus().post(new BluetoothDeviceChanged(msg.getAddress(), data[4]+data[3]+"sub"+data[5], decodeByteArray(dataPayload)));
            }
        } else {
            Log.d(TAG, "Invalid checksum!");
        }
    }

    /**
     * Used to retrive data from Bluetooth messages that are longer then 12Bytes (>4Byte Data).
     * These messages do not follow the CANopen specification. Instead Field 6 tells the
     * additional length after then normal CANopen message (12B).
     */
    private byte[] retriveBigData(byte[] input){
        byte [] subArray = Arrays.copyOfRange(input, 9, input.length-3);
        if(subArray.length != input[6]){
            Log.d(TAG, "Length of extracted data doesn't match specified length!");
        }
        return subArray;
    }

    private int decodeByteArray(byte[] input){
        ByteBuffer buffer = ByteBuffer.wrap(input);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
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
    private void setConnectionTimeout(BTConnectionThread connectionThread, int time) {
        try {
            Thread.sleep(time*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connectionThread.keepRunning.set(false);
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
}