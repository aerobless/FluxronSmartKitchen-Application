package ch.fluxron.fluxronapp.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.objectBase.DeviceParameter;
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
            DeviceParameter pHeatSink1 = inputMsg.getDevice().getDeviceParameter(heatsink1.getParameter());
            DeviceParameter pKwfSetpoint = inputMsg.getDevice().getDeviceParameter(kwfSetpoint.getParameter());
            DeviceParameter pkwfPower = inputMsg.getDevice().getDeviceParameter(kwfPower.getParameter());
            DeviceParameter ptempGradient = inputMsg.getDevice().getDeviceParameter(tempGradient.getParameter());
            DeviceParameter pTemperatureBar1 = inputMsg.getDevice().getDeviceParameter(temperatureBar1.getParameter());
            DeviceParameter pTemperatureBar2 = inputMsg.getDevice().getDeviceParameter(temperatureBar2.getParameter());
            DeviceParameter pTemperatureBar3 = inputMsg.getDevice().getDeviceParameter(temperatureBar3.getParameter());
            DeviceParameter pTemperatureBar4 = inputMsg.getDevice().getDeviceParameter(temperatureBar4.getParameter());

            if(pHeatSink1 != null){
                heatsink1.setValue(pHeatSink1.getValue());
            }
            if(pKwfSetpoint != null){
                kwfSetpoint.setValue(pKwfSetpoint.getValue());
            }
            if(pkwfPower != null){
                kwfPower.setValue(pkwfPower.getValue());
            }
            if(ptempGradient != null){
                tempGradient.setValue(ptempGradient.getValue());
            }
            if(pTemperatureBar1 != null){
                temperatureBar1.updateCurrentTemperature(Float.parseFloat(pTemperatureBar1.getValue()));
            }
            if(pTemperatureBar2 != null){
                temperatureBar2.updateCurrentTemperature(Float.parseFloat(pTemperatureBar2.getValue()));
            }
            if(pTemperatureBar3 != null){
                temperatureBar3.updateCurrentTemperature(Float.parseFloat(pTemperatureBar3.getValue()));
            }
            if(pTemperatureBar4 != null){
                temperatureBar4.updateCurrentTemperature(Float.parseFloat(pTemperatureBar4.getValue()));
            }
        }
    }
}
