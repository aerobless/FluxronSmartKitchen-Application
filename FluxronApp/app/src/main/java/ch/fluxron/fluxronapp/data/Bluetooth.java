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
import android.util.LruCache;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.modelDal.ToastProduced;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothBondingCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothConnectionFailed;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothWriteRequest;
import ch.fluxron.fluxronapp.model.ConnectionCache;
import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Listens to eventbus messages. Provides access to bluetooth devices.
 */
public class Bluetooth {
    private IEventBusProvider provider;
    private MessageFactory messageFactory;
    private MessageInterpreter messageInterpreter;
    private BluetoothAdapter btAdapter = null;
    private final LruCache<String, BTConnectionThread> connectionCache;

    private static final String TAG = "FLUXRON";
    private static final String DEVICE_PIN = "1234";
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //well-known
    private static final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST"; //to support api18

    public Bluetooth(IEventBusProvider provider, Context context) {
        this.provider = provider;
        this.provider.getDalEventBus().register(this);

        messageFactory = new MessageFactory();
        messageInterpreter = new MessageInterpreter(provider, messageFactory);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        setupDiscovery(context);
        setupBonding(context);
        connectionCache = new ConnectionCache(3);
    }

    /**
     * Set the Bluetooth PIN of a device. This method should only be run after a attempt at bonding
     * has been made.
     *
     * @param device
     */
    public void setDevicePin(BluetoothDevice device) {
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
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets up a BroadcastReceiver to listen to BluetoothDevice.ACTION_FOUND.
     * When a device is found a message is sent to the DAL-BL event bus.
     *
     * @param context
     */
    private void setupDiscovery(Context context) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    provider.getDalEventBus().post(new BluetoothDeviceFound(new Device(device.getName(), device.getAddress(), device.getBondState() == BluetoothDevice.BOND_BONDED)));
                }
            }
        };
        IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, ifilter);
    }

    /**
     * Sets up a BroadcastReceiver to listen to bonding events.
     *
     * @param context
     */
    private void setupBonding(Context context) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(TAG, action);
                if (ACTION_PAIRING_REQUEST.equals(action)) {
                    Log.d(TAG, "TRYING TO PAIR");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    setDevicePin(device);
                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                    if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                        Log.d(TAG, "DEVICE PAIRED");
                        provider.getDalEventBus().post(new BluetoothDeviceChanged(device.getAddress(), "Bondage", 1));
                    } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
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
     *
     * @param cmd
     */
    public void onEventAsync(BluetoothDiscoveryCommand cmd) {
        if (cmd.isEnabled()) {
            startDeviceDiscovery();
        } else {
            stopDeviceDiscovery();
        }
    }

    /**
     * Connects to a bluetooth device and writes data to the field specified in the command.
     *
     * @param cmd
     */
    public void onEventAsync(BluetoothWriteRequest cmd) {
        byte[] message = messageFactory.makeWriteRequest(cmd.getField(), cmd.getValue());
        //messageFactory.printUnsignedByteArray(message); //DEBUG
        try {
            sendData(cmd.getAddress(), message, cmd);
        } catch (IOException e) {
            BluetoothConnectionFailed connectionFailure = new BluetoothConnectionFailed(BluetoothConnectionFailed.FailureType.GENERIC_CONECTION_FAILURE, cmd.getAddress());
            connectionFailure.setConnectionId(cmd);
            provider.getDalEventBus().post(connectionFailure);
        }
    }

    /**
     * Connects to a bluetooth device and requests the parameters specified in the command.
     *
     * @param cmd
     */
    public void onEventAsync(BluetoothReadRequest cmd) {
        Set<String> parameters = new HashSet<>(cmd.getParameters());
        for (String p : parameters) {
            byte[] message = messageFactory.makeReadRequest(p);
            //messageFactory.printUnsignedByteArray(message); //DEBUG
            try {
                sendData(cmd.getAddress(), message, cmd);
            } catch (IOException e) {
                BluetoothConnectionFailed connectionFailure = new BluetoothConnectionFailed(BluetoothConnectionFailed.FailureType.GENERIC_CONECTION_FAILURE, cmd.getAddress());
                connectionFailure.setConnectionId(cmd);
                provider.getDalEventBus().post(connectionFailure);
            }
        }
    }

    /**
     * Used to start the bonding process with a specific bluetooth device.
     *
     * @param cmd
     */
    public void onEventAsync(BluetoothBondingCommand cmd) {
        if (bluetoothEnabled()) {
            BluetoothDevice device = btAdapter.getRemoteDevice(cmd.getAddress());
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                Log.d(TAG, "Trying to bond!");
                try {
                    Method method = device.getClass().getMethod("createBond", (Class[]) null);
                    method.invoke(device, (Object[]) null);
                } catch (Exception e) {
                    Log.d(TAG, "Attempt invoke bonding failed: " + e.getMessage());
                }
            } else {
                Log.d(TAG, "Device is already bonded.");
            }
        }
    }

    private void sendData(String address, byte[] message, RequestResponseConnection connection) throws IOException {
        if (bluetoothEnabled()) {
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                BTConnectionThread connectionThread = getConnection(device, false);
                //Log.d(TAG, "GOT connection, trying to write message");
                connectionThread.write(message, connection);
            } else {
                throw new IOException("Unbonded Device");
            }
        }
    }

    /**
     * Kill all existing connections.
     */
    private void disconnectAllDevices() {
        synchronized (connectionCache) {
            connectionCache.evictAll();
        }
    }

    /**
     * Get an existing connection to a device or establish a new one.
     *
     * @param device
     * @return BTConnectionThread for the specified device
     */
    private BTConnectionThread getConnection(BluetoothDevice device, boolean clean) throws IOException {
        if (clean) {
            synchronized (connectionCache) {
                connectionCache.remove(device.getAddress());
            }
        }
        BTConnectionThread connectionThread;
        synchronized (connectionCache) {
            connectionThread = connectionCache.get(device.getAddress());
        }
        if (connectionThread == null) {
            BluetoothSocket btSocket = createBluetoothSocket(device);
            if (connectSocket(btSocket)) {
                connectionThread = new BTConnectionThread(btSocket, provider);
                connectionThread.start();
                synchronized (connectionCache) {
                    connectionCache.put(device.getAddress(), connectionThread);
                }
            } else {
                throw new IOException("Unable to connect to remote device. Are you sure it is turned on and noone else is connected to it?");
            }
        }
        return connectionThread;
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
            }
        }
        return false;
    }

    private void startDeviceDiscovery() {
        if (bluetoothEnabled()) {
            stopDeviceDiscovery();
            disconnectAllDevices();
            btAdapter.startDiscovery();
        }
    }

    private void stopDeviceDiscovery() {
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
                return (BluetoothSocket) m.invoke(device, SPP_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
                e.printStackTrace();
            }
        }
        return device.createRfcommSocketToServiceRecord(SPP_UUID);
    }

    private boolean bluetoothEnabled() {
        if (btAdapter == null) {
            Log.d(TAG, "Bluetooth not supported (Are you running on emulator?)");
        } else {
            if (btAdapter.isEnabled()) {
                return true;
            } else {
                provider.getDalEventBus().post(new ToastProduced("Please turn on Bluetooth!"));
                Log.d(TAG, "Bluetooth not turned on");
            }
        }
        return false;
    }
}