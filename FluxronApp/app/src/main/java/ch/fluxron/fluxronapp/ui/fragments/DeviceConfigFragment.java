package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.ui.components.ConfigurableScrollView;
import ch.fluxron.fluxronapp.ui.components.ParameterEditable;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

public class DeviceConfigFragment extends Fragment{
    IEventBusProvider provider;
    String deviceAddress;
    ParameterEditable coilSetup;
    ParameterEditable bltVisibility;
    ParameterEditable kwfEnable;
    ParameterEditable kwfMaxPower;
    ParameterEditable kwfTempSetpoint;
    ParameterEditable pmgEnable;
    ParameterEditable pmgPowerReduction;
    ParameterEditable baxFaultDelay;
    ParameterEditable baxRpPsc;
    ParameterEditable baxScanPanPsc;
    ParameterEditable baxLiftPsc;
    ParameterEditable flxActivePowerMax;
    ParameterEditable baxFWarningTempLimit;
    ParameterEditable baxGradientLimit;
    ParameterEditable sevenSegConfig;
    ParameterEditable testMode;

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
        View deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_device_config, container, false);
        provider = (IEventBusProvider)getContext().getApplicationContext();
        coilSetup = (ParameterEditable) deviceView.findViewById(R.id.coilSetup);
        bltVisibility = (ParameterEditable) deviceView.findViewById(R.id.bltVisibility);
        kwfEnable = (ParameterEditable) deviceView.findViewById(R.id.kwfEnable);
        kwfMaxPower = (ParameterEditable) deviceView.findViewById(R.id.kwfMaxPower);
        kwfTempSetpoint = (ParameterEditable) deviceView.findViewById(R.id.kwfTempSetpoint);
        pmgEnable = (ParameterEditable) deviceView.findViewById(R.id.pmgEnable);
        pmgPowerReduction = (ParameterEditable) deviceView.findViewById(R.id.pmgPowerReduction);
        baxFaultDelay = (ParameterEditable) deviceView.findViewById(R.id.baxFaultDelay);
        baxRpPsc  = (ParameterEditable) deviceView.findViewById(R.id.baxRpPsc);
        baxScanPanPsc = (ParameterEditable) deviceView.findViewById(R.id.baxScanPanPsc);
        baxLiftPsc = (ParameterEditable) deviceView.findViewById(R.id.baxLiftPsc);
        flxActivePowerMax = (ParameterEditable) deviceView.findViewById(R.id.flxActivePowerMax);
        baxFWarningTempLimit = (ParameterEditable) deviceView.findViewById(R.id.baxFWarningTempLimit);
        baxGradientLimit = (ParameterEditable) deviceView.findViewById(R.id.baxGradientLimit);
        sevenSegConfig = (ParameterEditable) deviceView.findViewById(R.id.sevenSegConfig);
        testMode = (ParameterEditable) deviceView.findViewById(R.id.testMode);
        ((ConfigurableScrollView) deviceView.findViewById(R.id.scrollView)).setScrollOffset(100);
        return deviceView;
    }

    public void setDeviceAddress(String address){
        this.deviceAddress = address;
    }

    public void onEventMainThread(DeviceChanged inputMsg){
        if(inputMsg.getDevice().getAddress().equals(deviceAddress)){
            coilSetup.handleDeviceChanged(inputMsg);
            bltVisibility.handleDeviceChanged(inputMsg);
            kwfEnable.handleDeviceChanged(inputMsg);
            kwfMaxPower.handleDeviceChanged(inputMsg);
            kwfTempSetpoint.handleDeviceChanged(inputMsg);
            pmgEnable.handleDeviceChanged(inputMsg);
            pmgPowerReduction.handleDeviceChanged(inputMsg);
            baxFaultDelay.handleDeviceChanged(inputMsg);
            baxRpPsc.handleDeviceChanged(inputMsg);
            baxScanPanPsc.handleDeviceChanged(inputMsg);
            baxLiftPsc.handleDeviceChanged(inputMsg);
            flxActivePowerMax.handleDeviceChanged(inputMsg);
            baxFWarningTempLimit.handleDeviceChanged(inputMsg);
            baxGradientLimit.handleDeviceChanged(inputMsg);
            sevenSegConfig.handleDeviceChanged(inputMsg);
            testMode.handleDeviceChanged(inputMsg);

            coilSetup.setDeviceAddress(deviceAddress);
            bltVisibility.setDeviceAddress(deviceAddress);
            kwfEnable.setDeviceAddress(deviceAddress);
            kwfMaxPower.setDeviceAddress(deviceAddress);
            kwfTempSetpoint.setDeviceAddress(deviceAddress);
            pmgEnable.setDeviceAddress(deviceAddress);
            pmgPowerReduction.setDeviceAddress(deviceAddress);
            baxFaultDelay.setDeviceAddress(deviceAddress);
            baxRpPsc.setDeviceAddress(deviceAddress);
            baxScanPanPsc.setDeviceAddress(deviceAddress);
            baxLiftPsc.setDeviceAddress(deviceAddress);
            flxActivePowerMax.setDeviceAddress(deviceAddress);
            baxFWarningTempLimit.setDeviceAddress(deviceAddress);
            baxGradientLimit.setDeviceAddress(deviceAddress);
            sevenSegConfig.setDeviceAddress(deviceAddress);
            testMode.setDeviceAddress(deviceAddress);
        }
    }
}
