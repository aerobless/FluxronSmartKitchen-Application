package ch.fluxron.fluxronapp.model;

import android.util.Log;
import android.util.LruCache;

import java.util.Date;
import java.util.Map;

import ch.fluxron.fluxronapp.data.Bluetooth;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.modelDal.ToastProduced;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothConnectionFailed;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceChanged;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothWriteRequest;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothBondingCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChangeCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceFailed;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceLoaded;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.InjectDevicesCommand;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;
import ch.fluxron.fluxronapp.objectBase.DevicePosition;

/**
 * Manages & caches bluetooth devices.
 */
public class DeviceManager {
    private IEventBusProvider provider;
    private final LruCache<String, Device> deviceCache;
    private Map<String, ch.fluxron.fluxronapp.data.generated.DeviceParameter> paramMap;
    private CyclicRefresh cyclicRefresh;
    private ParamValidator validator;

    private static final String PARAM_PRODUCT_CODE = "1018sub2";

    public DeviceManager(IEventBusProvider provider) {
        this.provider = provider;
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
        deviceCache = new LruCache<>(256);
        cyclicRefresh = new CyclicRefresh(provider, deviceCache);
        paramMap = new ParamManager().getParamMap();
        validator = new ParamValidator();
    }

    /**
     * Used to inject devices loaded with a kitchen into the device manager.
     *
     * @param cmd
     */
    public void onEventAsync(InjectDevicesCommand cmd) {
        synchronized (deviceCache) {
            deviceCache.evictAll();
            for (DevicePosition d : cmd.getDeviceList()) {
                if (deviceCache.get(d.getDeviceId()) == null) {
                    deviceCache.put(d.getDeviceId(), new Device(d.getName(), d.getDeviceId(), false));
                }
            }
        }
    }

    /**
     * Listens to DeviceChangedCommands. Validates incoming change requests and then
     * sends them on to the bluetooth module. If a change request is invalid a response is
     * sent to the UI.
     *
     * @param cmd
     */
    public void onEventAsync(DeviceChangeCommand cmd) {
        ParameterValue changedParam = cmd.getChangeRequest();
        if (validator.isValid(changedParam)) {
            String address = cmd.getAddress();
            String field = cmd.getChangeRequest().getName();
            Object value = validator.convertToObject(changedParam);
            provider.getDalEventBus().post(new BluetoothWriteRequest(address, field, value));
        } else {
            provider.getUiEventBus().post(new ch.fluxron.fluxronapp.events.modelUi.ToastProduced("Illegal Value entered"));
            Log.d("Fluxron", "Illegal Value entered");
            //TODO: return proper message instead of toast
        }
    }

    /**
     * Enables or disables the discovery of bluetooth devices.
     *
     * @param inputCmd
     */
    public void onEventAsync(ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothDiscoveryCommand inputCmd) {
        RequestResponseConnection btDiscoveryCmd = new BluetoothDiscoveryCommand(inputCmd.isEnabled());
        btDiscoveryCmd.setConnectionId(inputCmd);
        provider.getDalEventBus().post(btDiscoveryCmd);

        //Send all cached devices up
        Map<String, Device> deviceCacheSnapshot;
        synchronized (deviceCache) {
            deviceCacheSnapshot = deviceCache.snapshot();
        }
        for (Device d : deviceCacheSnapshot.values()) {
            RequestResponseConnection deviceLoaded = new DeviceLoaded(d);
            deviceLoaded.setConnectionId(inputCmd);
            provider.getUiEventBus().post(deviceLoaded);
        }
    }

