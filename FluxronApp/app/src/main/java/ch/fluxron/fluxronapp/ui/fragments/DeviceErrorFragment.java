package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.ui.components.ErrorView;
import ch.fluxron.fluxronapp.ui.fragments.common.DeviceBaseFragment;
import ch.fluxron.fluxronapp.ui.util.DeviceTypeConverter;

public class DeviceErrorFragment extends DeviceBaseFragment {
    private ErrorView[] errorViews;
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
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_cclass_device_errors, container, false);
            init(deviceView);
            ready = true;
        } else if (getDeviceClass().equals(DeviceTypeConverter.SCLASS)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_sclass_device_errors, container, false);
            init(deviceView);
            ready = true;
        }  else if (getDeviceClass().equals(DeviceTypeConverter.ETX)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_etx_device_errors, container, false);
            init(deviceView);
            ready = true;
        } else {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_unsupported_device, container, false);
        }
        return deviceView;
    }

    private void init(View deviceView){
        ViewGroup list = (ViewGroup) deviceView.findViewById(R.id.errorViewList);
        errorViews = new ErrorView[list.getChildCount()];

        for (int i = 0; i < errorViews.length; i++) {
            errorViews[i] = (ErrorView) list.getChildAt(i);
        }
    }

    public void onEventMainThread(DeviceChanged inputMsg) {
        if (inputMsg.getDevice().getAddress().equals(getDeviceAddress()) && ready) {
            for (ErrorView er : errorViews) {
                er.handleDeviceChanged(inputMsg);
            }
        }
    }
}
