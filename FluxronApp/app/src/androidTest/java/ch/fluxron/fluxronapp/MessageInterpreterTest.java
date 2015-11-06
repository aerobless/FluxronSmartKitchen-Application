package ch.fluxron.fluxronapp;

import junit.framework.TestCase;

import ch.fluxron.fluxronapp.data.MessageFactory;
import ch.fluxron.fluxronapp.data.MessageInterpreter;

/**
 * Unit tests for MessageInterpreter class.
 */
public class MessageInterpreterTest extends TestCase {

    public void testChecksumValid(){
        MessageFactory messageFactory = new MessageFactory();
        MessageInterpreter interpreter = new MessageInterpreter(messageFactory);
        byte[] someMessage = new byte[]{
                (byte) 0xAA, (byte) 0xAA,
                (byte) 0x40, (byte) 0x01, (byte) 0x30, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x72, (byte) 0x00 };
        assertTrue(interpreter.isChecksumValid(someMessage));
    }

    public void testChecksumInvalid(){
        MessageFactory messageFactory = new MessageFactory();
        MessageInterpreter interpreter = new MessageInterpreter(messageFactory);
        byte[] someMessage = new byte[]{
                (byte) 0xAA, (byte) 0xAA,
                (byte) 0x40, (byte) 0x01, (byte) 0x30, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00 };
        assertFalse(interpreter.isChecksumValid(someMessage));
    }

    public void testValidateChecksumGreater12B(){
        MessageFactory messageFactory = new MessageFactory();
        MessageInterpreter interpreter = new MessageInterpreter(messageFactory);
        byte[] expectation = new byte[]{
                (byte) 0xAA, (byte) 0xAA, //Start sequence
                (byte) 0x40, (byte) 0x08, (byte) 0x10, (byte) 0x00, //Command, Index + Subindex
                (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, //specifies length as 5Bytes
                (byte) 0x47, (byte) 0x49, (byte) 0x45, (byte) 0x54, (byte) 0x5a, //5Bytes
                (byte) 0xe0, (byte) 0x1}; //Checksum
        assertTrue(interpreter.isChecksumValid(expectation));
    }
}
