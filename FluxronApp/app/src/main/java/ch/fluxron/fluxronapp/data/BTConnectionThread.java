package ch.fluxron.fluxronapp.data;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread for active bluetooth connection.
 */
public class BTConnectionThread extends Thread{
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final int msgLength = 17;
    private BluetoothSocket socket;
    public AtomicBoolean keepRunning = new AtomicBoolean(true);

    public BTConnectionThread(BluetoothSocket btsocket) {
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
                    Log.d("FLUXRON", nofBytes + " Bytes received!!!");
                    if(outputStream.size() == msgLength){
                        //TODO: interpret & send response to event bus.
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
        Log.d("FLUXRON", "Sending message");
        try {
            mmOutStream.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printUnsignedByteArray(byte[] message) {
        String hexMessage = "";
        for (int i = 0; i < message.length; i++) {
            hexMessage = hexMessage+Integer.toHexString(0xFF & message[i])+" ";
        }
        Log.d("FLUXRON", hexMessage);
    }
}
