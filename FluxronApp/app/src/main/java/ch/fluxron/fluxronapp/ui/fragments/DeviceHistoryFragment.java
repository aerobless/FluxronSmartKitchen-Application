package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.ui.components.ParameterView;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

public class DeviceHistoryFragment extends Fragment {
    IEventBusProvider provider;
    String deviceAddress;
    ParameterView uptimePowerOnTime;
    ParameterView uptimeWorkingTime;

    ParameterView heatsinkPowerOnTime1;
    ParameterView heatsinkWorkingTime1;
    ParameterView heatsinkPowerOnTime2;
    ParameterView heatsinkWorkingTime2;

    ParameterView glassPowerOnTime1;
    ParameterView glassWorkingTime1;
    ParameterView glassPowerOnTime2;
    ParameterView glassWorkingTime2;


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
        View deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_device_history, container, false);
        provider = (IEventBusProvider)getContext().getApplicationContext();
        uptimePowerOnTime = (ParameterView) deviceView.findViewById(R.id.uptimePowerOnTime);
        uptimeWorkingTime = (ParameterView) deviceView.findViewById(R.id.uptimeWorkingTime);

        heatsinkPowerOnTime1 = (ParameterView) deviceView.findViewById(R.id.heatsinkPowerOnTime1);
        heatsinkPowerOnTime2 = (ParameterView) deviceView.findViewById(R.id.heatsinkPowerOnTime2);
        heatsinkWorkingTime1 = (ParameterView) deviceView.findViewById(R.id.heatsinkWorkingTime1);
        heatsinkWorkingTime2 = (ParameterView) deviceView.findViewById(R.id.heatsinkWorkingTime2);

        glassPowerOnTime1 = (ParameterView) deviceView.findViewById(R.id.glassPowerOnTime1);
        glassPowerOnTime2 = (ParameterView) deviceView.findViewById(R.id.glassPowerOnTime2);
        glassWorkingTime1 = (ParameterView) deviceView.findViewById(R.id.glassWorkingTime1);
        glassWorkingTime2 = (ParameterView) deviceView.findViewById(R.id.glassWorkingTime2);
        return deviceView;
    }

    public void setDeviceAddress(String address){
        this.deviceAddress = address;
    }

    public void onEventMainThread(DeviceChanged inputMsg){
        if(inputMsg.getDevice().getAddress().equals(deviceAddress)){
            uptimePowerOnTime.handleDeviceChanged(inputMsg);
            uptimeWorkingTime.handleDeviceChanged(inputMsg);
            heatsinkPowerOnTime1.handleDeviceChanged(inputMsg);
            heatsinkPowerOnTime2.handleDeviceChanged(inputMsg);
            heatsinkWorkingTime1.handleDeviceChanged(inputMsg);
            heatsinkWorkingTime2.handleDeviceChanged(inputMsg);
            glassPowerOnTime1.handleDeviceChanged(inputMsg);
            glassPowerOnTime2.handleDeviceChanged(inputMsg);
            glassWorkingTime1.handleDeviceChanged(inputMsg);
            glassWorkingTime2.handleDeviceChanged(inputMsg);
        }
    }
}
