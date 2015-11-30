package ch.fluxron.fluxronapp.data;

import android.util.Log;

import com.google.common.primitives.Ints;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;

import ch.fluxron.fluxronapp.data.generated.DeviceParameter;
import ch.fluxron.fluxronapp.data.generated.ParamManager;

/**
 * Encodes CANopen messages.
 */
public class MessageFactory {
    private Map<String, DeviceParameter> parameterMap;

    /**
     * Command Code:
     * The first byte after the initial sequence is the command code (CCD).
     * 1-4 specify the amount of bytes that should be read.
     * More information in CANopen.pdf pages 16-18.
     */
    public final static byte CCD_READ_REQUEST = (byte) 0x40;
    public final static byte CCD_WRITE_REQUEST_1B = (byte) 0x2F;
    public final static byte CCD_WRITE_REQUEST_2B = (byte) 0x2B;
    public final static byte CCD_WRITE_REQUEST_3B = (byte) 0x27;
    public final static byte CCD_WRITE_REQUEST_4B = (byte) 0x23;

    public final static byte CCD_ERROR_RESPONSE = (byte) 0x80;

    public final static byte CCD_READ_RESPONSE_1B = (byte) 0x4F;
    public final static byte CCD_READ_RESPONSE_2B = (byte) 0x4B;
    public final static byte CCD_READ_RESPONSE_3B = (byte) 0x47;
    public final static byte CCD_READ_RESPONSE_4B = (byte) 0x43;
    public final static byte CCD_WRITE_RESPONSE = (byte) 0x60;

    //Access Types
    public final static String ACCESS_CONST = "const"; //read only, will not change
    public final static String ACCESS_READ_ONLY = "ro"; //read-only, can change
    public final static String ACCESS_WRITE_ONLY = "wo"; //write-only
    public final static String ACCESS_READ_WRITE = "rw"; //read/writeable

    //Data Types
    public final static int DATA_TYPE_BOOLEAN = 1;
    public final static int DATA_TYPE_8BIT_SIGNED_INT = 2;
    public final static int DATA_TYPE_16BIT_SIGNED_INT = 3;
    public final static int DATA_TYPE_32BIT_SIGNED_INT = 4;
    public final static int DATA_TYPE_8BIT_UNSIGNED_INT = 5;
    public final static int DATA_TYPE_16BIT_UNSIGNED_INT = 6;
    public final static int DATA_TYPE_32BIT_UNSIGNED_INT = 7;
    public final static int DATA_TYPE_FLOAT = 8;
    public final static int DATA_TYPE_VISIBLE_STRING = 9;
    public final static int DATA_TYPE_OCTET_STRING = 10;
    public final static int DATA_TYPE_DATE = 11;
    public final static int DATA_TYPE_TIME_OF_DAY = 12;
    public final static int DATA_TYPE_TIME_DIFFERENCE = 13;
    public final static int DATA_TYPE_BIT_STRING = 14;
    public final static int DATA_TYPE_DOMAIN = 15;
    public final static int DATA_TYPE_PDO_COMM_PAR = 20;
    public final static int DATA_TYPE_PDO_MAPPING = 21;
    public final static int DATA_TYPE_SDO_PARAMETER = 22;

    /**
     * Instantiates a new MessageFactory.
     */
    public MessageFactory() {
        ParamManager paramManager = new ParamManager();
        this.parameterMap = paramManager.getParamMap();
    }

    /**
     * Create a new read request for a specific parameter.
     *
     * @param paramID
     * @return
     */
    public byte[] makeReadRequest(String paramID) {
        byte[] index = parameterMap.get(paramID).getIndex();
        byte[] messageBody = new byte[7];
        messageBody[0] = index[1]; //LSB
        messageBody[1] = index[0]; //MSB
        messageBody[2] = parameterMap.get(paramID).getSubindex();
        return buildMessage(CCD_READ_REQUEST, messageBody);
    }

    /**
     * Create a new write request for a specific parameter and value.
     *
     * @param paramID
     * @param data
     * @return
     */
    public byte[] makeWriteRequest(String paramID, Object data) {
        String accessType = parameterMap.get(paramID).getAccessType();

        if (accessType.equals(ACCESS_READ_WRITE) || accessType.equals(ACCESS_WRITE_ONLY)) {

            //Set Index & subindex
            byte[] index = parameterMap.get(paramID).getIndex();
            byte[] messageBody = new byte[7];
            messageBody[0] = index[1]; //LSB
            messageBody[1] = index[0]; //MSB
            messageBody[2] = parameterMap.get(paramID).getSubindex();

            //Create & copy payload to Little Endian
            byte[] dataArray = convertDataObjectToByte(paramID, data);

            ArrayUtils.reverse(dataArray);
            int bodyIt = 3;
            for (int i = 0; i < dataArray.length; i++) {
                messageBody[bodyIt] = dataArray[i];
                bodyIt++;
            }

            //Set message type
            if (dataArray.length <= 4) {
                byte messageType = CCD_WRITE_REQUEST_4B;
                switch (dataArray.length) {
                    case 3:
                        messageType = CCD_WRITE_REQUEST_3B;
                    case 2:
                        messageType = CCD_WRITE_REQUEST_2B;
                    case 1:
                        messageType = CCD_WRITE_REQUEST_1B;
                }
                return buildMessage(messageType, messageBody);
            } else {
                throw new UnsupportedOperationException("Support to write messages longer than 4Bytes hasn't been implemented yet");
            }
        } else {
            throw new IllegalArgumentException("Unable to create a write message for a read-only parameter.");
        }
    }

