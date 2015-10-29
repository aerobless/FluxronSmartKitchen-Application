package ch.fluxron.fluxronapp.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.ui.adapters.DeviceListAdapter;
import ch.fluxron.fluxronapp.ui.adapters.IAreaClickedListener;
import ch.fluxron.fluxronapp.ui.adapters.IDeviceClickListener;
import ch.fluxron.fluxronapp.ui.decorators.SpacesItemDecoration;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Implements a fragment that displays the list of discovered devices
 */
public class DeviceListFragment extends Fragment {
    private IEventBusProvider provider;
    private DeviceListAdapter listAdapter;
    private IDeviceClickListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setEventBusProvider(IEventBusProvider provider) {
        this.provider = provider;
    }

    public void setClickListener(IDeviceClickListener listener){
        this.listener = listener;
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
        View deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_device_list, container, false);

        // Set the list's properties
        RecyclerView deviceListView = (RecyclerView) deviceView.findViewById(R.id.deviceList);

        // Layout for the list
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        deviceListView.setLayoutManager(layoutManager);

        // Item decoration for the list
        SpacesItemDecoration deco = new SpacesItemDecoration(30);
        deviceListView.addItemDecoration(deco);

        // List adapter
        listAdapter = new DeviceListAdapter(this.listener, this.provider);
        deviceListView.setAdapter(listAdapter);

        return deviceView;
    }

    public void onEventMainThread(Object msg){

    }
}
