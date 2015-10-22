package ch.fluxron.fluxronapp;
import junit.framework.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ch.fluxron.fluxronapp.data.Bluetooth;
import ch.fluxron.fluxronapp.data.DeviceParameter;
import ch.fluxron.fluxronapp.data.MessageFactory;

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
        DeviceParameter dp = new DeviceParameter();
        dp.setIndex(new byte[]{(byte) 0x30, (byte) 0x01});
        dp.setSubindex((byte) 0x01);
        MessageFactory messageFactory = new MessageFactory();
        byte[] expectation = new byte[]{
                (byte) 0xAA, (byte) 0xAA,
                (byte) 0x40, (byte) 0x01, (byte) 0x30, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x72, (byte) 0x00 };
        messageFactory.printUnsignedByteArray(expectation);
        byte[] result = messageFactory.makeReadRequest(Bluetooth.F_KNOB_A_DIGITAL);
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
}
