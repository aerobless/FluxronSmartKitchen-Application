package ch.fluxron.fluxronapp.ui.util;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChangeCommand;
import ch.fluxron.fluxronapp.objectBase.CoilConfiguration;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;

/**
 * Used to determine which configuration is active based on specific parameter values
 */
public class CoilSetupConfigurator {
    Map<String, CoilConfiguration> configurationMap;

    public CoilSetupConfigurator() {
        configurationMap = new HashMap<>();
        configurationMap.put("Ø 240mm / 3.5kW", new CoilConfiguration("Ø 240mm / 3.5kW", 1, 0, 0, 0, 50, 0));
        configurationMap.put("Ø 240mm / 5kW", new CoilConfiguration("Ø 240mm / 5kW", 1, 18500, 75, 65, 50, 6500));
        configurationMap.put("Wok 300mm / 3.5kW", new CoilConfiguration("Wok 300mm / 3.5kW", 1, 0, 0, 0, 0, 0));
        configurationMap.put("Wok 300mm / 5kW", new CoilConfiguration("Wok 300mm / 5kW", 1, 20000, 65, 50, 50, 6500));
        configurationMap.put("Wok 300mm / 8kW", new CoilConfiguration("Wok 300mm / 8kW", 1, 18250, 65, 50, 62, 8000));
        configurationMap.put("Ø 270mm / 3.5kW", new CoilConfiguration("Ø 270mm / 3.5kW", 1, 0, 0, 0, 0, 0));
        configurationMap.put("Ø 270mm / 5kW", new CoilConfiguration("Ø 270mm / 5kW", 1, 20000, 65, 50, 50, 6500));
        configurationMap.put("2x ▢130x270mm / 5kW", new CoilConfiguration("2x ▢130x270mm / 5kW", 4, 18300, 35, 20, 50, 6500));
        configurationMap.put("Ø 270mm / 8kW", new CoilConfiguration("Ø 270mm / 8kW", 1, 19500, 65, 50, 62, 8000));
        configurationMap.put("2x ▢130x270mm / 8kW", new CoilConfiguration("2x ▢130x270mm / 8kW", 4, 19500, 40, 25, 58, 8000));
        configurationMap.put("4x ▢130 / 8kW", new CoilConfiguration("4x ▢130 / 8kW", 4, 21000, 30, 15, 54, 8000));
        configurationMap.put("Ø 305mm / 5kW", new CoilConfiguration("Ø 305mm / 5kW", 1, 20000, 65, 50, 50, 0));
        configurationMap.put("2x ▢145x305mm / 5kW", new CoilConfiguration("2x ▢145x305mm / 5kW", 4, 20000, 40, 25, 50, 0));
        configurationMap.put("▢305mm / 8kW", new CoilConfiguration("▢305mm / 8kW", 4, 21000, 65, 50, 56, 8000));
        configurationMap.put("Ø 305mm / 8kW", new CoilConfiguration("Ø 305mm / 8kW", 4, 20250, 65, 50, 62, 8000));
        configurationMap.put("2x ▢145x305mm / 8kW", new CoilConfiguration("2x ▢145x305mm / 8kW", 4, 19250, 30, 20, 58, 8000));
        configurationMap.put("4x ▢145mm / 8kW", new CoilConfiguration("4x ▢145mm / 8kW", 4, 19500, 30, 15, 56, 8000));
        configurationMap.put("▢185x385mm / 3.5kW", new CoilConfiguration("▢185x385mm / 3.5kW", 2, 0, 0, 0, 0, 0));
        configurationMap.put("Wok 400mm / 8kW", new CoilConfiguration("Wok 400mm / 8kW", 1, 19250, 65, 50, 60, 8000));
        configurationMap.put("4x ▢160mm / 8kW", new CoilConfiguration("4x ▢160mm / 8kW", 4, 19250, 30, 15, 56, 8000));
    }

    public CoilConfiguration getCoilConfig(String input) {
        return configurationMap.get(input);
    }

    public boolean postConfiguration(String config, String address, String deviceClass, IEventBusProvider provider) {
        boolean success = false;
        CoilConfiguration configuration = configurationMap.get(config);
        if (configuration != null && deviceClass.equals(DeviceTypeConverter.SCLASS)) {
            provider.getUiEventBus().post(new DeviceChangeCommand(address, new ParameterValue(ParamManager.F_SCLASS_3035SUB4_FLX_FREQUENCY, "" + configuration.getFrequency())));
            provider.getUiEventBus().post(new DeviceChangeCommand(address, new ParameterValue(ParamManager.F_SCLASS_3037SUB7_PAN_DETECT_ON_LIMIT, "" + configuration.getPanOn())));
            provider.getUiEventBus().post(new DeviceChangeCommand(address, new ParameterValue(ParamManager.F_SCLASS_3037SUB8_PAN_DETECT_OFF_LIMIT, "" + configuration.getPanOff())));
            provider.getUiEventBus().post(new DeviceChangeCommand(address, new ParameterValue(ParamManager.F_SCLASS_2000SUB6_COIL_PEAK_CURRENT_RATED, "" + configuration.getCoilPeakCurrentRated())));
            provider.getUiEventBus().post(new DeviceChangeCommand(address, new ParameterValue(ParamManager.F_SCLASS_3035SUBB_FLX_ACTIVE_POWER_MAX, "" + configuration.getpMax())));
            success = true;
        } else {
            Log.d("Fluxron", "This device class or configuration has not been implemented yet.");
        }
        return success;
    }
}
