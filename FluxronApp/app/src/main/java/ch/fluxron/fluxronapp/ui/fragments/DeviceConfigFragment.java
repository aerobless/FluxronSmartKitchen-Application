package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AccessGranted;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceNotChanged;
import ch.fluxron.fluxronapp.ui.components.ConfigurableScrollView;
import ch.fluxron.fluxronapp.ui.components.ParameterEditable;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

public class DeviceConfigFragment extends Fragment{
    private static final String STATE_ADDRESS = "address";
    private IEventBusProvider provider;
    private String deviceAddress;
    private List<ParameterEditable> parameters;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            deviceAddress = savedInstanceState.getString(STATE_ADDRESS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_ADDRESS, deviceAddress);
        super.onSaveInstanceState(outState);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_cclass_device_config, container, false);
        provider = (IEventBusProvider)getContext().getApplicationContext();

        parameters = new ArrayList<>();
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.coilSetup));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.bltVisibility));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.kwfEnable));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.kwfMaxPower));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.kwfTempSetpoint));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.pmgEnable));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.pmgPowerReduction));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.baxFaultDelay));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.baxRpPsc));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.baxScanPanPsc));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.baxLiftPsc));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.flxActivePowerMax));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.baxFWarningTempLimit));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.baxGradientLimit));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.sevenSegConfig));
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.testMode));

        ((ConfigurableScrollView) deviceView.findViewById(R.id.scrollView)).setScrollOffset(100);
        return deviceView;
    }

    public void setDeviceAddress(String address){
        this.deviceAddress = address;
    }

    public void onEventMainThread(DeviceChanged inputMsg){
        if(inputMsg.getDevice().getAddress().equals(deviceAddress)){
            for(ParameterEditable p:parameters){
                p.setDeviceAddress(deviceAddress);
                p.handleDeviceChanged(inputMsg);
            }
        }
    }

    public void onEventMainThread(DeviceNotChanged inputMsg){
        if(inputMsg.getAddress().equals(deviceAddress)){
            for(ParameterEditable p:parameters){
                p.setDeviceAddress(deviceAddress);
                p.handleDeviceNotChanged(inputMsg);
            }
        }
    }

    public void onEventMainThread(AccessGranted inputMsg){
        for(ParameterEditable p:parameters){
            p.handleAccessLevel(inputMsg.getAccessLevel());
        }
    }
}
