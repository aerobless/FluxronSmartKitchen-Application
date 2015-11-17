package ch.fluxron.fluxronapp;

import junit.framework.TestCase;

import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Unit Tests for the Device Class
 */
public class DeviceTest extends TestCase {

    public void testUnkownProductCode(){
        Device device = new Device();
        assertEquals(device.getProductCode(),0);
        assertEquals(device.getDeviceType(), Device.UNKNOWN_DEVICE_TYPE);
        assertEquals(device.getDeviceClass(), Device.UNKNOWN_DEVICE_CLASS);
    }

    public void testKownProductCode(){
        Device cclass = new Device();
        cclass.setProductCode(2573);
        assertEquals(cclass.getDeviceType(), "BAX");
        assertEquals(cclass.getDeviceClass(), "CClass");

        Device sclass = new Device();
        sclass.setProductCode(2574);
        assertEquals(sclass.getDeviceType(), "BAX");
        assertEquals(sclass.getDeviceClass(), "SClass");

        Device etx = new Device();
        etx.setProductCode(5130);
        assertEquals(etx.getDeviceType(), "ET");
        assertEquals(etx.getDeviceClass(), "ETX");
    }
}