    /**
     * Converts  Parameter Data Objects to a byte arrays.
     *
     * @param paramID the ID of a parameter (e.g. SClass_3101subB)
     * @param data the data that is to be written to that parameter
     * @return a byte array encapsulating parameter and data
     * @throws UnsupportedOperationException
     */
    public byte[] convertDataObjectToByte(String paramID, Object data) {
        int dataType = parameterMap.get(paramID).getDataType();
        byte[] dataByte;

        if (dataType == DATA_TYPE_BOOLEAN) {
            int val = ((Boolean) data) ? 1 : 0;
            dataByte = new byte[val];
        } else if (isSignedInt(dataType) || isUnsignedInt(dataType)) {
            dataByte = Ints.toByteArray((Integer) data);
        } else if (dataType == DATA_TYPE_VISIBLE_STRING) {
            String val = (String) data;
            dataByte = val.getBytes();
        } else {
            throw new UnsupportedOperationException("The datatype for this class hasn't been implemented yet.");
        }
        return dataByte;
    }

    /**
     * Check if this dataType is signed. Returns true if signed.
     *
     * @param dataType
     * @return
     */
    private static boolean isSignedInt(int dataType) {
        return dataType == DATA_TYPE_8BIT_SIGNED_INT
                || dataType == DATA_TYPE_16BIT_SIGNED_INT
                || dataType == DATA_TYPE_32BIT_SIGNED_INT;
    }

    /**
     * Check if this dataType is unsigned. Returns true if unsigned.
     *
     * @param dataType
     * @return
     */
    private static boolean isUnsignedInt(int dataType) {
        return dataType == DATA_TYPE_8BIT_UNSIGNED_INT
                || dataType == DATA_TYPE_16BIT_UNSIGNED_INT
                || dataType == DATA_TYPE_32BIT_UNSIGNED_INT;
    }


    /**
     * Add the requestCode to a canMessage.
     * <p/>
     * Message Format:
     * Byte 0: 0xAA Startsequence
     * Byte 1: 0xAA Startsequence
     * Byte 2: Command Code
     * Byte 3..9: CAN 8 Byte CAN Message
     * Byte 10: Check LB Low Byte Checksum
     * Byte 11: Check HB High Byte Checksum
     *
     * @param requestCode
     * @param canMessage
     * @return
     */
    public byte[] buildMessage(byte requestCode, byte[] canMessage) {
        byte[] message = new byte[12];
        message[0] = (byte) 0xAA;
        message[1] = (byte) 0xAA;
        message[2] = requestCode;
        for (int c = 0; c < (canMessage.length); c++) {
            message[c + 3] = canMessage[c];
        }
        message = setChecksum(message);
        return message;
    }

    /**
     * Calculate the checksum for byte 2-9 and set it in byte 10 & 11.
     */
    protected byte[] setChecksum(byte[] message) {
        if (message.length >= 5) {
            byte[] checkedMessage = message.clone();
            int checksum = 0;
            int msgStart = 2;
            int msgEnd = message.length - 2;
            for (int c = msgStart; c < msgEnd; c++) {
                checksum += checkedMessage[c];
            }
            checkedMessage[msgEnd] = (byte) (checksum & 0xFF);
            if (checksum >= 255) {
                checkedMessage[msgEnd + 1] = (byte) ((checksum >> 8) & 0xFF);
            }
            return checkedMessage;
        } else {
            Log.d("FLUXRON", "Unable to set checksum for message shorter than 5B");
            return message;
        }
    }

    /**
     * Print a unsigned byte array. Can be used for debugging.
     *
     * @param message
     */
    public void printUnsignedByteArray(byte[] message) {
        String hexMessage = "";
        for (int i = 0; i < message.length; i++) {
            hexMessage = hexMessage + Integer.toHexString(0xFF & message[i]) + " ";
        }
        Log.d("FLUXRON", hexMessage);
    }

    /**
     * Returns a ParameterValue if the key exists. Otherwise null.
     *
     * @param key a parameter id (e.g SClass_3101subB)
     * @return DeviceParameter containing the param and it's value
     */
    public DeviceParameter getParameter(String key) {
        return parameterMap.get(key);
    }
}
