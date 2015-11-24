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
import ch.fluxron.fluxronapp.ui.fragments.common.DeviceBaseFragment;
import ch.fluxron.fluxronapp.ui.util.DeviceTypeConverter;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

public class DeviceConfigFragment extends DeviceBaseFragment {
    private List<ParameterEditable> parameters;
    private boolean ready = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View deviceView;
        if (getDeviceClass().equals(DeviceTypeConverter.CCLASS)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_cclass_device_config, container, false);
            init(deviceView);
            ready = true;
        } else if (getDeviceClass().equals(DeviceTypeConverter.SCLASS)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_sclass_device_config, container, false);
            init(deviceView);
            ready = true;
        } else if (getDeviceClass().equals(DeviceTypeConverter.ETX)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_etx_device_config, container, false);
            init(deviceView);
            ready = true;
        } else {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_unsupported_device, container, false);
        }
        return deviceView;
    }

    private void init(View deviceView) {
        parameters = new ArrayList<>();
        ViewGroup list = (ViewGroup) deviceView.findViewById(R.id.editableViewList);
        for (int i = 0; i < list.getChildCount(); i++) {
            parameters.add((ParameterEditable) list.getChildAt(i));
        }
        ((ConfigurableScrollView) deviceView.findViewById(R.id.scrollView)).setScrollOffset(100);
    }

    public void onEventMainThread(DeviceChanged inputMsg) {
        if (inputMsg.getDevice().getAddress().equals(getDeviceAddress()) && ready) {
            for (ParameterEditable p : parameters) {
                p.setDeviceAddress(getDeviceAddress());
                p.handleDeviceChanged(inputMsg);
            }
        }
    }

    public void onEventMainThread(DeviceNotChanged inputMsg) {
        if (inputMsg.getAddress().equals(getDeviceAddress()) && ready) {
            for (ParameterEditable p : parameters) {
                p.setDeviceAddress(getDeviceAddress());
                p.handleDeviceNotChanged(inputMsg);
            }
        }
    }

    public void onEventMainThread(AccessGranted inputMsg) {
        if (ready) {
            for (ParameterEditable p : parameters) {
                p.handleAccessLevel(inputMsg.getAccessLevel());
            }
        }
    }
}
