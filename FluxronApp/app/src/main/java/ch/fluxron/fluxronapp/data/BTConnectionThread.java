package ch.fluxron.fluxronapp.data;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothMessageReceived;

/**
 * Thread for active bluetooth connection.
 */
public class BTConnectionThread extends Thread{
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final int msgLength = 12;
    private final BluetoothDevice remoteDevice;
    private final IEventBusProvider provider;
    private final BluetoothSocket socket;
    public AtomicBoolean keepRunning = new AtomicBoolean(true);

    public BTConnectionThread(BluetoothSocket btsocket, IEventBusProvider provider) {
        remoteDevice = btsocket.getRemoteDevice();
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        socket = btsocket;
        this.provider = provider;

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
                    Log.d("FLUXRON", nofBytes + " Bytes received!!!");

                    //TODO: need a better way to decide when a message is ended
                    if(outputStream.size() >= msgLength){
                        byte[] responseMsg = outputStream.toByteArray();
                        provider.getDalEventBus().post(new BluetoothMessageReceived(remoteDevice.getAddress(), responseMsg));
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
        Log.d("FLUXRON", "Sending message");
        try {
            mmOutStream.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
