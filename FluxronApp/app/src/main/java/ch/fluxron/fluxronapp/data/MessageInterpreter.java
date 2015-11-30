package ch.fluxron.fluxronapp.data;

import android.support.annotation.NonNull;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothMessageReceived;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothRequestFailed;

/**
 * Interprets bluetooth messages received from bluetooth devices.
 */
public class MessageInterpreter {
    private IEventBusProvider provider;
    private MessageFactory messageFactory;
    private BluetoothErrorCodeConverter errorCodeConverter;

    /**
     * Instantiates a new MessageInterpreter without a IEventBusProvider. Used for Unit Tests.
     *
     * @param messageFactory
     */
    public MessageInterpreter(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        this.errorCodeConverter = new BluetoothErrorCodeConverter();
    }

    /**
     * Instantiates a new MessageInterpreter.
     *
     * @param provider
     * @param messageFactory
     */
    public MessageInterpreter(IEventBusProvider provider, MessageFactory messageFactory) {
        this.provider = provider;
        this.messageFactory = messageFactory;
        this.errorCodeConverter = new BluetoothErrorCodeConverter();
        this.provider.getDalEventBus().register(this);
    }

    /**
     * Listens to BluetoothMessageReceived events. Interprets event and relays information to
     * business layer.
     *
     * @param inputMsg
     */
    public void onEventAsync(BluetoothMessageReceived inputMsg) {
        byte[] data = inputMsg.getData();
        //Log.d("Fluxron", "Message from " + address);

        byte[] dataPayload = null;
        //messageFactory.printUnsignedByteArray(data); //DEBUG
        if (isChecksumValid(data)) {
            //Log.d("Fluxron", "and its checksum is valid.");
            if (data[2] == MessageFactory.CCD_READ_RESPONSE_1B || data[2] == MessageFactory.CCD_READ_RESPONSE_2B || data[2] == MessageFactory.CCD_READ_RESPONSE_3B || data[2] == MessageFactory.CCD_READ_RESPONSE_4B) {
                //Java Ints are 32bits, so we need 4Bytes anyway. That's why we don't care
                //how long the payload really is.
                dataPayload = new byte[]{data[6], data[7], data[8], data[9]};
                handleReadResponse(inputMsg, data, dataPayload);
            } else if (data[2] == MessageFactory.CCD_WRITE_RESPONSE) {
                //Doesn't contain data
                //Log.d("Fluxron", "Successful save");
            } else if (data[2] == MessageFactory.CCD_READ_REQUEST) {
                /**
                 * A response with a READ_REQUEST as CCD is in fact a READ_RESPONSE longer then 4Bytes.
                 * This is not according to the CANopen specification but rather a derivation from the
                 * standard by Fluxron.
                 */
                dataPayload = retriveBigData(data);
                handleReadResponse(inputMsg, data, dataPayload);
            } else if (data[2] == MessageFactory.CCD_ERROR_RESPONSE) {
                //Log.d("Fluxron", "Received ERROR bluetooth message");
                dataPayload = new byte[]{data[6], data[7], data[8], data[9]};
                handleError(inputMsg, data, dataPayload);
            } else {
                Log.d("Fluxron", "Unkown Command Code" + data[2]);
            }
        } else {
            Log.d("Fluxron", "Invalid checksum! Dropping packet");
        }
    }

    /**
     * Interprets read responses and sends them upstream.
     *
     * @param inputMsg an incoming bluetooth message
     * @param data the extracted data
     * @param dataPayload the extracted payload
     */
    private void handleReadResponse(BluetoothMessageReceived inputMsg, byte[] data, byte[] dataPayload) {
        if (dataPayload != null) {
            String field = getFieldString(data);
            RequestResponseConnection deviceChanged = new BluetoothDeviceChanged(inputMsg.getAddress(), field, decodeByteArray(dataPayload));
            deviceChanged.setConnectionId(inputMsg);
            provider.getDalEventBus().post(deviceChanged);
        }
    }

    /**
     * Interprets error messages and sends them upstream.
     *
     * @param inputMsg an incoming bluetooth message
     * @param data the extracted data
     * @param dataPayload the extracted payload
     */
    private void handleError(BluetoothMessageReceived inputMsg, byte[] data, byte[] dataPayload) {
        if (dataPayload != null) {
            String field = getFieldString(data);
            String errorCode = errorPayloadToString(dataPayload);
            RequestResponseConnection deviceChanged;
            if (errorCode.equals(BluetoothErrorCodeConverter.OBJECT_DOES_NOT_EXIST)) {
                deviceChanged = new BluetoothRequestFailed(RequestError.INDEX_DOES_NOT_EXIST, inputMsg.getAddress(), field);
            } else {
                deviceChanged = new BluetoothRequestFailed(RequestError.GENERIC_FAILURE, inputMsg.getAddress(), field);
                Log.d("Fluxron", errorCodeConverter.getErrorDescription(errorCode));
            }
            deviceChanged.setConnectionId(inputMsg);
            provider.getDalEventBus().post(deviceChanged);
        }
    }

    /**
     * Converts the error payload to a legible error message.
     *
     * @param dataPayload
     * @return
     */
    @NonNull
    private static String errorPayloadToString(byte[] dataPayload) {
        String errorCode = "0x";
        for (int i = 3; i >= 0; i--) {
            String curByte = Byte.toString(dataPayload[i]);
            if (curByte.length() == 1) {
                curByte = "0" + curByte;
            }
            errorCode += curByte;
        }
        return errorCode;
    }

    /**
     * Returns the field as formatted string.
     * Example: 1008sub3
     *
     * @param data a byte array containing the parameter id
     * @return the parameter id as legible string
     */
    private static String getFieldString(byte[] data) {
        String msb = Integer.toHexString(0xFF & data[4]);
        String lsb = Integer.toHexString(0xFF & data[3]);
        String sub = Integer.toHexString(0xFF & data[5]);
        if (lsb.length() == 1) {
            lsb = "0" + lsb;
        }
        return msb + lsb + "sub" + sub.toUpperCase();
    }

    /**
     * Checks if the checksum is valid. Only messages up to 12 bytes are supported.
     *
     * @param originalMsg
     * @return
     */
    public boolean isChecksumValid(byte[] originalMsg) {
        if (originalMsg.length >= 12) {
            byte[] checkMsg = messageFactory.setChecksum(originalMsg);
            if (Arrays.equals(originalMsg, checkMsg)) {
                return true;
            }
        } else {
            Log.d("FLUXRON", "Only messages with length 12 can be verified. This message has length " + originalMsg.length);
        }
        return false;
    }

    /**
     * Used to retrive data from Bluetooth messages that are longer then 12Bytes (>4Byte Data).
     * These messages do not follow the CANopen specification. Instead Field 6 tells the
     * additional length after then normal CANopen message (12B).
     *
     * @param input a byte array containing a large message
     * @return byte[] Array containing only the data part of the input-Array.
     */
    private byte[] retriveBigData(byte[] input) {
        byte[] subArray = Arrays.copyOfRange(input, 9, input.length - 3);
        if (subArray.length != input[6]) {
            Log.d("Fluxron", "Length of extracted data doesn't match specified length!");
        }
        return subArray;
    }

    /**
     * Decodes little endian byte[] arrays to int values.
     *
     * @param input a byte array in Big-Endian
     * @return decoded Int value of the input
     */
    private static int decodeByteArray(byte[] input) {
        ByteBuffer buffer = ByteBuffer.wrap(input);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
    }
}
