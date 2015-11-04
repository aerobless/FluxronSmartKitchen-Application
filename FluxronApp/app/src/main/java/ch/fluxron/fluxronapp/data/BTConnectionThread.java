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
    private final static int MESSAGE_LENGTH = 12;
    private final BluetoothDevice remoteDevice;
    private final IEventBusProvider provider;
    private final BluetoothSocket socket;
    private AtomicBoolean keepRunning = new AtomicBoolean(true);

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
        int messageLength = MESSAGE_LENGTH;

        while (keepRunning.get()) {
            try {
                if(mmInStream.available() > 0){
                    nofBytes = mmInStream.read(buffer);
                    outputStream.write(buffer, 0, nofBytes);
                    Log.d("FLUXRON", nofBytes + " Bytes received!!!");

                    //For a message >=7 we can be sure that it includes the actual length of the message in the payload
                    if(outputStream.size() >= 7){
                        byte[] partialMsg = outputStream.toByteArray();
                        /* Returning messages with CCD 0x40_READ_REQUEST (64) are longer then 12Bytes.
                           Their actual length is set in the payload. */
                        if (partialMsg[2] == 64){
                            messageLength = MESSAGE_LENGTH + partialMsg[6];
                            Log.d("FLUXRON","Message length set to "+messageLength);
                        } else {
                            messageLength = MESSAGE_LENGTH;
                        }
                    }

                    if(outputStream.size() >= messageLength){
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

    public void write(byte[] message) throws IOException {
        Log.d("FLUXRON", "Sending message");
        mmOutStream.write(message);
    }

    /**
     * End this connection.
     */
    public void end(){
        keepRunning.set(false);
    }
}
