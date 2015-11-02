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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothMessageReceived;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.objectBase.Device;

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
    private static final String DEVICE_PIN = "1234";
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
        setupBonding(context);
    }

    /**
     * Set the Bluetooth PIN of a device. This method should only be run after a attempt at bonding
     * has been made.
     * @param device
     */
    public void setDevicePin(BluetoothDevice device)
    {
        Log.d(TAG, "SETTING PIN FOR " + device.getName());
        try {
            byte[] pinBytes = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, DEVICE_PIN);
            Log.d(TAG, "Try to set the PIN");
            Method m = device.getClass().getMethod("setPin", byte[].class);
            m.invoke(device, pinBytes);
            Log.d(TAG, "Success to add the PIN.");
            try {
                device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                Log.d(TAG, "Success to setPairingConfirmation.");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            //TODO: proper exception
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
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
                    provider.getDalEventBus().post(new BluetoothDeviceFound(new Device(device.getName(), device.getAddress(),device.getBondState()==BluetoothDevice.BOND_BONDED)));
                }
            }
        };
        IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, ifilter);
    }

    /**
     * Sets up a BroadcastReceiver to listen to bonding events.
     * @param context
     */
    private void setupBonding(Context context){
        final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST"; //to support api18
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_PAIRING_REQUEST.equals(action)){
                    Log.d(TAG, "TRYING TO PAIR");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    setDevicePin(device);
                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                    if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                        Log.d(TAG, "DEVICE PAIRED");
                    } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                        Log.d(TAG, "DEVICE UNPAIRED");
                    }
                }
            }
        };
        IntentFilter ifilter = new IntentFilter(ACTION_PAIRING_REQUEST);
        IntentFilter ifilter2 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(receiver, ifilter);
        context.registerReceiver(receiver, ifilter2);
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
            //stopDeviceDiscovery();

            BluetoothDevice device = btAdapter.getRemoteDevice(cmd.getAddress());
            if(device.getBondState()== BluetoothDevice.BOND_NONE){
                Log.d(TAG, "UNBONDED Device, trying to bond");
                pairDevice(device);
            }


            if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                BTConnectionThread connectionThread = null;
                try {
                    connectionThread = getConnection(device, false);
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }

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
                    try {
                        connectionThread = getConnection(device, true);
                        try {
                            connectionThread.write(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }
        }
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Kill all existing connections.
     */
    private void disconnectAllDevices(){
        synchronized (connectionMap){
            for(Map.Entry<String, BTConnectionThread> entry : connectionMap.entrySet()){
                entry.getValue().end();
            }
            connectionMap.clear();
        }
        synchronized (connectionQueue){
            connectionQueue.clear();
        }
    }

    /**
     * Get an existing connection to a device or establish a new one.
     * @param device
     * @return BTConnectionThread for the specified device
     */
    private BTConnectionThread getConnection(BluetoothDevice device, boolean clean) throws IOException {
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
                throw new IOException("Unable to connect to remote device. Are you sure it is turned on and noone else is connected to it?");
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

                String msb = Integer.toHexString(0xFF & data[4]);
                String lsb = Integer.toHexString(0xFF & data[3]);
                String sub = Integer.toHexString(0xFF & data[5]);
                if(lsb.length()==1){
                    lsb = "0"+lsb;
                }
                String field = msb+lsb+"sub"+sub;
                String fieldID = messageFactory.getParamID(field);
                if(fieldID == null){
                    fieldID = messageFactory.getParamID(field.substring(0, 4));
                }
                provider.getDalEventBus().post(new BluetoothDeviceChanged(msg.getAddress(), fieldID, decodeByteArray(dataPayload)));
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

    private void startDeviceDiscovery(){
        if(bluetoothEnabled()){
            stopDeviceDiscovery();
            disconnectAllDevices();
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