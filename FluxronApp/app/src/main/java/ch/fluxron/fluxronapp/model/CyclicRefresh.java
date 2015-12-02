package ch.fluxron.fluxronapp.model;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothConnectionFailed;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothReadRequest;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothRequestFailed;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.CyclicRefreshCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceNotChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.objectBase.Device;

/**
 * Used to iterate through all registered devices and refresh their parameters.
 */
public class CyclicRefresh {
    private AtomicBoolean enabled = new AtomicBoolean(false);
    private final LruCache<String, Device> deviceCache;
    private final Set<String> refreshList;
    private IEventBusProvider provider;
    private String currentConnection;
    private final Object lock = new Object();
    private AtomicBoolean doNext = new AtomicBoolean(false);
    private AtomicBoolean forceNextDevice = new AtomicBoolean(false);
    private final Set<String> listOfInterestingParameters;
    private boolean discoveryMode = false;

    /**
     * Instantiates a new CyclicRefresh service.
     *
     * @param provider
     * @param deviceCache
     */
    public CyclicRefresh(IEventBusProvider provider, LruCache<String, Device> deviceCache) {
        this.provider = provider;
        this.deviceCache = deviceCache;
        this.refreshList = new HashSet<>();
        this.currentConnection = "";
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
        listOfInterestingParameters = new HashSet<>();
        refreshLoiP();
    }

    /**
     * Initializes the list containing all interesting parameters.
     *
     * @return
     */
    private void refreshLoiP() {
        synchronized (listOfInterestingParameters) {
            listOfInterestingParameters.clear();
            listOfInterestingParameters.add(ParamManager.F_CCLASS_1018SUB2_PRODUCT_CODE);
        }
    }

    /**
     * Copy a snapshot of the current deviceCache to the refreshList.
     *
     * @return
     */
    @NonNull
    private Set<String> copyDeviceCacheToRefreshList() {
        Map<String, Device> deviceMap;
        synchronized (deviceCache) {
            deviceMap = deviceCache.snapshot();
        }
        Set<String> deviceSet = new HashSet<>();
        for (Device device : deviceMap.values()) {
            deviceSet.add(device.getAddress());
        }
        return deviceSet;
    }

    /**
     * Used to inject devices loaded with a kitchen into the device manager.
     *
     * @param cmd
     */
    public void onEventAsync(CyclicRefreshCommand cmd) {
        if (cmd.getDeviceToRefresh().equals(CyclicRefreshCommand.ALL_DEVICES)) {
            Log.d("FLUXRON", "RECEIVED A COMMAND TO REFRESH ALL DEVICES");
            refreshLoiP();
            refreshList.clear();
            refreshList.addAll(copyDeviceCacheToRefreshList());
            discoveryMode = true;
            start();
        } else if (cmd.getDeviceToRefresh().equals(CyclicRefreshCommand.NONE)) {
            refreshList.clear();
            skipToNextDevice();
            discoveryMode = false;
            enabled.set(false);
        } else {
            refreshList.clear();
            discoveryMode = false;
            refreshList.add(cmd.getDeviceToRefresh());
            start();
        }
    }

    /**
     * CyclicRefresh will now iterate through all devices and refresh their parameters until
     * its told to stop.
     */
    private void start() {
        skipToNextDevice();
        if (enabled.compareAndSet(false, true)) {
            while (enabled.get()) {
                Set<String> localRefreshList;
                synchronized (refreshList) {
                    localRefreshList = new HashSet<>(refreshList);
                }
                for (String device : localRefreshList) {
                    if (!enabled.get()) {
                        break;
                    }
                    Set<String> tempList;
                    synchronized (listOfInterestingParameters) {
                        tempList = new HashSet<>(listOfInterestingParameters);
                    }
                    for (String param : tempList) {
                        postRequest(device, param);
                        if (forceNextDevice.compareAndSet(true, false)) {
                            break;
                        }
                    }
                }
                cooldown(1000);
                if (discoveryMode) {
                    refreshList.addAll(copyDeviceCacheToRefreshList());
                }
            }
        }
    }

    /**
     * Post a request to read all the parameters in the list of interesting parameters of a specific
     * device.
     *
     * @param device
     */
    private void postRequest(String device, String parameter) {
        BluetoothReadRequest req = new BluetoothReadRequest(device, parameter);
        synchronized (lock) {
            while (!doNext.get()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Log.d("FLUXRON", "Interruped exception in CyclicRefresh");
                }
            }
            currentConnection = req.getConnectionId();
            provider.getDalEventBus().post(req);
            //Log.d("FLUXRON", "POSTED A REQUEST FOR DATA FOR DEVICE " + device + " and param " + parameter);
            doNext.set(false);
        }
    }

    /**
     * Used to prevent memory churning when there are only fake devices in a kitchen.
     * Can also be used for debugging purposes, to slow down the communication with the devices.
     *
     * @param timeInMs
     */
    private static void cooldown(int timeInMs) {
        try {
            Thread.sleep(timeInMs);
        } catch (InterruptedException e) {
            Log.d("FLUXRON", "InterruptedException during Cooldown in CyclicRefresh");
        }
    }

    /**
     * Jumps to the next parameter when we have confirmation that the device has changed.
     *
     * @param inputMsg
     */
    public void onEventAsync(BluetoothDeviceChanged inputMsg) {
        skipToNextParam();
    }

    /**
     * If the pipe broke or the connection could not be established for other reasons.
     * Skips the current device. It will be automatically retried in the next cycle.
     *
     * @param inputMsg
     */
    public void onEventAsync(BluetoothConnectionFailed inputMsg) {
        String connectionID = inputMsg.getConnectionId();
        if (connectionID.equals(currentConnection)) {
            skipToNextDevice();
        }
    }

    /**
     * If the requested parameter is not available on the remote device or there is some other
     * communication error where we still get a response from the device. (There is not connection
     * loss here, just an error response from the remote device.)
     *
     * @param inputMsg
     */
    public void onEventAsync(BluetoothRequestFailed inputMsg) {
        String connectionID = inputMsg.getConnectionId();
        if (connectionID.equals(currentConnection)) {
            skipToNextParam();
        }
        provider.getUiEventBus().post(new DeviceNotChanged(inputMsg.getField(), inputMsg.getAddress()));
    }

    /**
     * Skip to the next parameter.
     */
    private void skipToNextParam() {
        synchronized (lock) {
            doNext.set(true);
            lock.notifyAll();
        }
    }

    /**
     * Skip to the next device.
     */
    private void skipToNextDevice() {
        //Log.d("Fluxron", "Skipping to next Device");
        forceNextDevice.set(true);
        skipToNextParam();
    }

    /**
     * Listens to RegisterParameterCommand and adds it to the List of interesting parameters.
     *
     * @param inputMsg
     */
    public void onEventAsync(RegisterParameterCommand inputMsg) {
        synchronized (listOfInterestingParameters) {
            listOfInterestingParameters.add(inputMsg.getParameter());
        }
    }
}
