package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceNotChanged;
import ch.fluxron.fluxronapp.ui.components.ParameterView;
import ch.fluxron.fluxronapp.ui.components.TemperatureBar;
import ch.fluxron.fluxronapp.ui.fragments.common.DeviceBaseFragment;
import ch.fluxron.fluxronapp.ui.util.DeviceTypeConverter;

public class DeviceStatusFragment extends DeviceBaseFragment {
    private ParameterView heatsink1;
    private ParameterView kwfSetpoint;
    private ParameterView kwfPower;
    private ParameterView tempGradient;
    private TemperatureBar temperatureBar1;
    private TemperatureBar temperatureBar2;
    private TemperatureBar temperatureBar3;
    private TemperatureBar temperatureBar4;
    private boolean ready = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View deviceView;
        if (getDeviceClass().equals(DeviceTypeConverter.CCLASS)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_cclass_device_status, container, false);
            initControls(deviceView);
            ready = true;
        } else if (getDeviceClass().equals(DeviceTypeConverter.SCLASS)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_sclass_device_status, container, false);
            initControls(deviceView);
            ready = true;
        } else if (getDeviceClass().equals(DeviceTypeConverter.ETX)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_etx_device_status, container, false);
            initControls(deviceView);
            ready = true;
        } else {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_unsupported_device, container, false);
        }
        return deviceView;
    }

    private void initControls(View deviceView) {
        heatsink1 = (ParameterView) deviceView.findViewById(R.id.heatsink1);
        kwfSetpoint = (ParameterView) deviceView.findViewById(R.id.kwfSetpoint);
        kwfPower = (ParameterView) deviceView.findViewById(R.id.kwfPower);
        tempGradient = (ParameterView) deviceView.findViewById(R.id.tempGradient);
        temperatureBar1 = (TemperatureBar) deviceView.findViewById(R.id.temperature1);
        temperatureBar2 = (TemperatureBar) deviceView.findViewById(R.id.temperature2);
        temperatureBar3 = (TemperatureBar) deviceView.findViewById(R.id.temperature3);
        temperatureBar4 = (TemperatureBar) deviceView.findViewById(R.id.temperature4);
    }

    public void onEventMainThread(DeviceChanged inputMsg) {
        if (inputMsg.getDevice().getAddress().equals(getDeviceAddress()) && ready) {
            heatsink1.handleDeviceChanged(inputMsg);
            kwfSetpoint.handleDeviceChanged(inputMsg);
            kwfPower.handleDeviceChanged(inputMsg);
            tempGradient.handleDeviceChanged(inputMsg);
            temperatureBar1.handleDeviceChanged(inputMsg);
            temperatureBar2.handleDeviceChanged(inputMsg);
            temperatureBar3.handleDeviceChanged(inputMsg);
            temperatureBar4.handleDeviceChanged(inputMsg);
        }
    }

    public void onEventMainThread(DeviceNotChanged inputMsg) {
        if (inputMsg.getAddress().equals(getDeviceAddress()) && ready) {
            heatsink1.handleDeviceNotChanged(inputMsg);
            kwfSetpoint.handleDeviceNotChanged(inputMsg);
            kwfPower.handleDeviceNotChanged(inputMsg);
            tempGradient.handleDeviceNotChanged(inputMsg);
        }
    }
}
