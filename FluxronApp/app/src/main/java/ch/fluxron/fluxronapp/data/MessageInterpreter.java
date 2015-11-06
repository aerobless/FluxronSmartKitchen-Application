package ch.fluxron.fluxronapp.data;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothMessageReceived;

/**
 * Interprets bluetooth messages received from the remote device.
 */
public class MessageInterpreter {
    private IEventBusProvider provider;
    private MessageFactory messageFactory;

    public MessageInterpreter(IEventBusProvider provider, MessageFactory messageFactory) {
        this.provider = provider;
        this.messageFactory = messageFactory;
        this.provider.getDalEventBus().register(this);
    }

    public void onEventAsync(BluetoothMessageReceived inputMsg) {
        String address = inputMsg.getAddress();
        byte[] data = inputMsg.getData();
        Log.d("Fluxron", "Message from " + address);

        byte[] dataPayload = null;
        messageFactory.printUnsignedByteArray(data);
        if(isChecksumValid(data)){
            Log.d("Fluxron", "and its checksum is valid.");
            if(data[2] == MessageFactory.CCD_READ_RESPONSE_1B || data[2] == MessageFactory.CCD_READ_RESPONSE_2B || data[2] == MessageFactory.CCD_READ_RESPONSE_3B || data[2] == MessageFactory.CCD_READ_RESPONSE_4B){
                //Java Ints are 32bits, so we need 4Bytes anyway. That's why we don't care
                //how long the payload really is.
                dataPayload = new byte[]{data[6],data[7],data[8],data[9]};
            } else if (data[2] == MessageFactory.CCD_WRITE_RESPONSE){
                //Doesn't contain data
            } else if (data[2] == MessageFactory.CCD_READ_REQUEST){
                dataPayload = retriveBigData(data);
            } else if (data[2] == MessageFactory.CCD_ERROR_RESPONSE){
                Log.d("Fluxron", "Received ERROR bluetooth message");
            } else {
                Log.d("Fluxron", "Unkown Command Code"+ data[2]);
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
                String fieldID = getParamID(field);
                if(fieldID == null){
                    fieldID = getParamID(field.substring(0, 4));
                }
                RequestResponseConnection deviceChanged = new BluetoothDeviceChanged(inputMsg.getAddress(), fieldID, decodeByteArray(dataPayload));
                deviceChanged.setConnectionId(inputMsg);
                provider.getDalEventBus().post(deviceChanged);
            }
        } else {
            Log.d("Fluxron", "Invalid checksum!");
        }
    }

    public boolean isChecksumValid(byte[] originalMsg){
        if(originalMsg.length >= 12){
            byte[] checkMsg = messageFactory.setChecksum(originalMsg);
            if(Arrays.equals(originalMsg, checkMsg)){
                return true;
            }
        }else {
            Log.d("FLUXRON", "Only messages with length 12 can be verified. This message has length "+originalMsg.length);
        }
        return false;
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
            Log.d("Fluxron", "Length of extracted data doesn't match specified length!");
        }
        return subArray;
    }

    //TODO: proper distinction between SClass etc. based on communication ID.. will probably be done in Device Manager in the future.
    //This is a simple fix to not break device discovery after adding multiple EDSs.
    public String getParamID(String input){
        if(messageFactory.getParameter("SClass_"+input)!= null) {
            return messageFactory.getParameter("SClass_"+input).getId();
        } else {
            return null;
        }
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


}
