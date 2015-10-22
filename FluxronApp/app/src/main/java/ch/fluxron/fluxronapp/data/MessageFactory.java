package ch.fluxron.fluxronapp.data;

import android.util.Log;

import java.util.Arrays;
import java.util.Map;

import ch.fluxron.fluxronapp.data.generated.GParamManager;

/**
 * Encodes & decodes CANopen messages.
 */
public class MessageFactory {
    Map<String, DeviceParameter> parameterMap;

    /**
     * Command Code:
     * The first byte after the initial sequence is the command code (CCD).
     * 1-4 specify the amount of bytes that should be read.
     * More information in CANopen.pdf pages 16-18.
     */
    public final static byte CCD_READ_REQUEST =  (byte) 0x40;
    public final static byte CCD_WRITE_REQUEST_1B = (byte) 0x2F;
    public final static byte CCD_WRITE_REQUEST_2B = (byte) 0x2B;
    public final static byte CCD_WRITE_REQUEST_3B = (byte) 0x27;
    public final static byte CCD_WRITE_REQUEST_4B = (byte) 0x23;

    public final static byte CCD_ERROR_RESPONSE =  (byte) 0x80;

    public final static byte CCD_READ_RESPONSE_1B =  (byte) 0x4F;
    public final static byte CCD_READ_RESPONSE_2B =  (byte) 0x4B;
    public final static byte CCD_READ_RESPONSE_3B =  (byte) 0x47;
    public final static byte CCD_READ_RESPONSE_4B =  (byte) 0x43;
    public final static byte CCD_WRITE_RESPONSE =  (byte) 0x60;

    public MessageFactory() {
        GParamManager paramManager = new GParamManager();
        this.parameterMap = paramManager.getParamMap();
    }

    public byte[] makeReadRequest(String paramID){
        byte[] index = parameterMap.get(paramID).getIndex();
        byte[] messageBody = new byte[7];
        messageBody[0] = index[1]; //LSB
        messageBody[1] = index[0]; //MSB
        messageBody[2] = parameterMap.get(paramID).getSubindex();
        return buildMessage(CCD_READ_REQUEST, messageBody);
    }

    /*
     * Message Format:
     * Byte 0: 0xAA Startsequence
     * Byte 1: 0xAA Startsequence
     * Byte 2: Command Code
     * Byte 3..9: CAN 8 Byte CAN Message
     * Byte 10: Check LB Low Byte Checksum
     * Byte 11: Check HB High Byte Checksum
     */
    public byte[] buildMessage(byte requestCode, byte[] canMessage){
        byte[] message = new byte[12];
        message[0] = (byte)0xAA;
        message[1] = (byte)0xAA;
        message[2] = requestCode;
        for (int c = 0; c < (canMessage.length); c++) {
            message[c+3] = canMessage[c];
        }
        message = setChecksum(message);
        return message;
    }

    /**
     * Calculate the checksum for byte 2-9 and set it in byte 10 & 11.
     */
    private byte[] setChecksum(byte[] message) {
        if(message.length >= 5){
            byte[] checkedMessage = message.clone();
            int checksum = 0;
            int msgStart = 2;
            int msgEnd = message.length-2;
            for (int c = msgStart; c < msgEnd; c++) {
                checksum += checkedMessage[c];
            }
            checkedMessage[msgEnd] = (byte) (checksum & 0xFF);
            if(checksum>=255){
                checkedMessage[msgEnd+1] = (byte) ((checksum >> 8) & 0xFF);
            }
            return checkedMessage;
        } else {
            Log.d("FLUXRON", "Unable to set checksum for message shorter than 5B");
            return message;
        }
    }

    public boolean isChecksumValid(byte[] originalMsg){
        if(originalMsg.length >= 12){
            byte[] checkMsg = setChecksum(originalMsg);
            if(Arrays.equals(originalMsg, checkMsg)){
                return true;
            }
        }else {
            Log.d("FLUXRON", "Only messages with length 12 can be verified. This message has length "+originalMsg.length);
        }
        return false;
    }

    public void printUnsignedByteArray(byte[] message) {
        String hexMessage = "";
        for (int i = 0; i < message.length; i++) {
            hexMessage = hexMessage+Integer.toHexString(0xFF & message[i])+" ";
        }
        Log.d("FLUXRON", hexMessage);
    }
}