    /**
     * Handles BluetoothDeviceChanged event. Stores device in cache and notifies UI
     *
     * @param inputMsg
     */
    public void onEventAsync(BluetoothDeviceChanged inputMsg) {
        //Log.d("FLUXRON", "Device " + inputMsg.getAddress() + " has reported " + inputMsg.getValue() + " for field " + inputMsg.getField());
        Device device;
        synchronized (deviceCache) {
            device = deviceCache.get(inputMsg.getAddress());
            if (device == null) {
                return;
            }
        }
        //Log.d("Setting param",device.getDeviceClass() + "_" + inputMsg.getField()+"   "+Integer.toString(inputMsg.getValue()));
        /**
         * The product code is needed to correctly store all other parameters. So we're
         * directly storing it as a parameters of the device.
         */
        if (inputMsg.getField().equals(PARAM_PRODUCT_CODE)) {
            device.setProductCode(inputMsg.getValue());
            //Log.d("FLUXRON", "SET PRODUCT CODE TO"+inputMsg.getValue());
        } else if (inputMsg.getField().equals(Bluetooth.DEVICE_BONDED)) {
            device.setBonded(true);
            device.setLastContact(new Date());
        } else if (paramMap.get(device.getDeviceClass() + "_" + inputMsg.getField()) != null) {
            device.setDeviceParameter(new ParameterValue(device.getDeviceClass() + "_" + inputMsg.getField(), Integer.toString(inputMsg.getValue())));
            device.setBonded(true);
            device.setLastContact(new Date());
        }
        addDeviceToCache(device);
        RequestResponseConnection deviceChanged = new DeviceChanged(deviceCache.get(inputMsg.getAddress()));
        deviceChanged.setConnectionId(inputMsg);
        provider.getUiEventBus().post(deviceChanged);
    }

    /**
     * Relays bonding commands to DAL
     *
     * @param inputMsg
     */
    public void onEventAsync(BluetoothBondingCommand inputMsg) {
        RequestResponseConnection req = new ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothBondingCommand(inputMsg.getAddress());
        req.setConnectionId(inputMsg);
        provider.getDalEventBus().post(req);
    }

    /**
     * Relays toast messages to UI
     *
     * @param inputToast
     */
    public void onEventAsync(ToastProduced inputToast) {
        RequestResponseConnection freshToast = new ch.fluxron.fluxronapp.events.modelUi.ToastProduced(inputToast.getMessage());
        freshToast.setConnectionId(inputToast);
        provider.getUiEventBus().post(freshToast);
    }

    /**
     * Handles BluetoothDeviceFound event. Validates devices and notifies UI
     *
     * @param inputMsg
     */
    public void onEventAsync(BluetoothDeviceFound inputMsg) {
        Device device = inputMsg.getDevice();
        if (device != null && isFluxronDevice(device.getName())) {
            Device cachedDevice;
            synchronized (deviceCache) {
                cachedDevice = deviceCache.get(device.getAddress());
            }
            if (cachedDevice == null) {
                Log.d("FLUXRON", "New Device found: " + device.getName() + " " + device.getAddress());
                addDeviceToCache(device);
                RequestResponseConnection deviceLoaded = new DeviceLoaded(device);
                deviceLoaded.setConnectionId(inputMsg);
                provider.getUiEventBus().post(deviceLoaded);
            } else {
                cachedDevice.setLastContact(new Date());
                cachedDevice.setBonded(device.isBonded());
                addDeviceToCache(cachedDevice);
            }
        }
    }

    /**
     * Add a new device to the cache.
     *
     * @param device
     */
    private void addDeviceToCache(Device device) {
        device.setLastContact(new Date());
        synchronized (deviceCache) {
            deviceCache.put(device.getAddress(), device);
        }
    }

    /**
     * Forwards device failures to the UI.
     *
     * @param inputMsg
     */
    public void onEventAsync(BluetoothConnectionFailed inputMsg) {
        provider.getUiEventBus().post(new DeviceFailed(inputMsg.getAddress()));
    }

    /**
     * Light filter to prevent non-fluxron devices from getting listed.
     *
     * @param deviceName
     * @return true if deviceName starts with FLX, DGL, HC-06 or HM-Soft, which identifies it as a potential Fluxron Device.
     */
    public boolean isFluxronDevice(String deviceName) {
        return deviceName.matches("(FLX|DGL|HC-06|HMSoft|FAKE).*");
    }
}
