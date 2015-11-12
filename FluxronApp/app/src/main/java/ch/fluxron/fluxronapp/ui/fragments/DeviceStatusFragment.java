package ch.fluxron.fluxronapp.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.ui.components.ParameterView;
import ch.fluxron.fluxronapp.ui.components.TemperatureBar;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

public class DeviceStatusFragment extends Fragment {
    IEventBusProvider provider;
    ParameterView heatsink1;
    ParameterView kwfSetpoint;
    ParameterView kwfPower;
    ParameterView tempGradient;
    TemperatureBar temperatureBar1;
    TemperatureBar temperatureBar2;
    TemperatureBar temperatureBar3;
    TemperatureBar temperatureBar4;
    String deviceAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        provider.getUiEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        provider.getUiEventBus().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_device_status, container, false);
        heatsink1 = (ParameterView) deviceView.findViewById(R.id.heatsink1);
        kwfSetpoint = (ParameterView)deviceView.findViewById(R.id.kwfSetpoint);
        kwfPower = (ParameterView) deviceView.findViewById(R.id.kwfPower);
        tempGradient = (ParameterView) deviceView.findViewById(R.id.tempGradient);
        temperatureBar1 = (TemperatureBar) deviceView.findViewById(R.id.temperature1);
        temperatureBar1.updateCurrentTemperature(80);
        temperatureBar2 = (TemperatureBar) deviceView.findViewById(R.id.temperature2);
        temperatureBar2.updateCurrentTemperature(40);
        temperatureBar3 = (TemperatureBar) deviceView.findViewById(R.id.temperature3);
        temperatureBar3.updateCurrentTemperature(130);
        temperatureBar4 = (TemperatureBar) deviceView.findViewById(R.id.temperature4);
        temperatureBar4.updateCurrentTemperature(145.3f);
        provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider)getContext().getApplicationContext();
        return deviceView;
    }

    public void setDeviceAddress(String address){
        this.deviceAddress = address;
    }

    public void onEventMainThread(DeviceChanged inputMsg){
        if(inputMsg.getDevice().getAddress().equals(deviceAddress)){
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
}
