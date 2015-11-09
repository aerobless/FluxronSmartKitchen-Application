package ch.fluxron.fluxronapp.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.ui.components.ParameterView;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

public class DeviceStatusFragment extends Fragment {
    IEventBusProvider provider;
    ParameterView heatsink1;
    String deviceAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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
        provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider)getContext().getApplicationContext();
        provider.getUiEventBus().register(this);
        return deviceView;
    }

    public void setDeviceAddress(String address){
        this.deviceAddress = address;
    }

    public void onEventAsync(DeviceChanged inputMsg){
        if(inputMsg.getDevice().getAddress().equals(deviceAddress)){

        }
    }

    private void registerParameter(String param){
        provider.getUiEventBus().post(new RegisterParameterCommand(param));
    }
}
