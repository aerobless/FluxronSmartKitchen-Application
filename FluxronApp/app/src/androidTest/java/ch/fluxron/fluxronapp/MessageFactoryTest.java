package ch.fluxron.fluxronapp;

import junit.framework.*;

import java.util.Arrays;
import ch.fluxron.fluxronapp.data.MessageFactory;
import ch.fluxron.fluxronapp.data.generated.ParamManager;

/**
 * Unit tests for MessageFactory class.
 */
public class MessageFactoryTest extends TestCase {

    public void testLowChecksum(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] expectation = new byte[]{
                (byte) 0xAA, (byte) 0xAA, //Start sequence
                (byte) 0x40, (byte) 0x18, (byte) 0x10, (byte) 0x04,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x6c, (byte) 0x00}; //Checksum
        messageFactory.printUnsignedByteArray(expectation);
        byte[] result = messageFactory.buildMessage(
                MessageFactory.CCD_READ_REQUEST,
                new byte[]{(byte) 0x18, (byte) 0x10, (byte) 0x04,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
        messageFactory.printUnsignedByteArray(result);
        assertTrue(Arrays.equals(expectation, result));
    }

    public void testHighChecksum(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] expectation = new byte[]{
                (byte) 0xAA, (byte) 0xAA, //Start sequence
                (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x44, (byte) 0x44, (byte) 0x00, (byte) 0x44,
                (byte) 0xC, (byte) 0x1}; //Checksum
        messageFactory.printUnsignedByteArray(expectation);
        byte[] result = messageFactory.buildMessage(
                MessageFactory.CCD_READ_REQUEST,
                new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x44, (byte) 0x44, (byte) 0x00, (byte) 0x44});
        messageFactory.printUnsignedByteArray(result);
        assertTrue(Arrays.equals(expectation, result));
    }

    public void testGenerateFullMessage(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] expectation = new byte[]{
                (byte) 0xAA, (byte) 0xAA,
                (byte) 0x40, (byte) 0x01, (byte) 0x30, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x72, (byte) 0x00 };
        messageFactory.printUnsignedByteArray(expectation);
        byte[] result = messageFactory.makeReadRequest(ParamManager.F_SCLASS_3001SUB1_KNOB_A_DIGITAL);
        messageFactory.printUnsignedByteArray(result);
        assertTrue(Arrays.equals(expectation, result));
    }

    public void testObjectConvertInt(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] result = messageFactory.convertDataObjectToByte(ParamManager.F_SCLASS_3035SUB17_FLX_TEMP_FAN_LEVEL_MAX, 5);
        messageFactory.printUnsignedByteArray(result);
        assertTrue(Arrays.equals(new byte[]{0x00, 0x00, 0x00, 0x05}, result));

        byte[] result2 = messageFactory.convertDataObjectToByte(ParamManager.F_SCLASS_1A00SUB3_PDO_1_MAPPING_FOR_A_PROCESS_DATA_VARIABLE_3, 12345);
        messageFactory.printUnsignedByteArray(result2);
        assertTrue(Arrays.equals(new byte[]{0x00, 0x00, 0x30, 0x39}, result2));
    }

    public void testObjectConvertString(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] result = messageFactory.convertDataObjectToByte(ParamManager.F_SCLASS_1008_MANUFACTURER_DEVICE_NAME, "TROLL");
        messageFactory.printUnsignedByteArray(result);
        assertTrue(Arrays.equals(new byte[]{0x54, 0x52, 0x04f, 0x4c, 0x4c}, result));
    }

    public void testGenerateWriteMessage(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] expectation = new byte[]{
                (byte) 0xAA, (byte) 0xAA,
                (byte) 0x23, (byte) 0x00, (byte) 0x14, (byte) 0x03,
                (byte) 0x37, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x71, (byte) 0x00 };
        messageFactory.printUnsignedByteArray(expectation);
        byte[] result = messageFactory.makeWriteRequest(ParamManager.F_SCLASS_1400SUB3_INHIBIT_TIME, 55);
        messageFactory.printUnsignedByteArray(result);
        assertTrue(Arrays.equals(expectation, result));
    }
}
