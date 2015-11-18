package ch.fluxron.fluxronapp.data.mocking;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.fluxron.fluxronapp.data.IEventBusProvider;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;

/**
 * Responds to BluetoothDiscoveryCommands and replies with Fake devices.
 */
public class FakeBluetooth {
    private IEventBusProvider provider;
    private static final boolean FAKE_ENABLED = true;
    private static final int FAKE_DEVICE_COUNT = 10;
    private boolean discoveryActive = true;
    private List<Integer> deviceTypes;

    public FakeBluetooth(IEventBusProvider provider) {
        this.provider = provider;
        if(FAKE_ENABLED){
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
     * @param cmd
     */
    public void onEventAsync(BluetoothDiscoveryCommand cmd) {
        if(cmd.isEnabled()){
            discoveryActive = true;
           startDeviceDiscovery();
            Log.d("Fluxron","Fake Discovery Request");
        } else {
            discoveryActive = false;
        }
    }

    private void startDeviceDiscovery(){
        for(int i=0; i<FAKE_DEVICE_COUNT; i++){
            if(!discoveryActive){
                break;
            }

            int sleepyTime = randInt(1, 4);
            try {
                Thread.sleep(sleepyTime*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Device unreal = generateFakeDevice();
            Log.d("FLUXRON","Fake device generated");
            provider.getDalEventBus().post(new BluetoothDeviceFound(unreal));
        }
    }

    private Device generateFakeDevice(){
        int deviceID = randInt(100, 999);
        String deviceMac = randInt(10,99)+":"+randInt(10,99)+":"+randInt(10,99)+":"+randInt(10,99)+":"+randInt(10,99)+":"+randInt(10,99);
        Device unreal = new Device("FAKE_"+deviceID, deviceMac, false);
        int deviceType = deviceTypes.get(randInt(0, 3));
        ParameterValue productCodeParam = new ParameterValue(ParamManager.F_SCLASS_1018SUB2_PRODUCT_CODE, deviceType+"");
        unreal.setDeviceParameter(productCodeParam);
        unreal.setBonded(true);
        return unreal;
    }

    private static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
