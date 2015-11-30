package ch.fluxron.fluxronapp.data.mocking;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.fluxron.fluxronapp.data.IEventBusProvider;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;

/**
 * Responds to BluetoothDiscoveryCommands and replies with Fake devices.
 * Can be used to test application with a lot of devices or to test specific devices
 * that we don't have on hand. For example we did not have a ETX device to tests with
 * but by emulating it here we could still build a layout for it.
 */
public class FakeBluetooth {
    private IEventBusProvider provider;
    private boolean discoveryActive = true;
    private List<Integer> deviceTypes;

    //Settings:
    private static final boolean FAKE_GENERATION_ENABLED = false;
    private static final boolean FAKE_RESPONSE_ENABLED = false;
    private static final int FAKE_DEVICE_COUNT = 10; //Max 99
    private static final int DEVICE_TYPE = 5150; //When FAKE_RESPONSE is enabled, all devices will report that they are of this device type.

    /**
     * Instantiates a new FakeBluetooth module.
     *
     * @param provider
     */
    public FakeBluetooth(IEventBusProvider provider) {
        this.provider = provider;
        if (FAKE_GENERATION_ENABLED) {
            this.provider.getDalEventBus().register(this);
        }
        deviceTypes = new ArrayList<>();
        deviceTypes.add(15373);
        deviceTypes.add(2576);
        deviceTypes.add(2576);
        deviceTypes.add(12816);
    }

    /**
     * Starts/Stops the discovery of new devices via bluetooth.
     *
     * @param cmd Message saying whether to enable or disable the discovery.
     */
    public void onEventAsync(BluetoothDiscoveryCommand cmd) {
        if (cmd.isEnabled()) {
            discoveryActive = true;
            startDeviceDiscovery();
            Log.d("Fluxron", "Fake Discovery Request");
        } else {
            discoveryActive = false;
        }
    }

    /**
     * Discovers a specific amount of fake devices
     */
    private void startDeviceDiscovery() {
        for (int i = 0; i < FAKE_DEVICE_COUNT; i++) {
            if (!discoveryActive) {
                break;
            }

            int sleepyTime = randInt(1, 4);
            try {
                Thread.sleep(sleepyTime * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Device unreal = generateFakeDevice(i);
            Log.d("FLUXRON", "Fake device generated");
            provider.getDalEventBus().post(new BluetoothDeviceFound(unreal));
        }
    }

    /**
     * Generates a fake device.
     *
     * @param input the device number, used to always generate the "same" devices.
     * @return a Device
     */
    private Device generateFakeDevice(int input) {
        int deviceID = randInt(100, 999);
        String deviceMac;
        if (input < 10) {
            deviceMac = "FF:FF:FF:FF:FF:0" + input;
        } else {
            deviceMac = "FF:FF:FF:FF:FF:" + input;
        }
        Device unreal = new Device("FAKE_" + deviceID, deviceMac, false);
        int deviceType = deviceTypes.get(randInt(0, 3));
        ParameterValue productCodeParam = new ParameterValue(ParamManager.F_SCLASS_1018SUB2_PRODUCT_CODE, deviceType + "");
        unreal.setDeviceParameter(productCodeParam);
        unreal.setBonded(true);
        return unreal;
    }

    /**
     * Generates a random int between min & max.
     *
     * @param min
     * @param max
     * @return
     */
    private static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * Responds to read requests on fake devices.
     * Always returns the product code. Doesn't replay to the actual request.
     *
     * @param cmd a ReadRequest.
     */
    public void onEventAsync(BluetoothReadRequest cmd) {
        if (FAKE_RESPONSE_ENABLED) {
            if (cmd.getAddress().contains("FF:FF:FF:FF")) {
                RequestResponseConnection deviceChanged = new BluetoothDeviceChanged(cmd.getAddress(), "1018sub2", DEVICE_TYPE);
                deviceChanged.setConnectionId(cmd);
                provider.getDalEventBus().post(deviceChanged);
            }
        }
    }
}
