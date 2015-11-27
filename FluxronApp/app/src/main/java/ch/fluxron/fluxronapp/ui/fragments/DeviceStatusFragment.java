package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceNotChanged;
import ch.fluxron.fluxronapp.ui.components.ParameterView;
import ch.fluxron.fluxronapp.ui.components.TemperatureBar;
import ch.fluxron.fluxronapp.ui.fragments.common.DeviceBaseFragment;
import ch.fluxron.fluxronapp.ui.util.DeviceTypeConverter;

public class DeviceStatusFragment extends DeviceBaseFragment {
    private List<TemperatureBar> temperatureBars;
    private List<ParameterView> parameters;
    private boolean ready = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View deviceView;
        if (savedInstanceState != null) {
            setDeviceAddress(savedInstanceState.getString(STATE_ADDRESS));
            setDeviceClass(savedInstanceState.getString(STATE_DEVICE_CLASS));
        }
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
        temperatureBars = new ArrayList<>();
        ViewGroup temperatureList = (ViewGroup) deviceView.findViewById(R.id.temperatureBars);
        for (int i = 0; i < temperatureList.getChildCount(); i++) {
            temperatureBars.add((TemperatureBar) temperatureList.getChildAt(i));
        }

        parameters = new ArrayList<>();
        ViewGroup paramList = (ViewGroup) deviceView.findViewById(R.id.paramGrid);
        for (int i = 0; i < paramList.getChildCount(); i++) {
            parameters.add((ParameterView) paramList.getChildAt(i));
        }
    }

    public void onEventMainThread(DeviceChanged inputMsg) {
        if (inputMsg.getDevice().getAddress().equals(getDeviceAddress()) && ready) {
            for(TemperatureBar p:temperatureBars){
                p.handleDeviceChanged(inputMsg);
            }
            for(ParameterView p:parameters){
                p.handleDeviceChanged(inputMsg);
            }
        }
    }

    public void onEventMainThread(DeviceNotChanged inputMsg) {
        if (inputMsg.getAddress().equals(getDeviceAddress()) && ready) {
            for(ParameterView p:parameters){
                p.handleDeviceNotChanged(inputMsg);
            }
        }
    }
}
