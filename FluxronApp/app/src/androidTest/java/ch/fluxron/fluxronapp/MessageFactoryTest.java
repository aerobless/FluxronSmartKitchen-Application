package ch.fluxron.fluxronapp;
import junit.framework.*;

import java.util.Arrays;

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
                (byte) 0x40, (byte) 0xCC}; //Checksum
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
        DeviceParameter dp = new DeviceParameter();
        dp.setIndex(new byte[]{(byte) 0x30, (byte) 0x01});
        dp.setSubindex((byte) 0x01);

        byte[] expectation = new byte[]{
                (byte) 0xAA, (byte) 0xAA,
                (byte) 0x40, (byte) 0x01, (byte) 0x30, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x72, (byte) 0x00 };
        messageFactory.printUnsignedByteArray(expectation);
        byte[] result = messageFactory.makeReadRequest(dp.getIndex(), dp.getSubindex());
        messageFactory.printUnsignedByteArray(result);
        assertTrue(Arrays.equals(expectation, result));
    }
}
