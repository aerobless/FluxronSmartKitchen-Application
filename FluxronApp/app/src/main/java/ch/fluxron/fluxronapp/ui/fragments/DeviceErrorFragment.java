package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.ui.components.ErrorView;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

public class DeviceErrorFragment extends Fragment {
    private static final String STATE_ADDRESS = "address";
    private IEventBusProvider provider;
    private String deviceAddress;
    private ErrorView[] errorViews;

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
        View deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_cclass_device_errors, container, false);
        provider = (IEventBusProvider)getContext().getApplicationContext();

        ViewGroup list = (ViewGroup) deviceView.findViewById(R.id.errorViewList);
        errorViews = new ErrorView[list.getChildCount()];

        for(int i = 0; i < errorViews.length; i++){
            errorViews[i] = (ErrorView)list.getChildAt(i);
        }

        return deviceView;
    }

    public void setDeviceAddress(String address){
        this.deviceAddress = address;
    }

    public void onEventMainThread(DeviceChanged inputMsg){
        if(inputMsg.getDevice().getAddress().equals(deviceAddress)){
            for(ErrorView er :  errorViews){
                er.handleDeviceChanged(inputMsg);
            }
        }
    }
}
