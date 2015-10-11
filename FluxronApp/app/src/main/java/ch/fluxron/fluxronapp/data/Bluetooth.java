package ch.fluxron.fluxronapp.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import ch.fluxron.fluxronapp.events.modelDal.BluetoothDiscoveryRequest;

/**
 * Listens to eventbus messages. Provides access to bluetooth devices.
 */
public class Bluetooth {
    private IEventBusProvider provider;
    private BluetoothSPP btSPP;

    private static final String TAG = "FLUXRON";

    //Bluetooth Device MAC
    private static final String FLX_GTZ_196_ADDRESS = "00:13:04:12:06:20";
    private static final String FLX_BAX_5206_ADDRESS = "30:14:10:31:11:85";
    private static final String HMSoft_ADDRESS = "00:0E:0E:00:A8:A2";

    public Bluetooth(IEventBusProvider provider, BluetoothSPP bt) {
        this.provider = provider;
        this.provider.getDalEventBus().register(this);
        this.btSPP = bt;
        setupBluetooth();
    }

    public void onEventAsync(BluetoothDiscoveryRequest msg) {
        listPairedDevices();
        btSPP.connect(FLX_BAX_5206_ADDRESS);
    }

    private void setupBluetooth(){
        btSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Log.d(TAG, "connected");
            }

            public void onDeviceDisconnected() {
                Log.d(TAG, "disconnected");
            }

            public void onDeviceConnectionFailed() {
                Log.d(TAG, "connection failed");
            }
        });

        btSPP.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if (state == BluetoothState.STATE_CONNECTED) {
                    Log.d(TAG, "successfully connected");
                    Log.d(TAG, "sending message");
                    byte[] message = generateMessage(new byte[]{
                            (byte) 0x40, (byte) 0x18, (byte) 0x10, (byte) 0x04,
                            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
                    btSPP.send(message, true);
                    btSPP.send("Hello world, plz answer..", true);
                    Log.d(TAG, "message sent.. awaiting response");
                }
                else if (state == BluetoothState.STATE_CONNECTING)
                    Log.d(TAG, "trying to connect");
                else if (state == BluetoothState.STATE_LISTEN)
                    Log.d(TAG, "waiting for connection");
                else if (state == BluetoothState.STATE_NONE)
                    Log.d(TAG, "no connection");
            }
        });

        btSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Log.d(TAG, "data incomming: " + message + " " + data);
            }
        });

        btSPP.setupService();
        btSPP.startService(BluetoothState.DEVICE_OTHER);
    }

    private List<String> listPairedDevices(){
        Set<BluetoothDevice> pairedDevices = btSPP.getBluetoothAdapter().getBondedDevices();
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

    //TODO: add unit test
    /*
     * Message Format:
     * Byte 0: 0xAA Startsequence
     * Byte 1: 0xAA Startsequence
     * Byte 2..9: CAN 8 Byte CAN Message
     * Byte 11: Check LB Low Byte Checksum
     * Byte 12: Check HB High Byte Checksum
     *
     * Note: According to document, Byte 10 & 11 provide CR.. however
     * the example message has 12 bytes.
     */
    private byte[] generateMessage(byte[] canMessage){
        byte[] message = new byte[12];
        message[0] = (byte)0xAA;
        message[1] = (byte)0xAA;
        int checksumLow = 0;
        int checksumHigh = 0;
        for (int c = 2; c < canMessage.length; c++) {
            message[c] = canMessage[c-2];
            if(c<6){
                checksumLow += canMessage[c-2];
            } else {
                checksumHigh += canMessage[c-2];
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
}