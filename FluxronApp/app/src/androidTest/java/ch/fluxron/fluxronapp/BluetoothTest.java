package ch.fluxron.fluxronapp;
import junit.framework.*;

import java.util.Arrays;

import ch.fluxron.fluxronapp.data.Bluetooth;

/**
 * Unit tests for the bluetooth class.
 */
public class BluetoothTest extends TestCase {

    public void testGenerateChecksumLowChecksum(){
        Bluetooth bt = new Bluetooth();
        byte[] expectation = new byte[]{
                (byte) 0xAA, (byte) 0xAA, //Start sequence
                (byte) 0x40, (byte) 0x18, (byte) 0x10, (byte) 0x04,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x6c, (byte) 0x00}; //Checksum
        byte[] result = bt.generateChecksum(new byte[]{
                (byte) 0x40, (byte) 0x18, (byte) 0x10, (byte) 0x04,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
        assertTrue(Arrays.equals(expectation, result));
    }

    public void testGenerateChecksumHighChecksum(){
        Bluetooth bt = new Bluetooth();
        byte[] expectation = new byte[]{
                (byte) 0xAA, (byte) 0xAA, //Start sequence
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x44, (byte) 0x44, (byte) 0x00, (byte) 0x44,
                (byte) 0x00, (byte) 0xCC}; //Checksum
        byte[] result = bt.generateChecksum(new byte[]{
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x44, (byte) 0x44, (byte) 0x00, (byte) 0x44});
        assertTrue(Arrays.equals(expectation, result));
    }
}
