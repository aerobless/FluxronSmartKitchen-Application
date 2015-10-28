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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
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
    private IEventBusProvider provider;
    private MessageFactory messageFactory;
    private BluetoothAdapter btAdapter = null;
    private final Map<String, BTConnectionThread> connectionMap;
    private final Queue<String> connectionQueue;

    private static final String TAG = "FLUXRON";
    private static final int READ_TIMEOUT_IN_SECONDS = 1;
    private static final int MAX_CONCURRENT_CONNECTIONS = 3;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //well-known

    public Bluetooth(IEventBusProvider provider, Context context) {
        this.provider = provider;
        this.provider.getDalEventBus().register(this);

        messageFactory = new MessageFactory();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        connectionMap = new HashMap<String, BTConnectionThread>();
        connectionQueue = new LinkedList<String>();
        setupDiscovery(context);
    }

    /**
     * Sets up a BroadcastReceiver to listen to BluetoothDevice.ACTION_FOUND.
     * When a device is found a message is sent to the DAL-BL event bus.
     * @param context
     */
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

    /**
     * Starts/Stops the discovery of new devices via bluetooth.
     * @param cmd
     */
    public void onEventAsync(BluetoothDiscoveryCommand cmd) {
        if(cmd.isEnabled()){
            startDeviceDiscovery();
        } else {
            stopDeviceDiscovery();
        }
    }

    /**
     * Connects to a bluetooth device and reads the field specified in the command.
     * @param cmd
     */
    public void onEventAsync(BluetoothReadRequest cmd) {
        if(bluetoothEnabled()){
            stopDeviceDiscovery();

            BluetoothDevice device = btAdapter.getRemoteDevice(cmd.getAddress());

            BTConnectionThread connectionThread = getConnection(device, false);

            byte[] message = messageFactory.makeReadRequest(cmd.getField());
            messageFactory.printUnsignedByteArray(message);

            boolean retry = false;
            try {
                connectionThread.write(message);
            } catch (IOException e) {
                if(e.getMessage().equals("Broken pipe")){
                    Log.d(TAG, "Broken pipe");
                    retry = true;
                }else {
                    e.printStackTrace();
                }
            }
            if(retry){
                connectionThread = getConnection(device, true);
                try {
                    connectionThread.write(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get an existing connection to a device or establish a new one.
     * @param device
     * @return BTConnectionThread for the specified device
     */
    private BTConnectionThread getConnection(BluetoothDevice device, boolean clean){
        if(clean){
            synchronized (connectionMap){
                connectionMap.remove(device.getAddress());
            }
            synchronized (connectionQueue){
                connectionQueue.remove(device.getAddress());
            }
        }
        BTConnectionThread connectionThread = connectionMap.get(device.getAddress());
        if(connectionThread == null){
            try {
                BluetoothSocket btSocket = createBluetoothSocket(device);
                if(connectSocket(btSocket)){
                    connectionThread = new BTConnectionThread(btSocket, provider);
                    connectionThread.start();

                    String deadConnection = null;
                    synchronized (connectionQueue){
                        connectionQueue.add(device.getAddress());
                        if(connectionQueue.size() > MAX_CONCURRENT_CONNECTIONS){
                            Log.d(TAG, "Killing a connection to make room for a new one");
                            deadConnection = connectionQueue.poll();
                        }
                    }
                    synchronized (connectionMap) {
                        if (deadConnection != null) {
                            connectionMap.get(deadConnection).end();
                            connectionMap.remove(deadConnection);
                            connectionMap.put(device.getAddress(), connectionThread);
                        }
                        connectionMap.put(device.getAddress(), connectionThread);
                    }
                    return connectionThread;
                } else {
                    Log.d(TAG, "Unable to connect to remote device. Are you sure it is turned on and noone else is connected to it?");
                }
            } catch (IOException e) {
                Log.d(TAG, "In onResume() and socket create failed");
                e.printStackTrace();
            }
        }
        return connectionThread;
    }

    /**
     * Handles (verifies, interprets) messages received from BTConnectionThread and sends them on to the business layer.
     * @param msg
     */
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

                String field = Integer.toHexString(0xFF & data[4])+Integer.toHexString(0xFF & data[3])+"sub"+Integer.toHexString(0xFF & data[5]);
                provider.getDalEventBus().post(new BluetoothDeviceChanged(msg.getAddress(), messageFactory.getFieldname(field), decodeByteArray(dataPayload)));
            }
        } else {
            Log.d(TAG, "Invalid checksum!");
        }
    }

    /**
     * Used to retrive data from Bluetooth messages that are longer then 12Bytes (>4Byte Data).
     * These messages do not follow the CANopen specification. Instead Field 6 tells the
     * additional length after then normal CANopen message (12B).
     * @param input
     * @return byte[] Array containing only the data part of the input-Array.
     */
    private byte[] retriveBigData(byte[] input){
        byte [] subArray = Arrays.copyOfRange(input, 9, input.length - 3);
        if(subArray.length != input[6]){
            Log.d(TAG, "Length of extracted data doesn't match specified length!");
        }
        return subArray;
    }

    /**
     * Decodes little endian byte[] arrays to int values.
     * @param input
     * @return decoded Int value of the input
     */
    private int decodeByteArray(byte[] input){
        ByteBuffer buffer = ByteBuffer.wrap(input);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
    }

    private boolean connectSocket(BluetoothSocket btSocket) {
        Log.d(TAG, "Trying to connect to " + btSocket.getRemoteDevice().getAddress());
        try {
            btSocket.connect();
            Log.d(TAG, "Connection to " + btSocket.getRemoteDevice().getAddress() + " established");
            return true;
        } catch (IOException e) {
            Log.d(TAG, "Connection to " + btSocket.getRemoteDevice().getAddress() + " failed.");
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.d(TAG, "In onResume() and unable to close socket during connection failure");
                e.printStackTrace();
            }
        }
        return false;
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
        if(bluetoothEnabled()){
            stopDeviceDiscovery();
            btAdapter.startDiscovery();
        }
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
                return true;
            } else {
                //TODO: Prompt user to turn on Bluetooth
                Log.d(TAG, "Bluetooth not turned on");
            }
        }
        return false;
    }
}