package ch.fluxron.fluxronapp.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceLoaded;
import ch.fluxron.fluxronapp.ui.adapters.DeviceListAdapter;
import ch.fluxron.fluxronapp.ui.adapters.IDeviceClickListener;
import ch.fluxron.fluxronapp.ui.adapters.SectionedDeviceListAdapter;
import ch.fluxron.fluxronapp.ui.decorators.SpacesItemDecoration;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Implements a fragment that displays the list of discovered devices
 */
public class DeviceListFragment extends Fragment {
    private IEventBusProvider provider;
    private DeviceListAdapter listAdapter;
    private SectionedDeviceListAdapter sectionedAdapter;
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
        provider.getUiEventBus().post(new BluetoothDiscoveryCommand(true));
    }

    @Override
    public void onStop() {
        super.onStop();
        provider.getUiEventBus().post(new BluetoothDiscoveryCommand(false));
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
        deviceListView.setLayoutManager(layoutManager);

        // Item decoration for the list
        SpacesItemDecoration deco = new SpacesItemDecoration(30);
        deviceListView.addItemDecoration(deco);

        // List adapter
        listAdapter = new DeviceListAdapter(this.listener, this.provider);

        //TODO: sectioned list
        /*List<SectionedDeviceListAdapter.Section> sections = new ArrayList<>();
        sections.add(new SectionedDeviceListAdapter.Section(0,"Section 1"));
        sections.add(new SectionedDeviceListAdapter.Section(3,"Section 2"));*/

        //SectionedDeviceListAdapter.Section[] dummy = new SectionedDeviceListAdapter.Section[sections.size()];
        sectionedAdapter = new SectionedDeviceListAdapter(this.getActivity(), R.layout.section, R.id.section_text, listAdapter);
        //sectionedAdapter.setSections(sections.toArray(dummy));
        deviceListView.setAdapter(sectionedAdapter);

        return deviceView;
    }

    public void onEventMainThread(DeviceLoaded msg){
        Map<String, Integer> deviceCategories = listAdapter.addOrUpdate(msg.getDevice());
        sectionedAdapter.updateSections(deviceCategories);
    }

    public void onEventMainThread(DeviceChanged msg){
        Map<String, Integer> deviceCategories = listAdapter.addOrUpdate(msg.getDevice());
        sectionedAdapter.updateSections(deviceCategories);
    }
}
