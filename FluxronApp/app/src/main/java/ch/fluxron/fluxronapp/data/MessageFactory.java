package ch.fluxron.fluxronapp.data;

import android.util.Log;

/**
 * Encodes & decodes CANopen messages.
 */
public class MessageFactory {

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

    public final static byte CCD_READ_RESPONSE_1B =  (byte) 0x4F;
    public final static byte CCD_READ_RESPONSE_2B =  (byte) 0x4B;
    public final static byte CCD_READ_RESPONSE_3B =  (byte) 0x47;
    public final static byte CCD_READ_RESPONSE_4B =  (byte) 0x43;
    public final static byte CCD_WRITE_RESPONSE =  (byte) 0x60;

    //Known Fields
    public final static String F_DEVICE_TYPE = "1000";
    public final static String F_ERROR_REGISTER = "1001";
    public final static String F_IDENTITY = "1018";
    public final static String F_NOF_ENTRIES_IDENTITY = "1018sub0";
    public final static String F_VENDOR_ID = "1018sub1";
    public final static String F_PRODUCT_CODE = "1018sub2";
    public final static String F_REVISION_NUMBER = "1018sub3";
    public final static String F_SERIAL_NUMBER = "1018sub4";
    public final static String F_UNSIGNED8_0to15 = "A0";
    public final static String F_NOF_ENTRIES_UNSIGNED8 = "A0sub0";
    public final static String F_TYPE = "A0sub1";
    public final static String F_MINIMUM_VALUE = "A0sub2";
    public final static String F_MAXIMUM_VALUE = "A0sub3";
    public final static String F_MANUFACTURER_DEVICE_NAME = "1008";
    public final static String F_MANUFACTURER_HARDWARE_VERSION = "1009";
    public final static String F_MANUFACTURER_SOFTWARE_VERSION = "100A";
    public final static String F_KNOB_ANGLE_DIGITAL = "3001";
    public final static String F_NOF_ENTRIES_KNOB_ANGLE_DIGITAL = "3001sub0";
    public final static String F_KNOB_A_DIGITAL = "3001sub1";
    public final static String F_KNOB_B_DIGITAL = "3001sub2";
    public final static String F_KNOB_STATUS = "3001sub3";
    public final static String F_TEMPERATURE_SENSOR = "3028";
    public final static String F_NOF_ENTRIES_TEMPERATURE_SENSOR = "3028sub0";
    public final static String F_KMX_TEMPERATURE_0 = "3028sub1";
    public final static String F_KMX_TEMPERATURE_1 = "3028sub2";
    public final static String F_KMX_TEMPERATURE_2 = "3028sub3";
    public final static String F_KMX_TEMPERATURE_3 = "3028sub4";

    public byte[] makeReadRequest(byte[] index, byte subindex){
        byte[] messageBody = new byte[7];
        messageBody[0] = index[1]; //LSB
        messageBody[1] = index[0]; //MSB
        messageBody[2] = subindex;
        printUnsignedByteArray(messageBody);
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
        int checksumLow = 0;
        int checksumHigh = 0;
        for (int c = 2; c < 10; c++) {
            if(c<6){
                checksumLow += message[c];
            } else {
                checksumHigh += message[c];
            }
        }
        message[10] = (byte)checksumLow;
        message[11] = (byte)checksumHigh;
        return message;
    }

    public void printUnsignedByteArray(byte[] message) {
        String hexMessage = "";
        for (int i = 0; i < message.length; i++) {
            hexMessage = hexMessage+Integer.toHexString(0xFF & message[i])+" ";
        }
        Log.d("FLUXRON", hexMessage);
    }
}
