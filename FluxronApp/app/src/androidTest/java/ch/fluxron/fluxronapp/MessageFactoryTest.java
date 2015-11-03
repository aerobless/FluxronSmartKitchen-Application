package ch.fluxron.fluxronapp;
import android.util.Log;

import junit.framework.*;

import java.util.Arrays;
import ch.fluxron.fluxronapp.data.MessageFactory;
import ch.fluxron.fluxronapp.data.generated.ParamManager;

/**
 * Unit tests for the bluetooth class.
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

    public void testValidateChecksumGreater12B(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] expectation = new byte[]{
                (byte) 0xAA, (byte) 0xAA, //Start sequence
                (byte) 0x40, (byte) 0x08, (byte) 0x10, (byte) 0x00, //Command, Index + Subindex
                (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, //specifies length as 5Bytes
                (byte) 0x47, (byte) 0x49, (byte) 0x45, (byte) 0x54, (byte) 0x5a, //5Bytes
                (byte) 0xe0, (byte) 0x1}; //Checksum
        assertTrue(messageFactory.isChecksumValid(expectation));
    }

    public void testGenerateFullMessage(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] expectation = new byte[]{
                (byte) 0xAA, (byte) 0xAA,
                (byte) 0x40, (byte) 0x01, (byte) 0x30, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x72, (byte) 0x00 };
        messageFactory.printUnsignedByteArray(expectation);
        byte[] result = messageFactory.makeReadRequest(ParamManager.F_KNOB_A_DIGITAL_3001SUB1);
        messageFactory.printUnsignedByteArray(result);
        assertTrue(Arrays.equals(expectation, result));
    }

    public void testChecksumValid(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] someMessage = new byte[]{
                (byte) 0xAA, (byte) 0xAA,
                (byte) 0x40, (byte) 0x01, (byte) 0x30, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x72, (byte) 0x00 };
        assertTrue(messageFactory.isChecksumValid(someMessage));
    }

    public void testChecksumInvalid(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] someMessage = new byte[]{
                (byte) 0xAA, (byte) 0xAA,
                (byte) 0x40, (byte) 0x01, (byte) 0x30, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00 };
        assertFalse(messageFactory.isChecksumValid(someMessage));
    }

    public void testObjectConvertInt(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] result = messageFactory.convertDataObjectToByte(ParamManager.F_FLX_TEMP_FAN_LEVEL_MAX_3035SUB17, 5);
        messageFactory.printUnsignedByteArray(result);
        assertTrue(Arrays.equals(new byte[]{0x00, 0x00, 0x00, 0x05}, result));

        byte[] result2 = messageFactory.convertDataObjectToByte(ParamManager.F_PDO_1_MAPPING_FOR_A_PROCESS_DATA_VARIABLE_3_1A00SUB3, 12345);
        messageFactory.printUnsignedByteArray(result2);
        assertTrue(Arrays.equals(new byte[]{0x00, 0x00, 0x30, 0x39}, result2));
    }

    public void testObjectConvertString(){
        MessageFactory messageFactory = new MessageFactory();
        byte[] result = messageFactory.convertDataObjectToByte(ParamManager.F_MANUFACTURER_DEVICE_NAME_1008, "TROLL");
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
        byte[] result = messageFactory.makeWriteRequest(ParamManager.F_INHIBIT_TIME_1400SUB3, 55);
        messageFactory.printUnsignedByteArray(result);
        assertTrue(Arrays.equals(expectation, result));
    }
}
