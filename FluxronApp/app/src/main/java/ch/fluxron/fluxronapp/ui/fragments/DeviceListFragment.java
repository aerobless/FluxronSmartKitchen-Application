package ch.fluxron.fluxronapp.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothTestCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.CyclicRefreshCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceLoaded;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.ui.adapters.DeviceListAdapter;
import ch.fluxron.fluxronapp.ui.adapters.IDeviceClickListener;
import ch.fluxron.fluxronapp.ui.adapters.SectionedDeviceListAdapter;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Implements a fragment that displays the list of discovered devices
 */
public class DeviceListFragment extends Fragment implements IDeviceClickListener{
    public interface IDeviceAddedListener{
        void onDeviceAddRequested(Device d);
    }

    private IEventBusProvider provider;
    private DeviceListAdapter listAdapter;
    private SectionedDeviceListAdapter sectionedAdapter;
    private IDeviceClickListener listener;
    private boolean discoveryActive = true;
    private TextView discoveryStatus;
    private IDeviceAddedListener addListener;



    public void setEventBusProvider(IEventBusProvider provider) {
        this.provider = provider;
        provider.getUiEventBus().register(this);
        setClickListener(this);
    }

    public void setClickListener(IDeviceClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        provider.getUiEventBus().post(new BluetoothDiscoveryCommand(true));
        provider.getUiEventBus().post(new CyclicRefreshCommand(true));
        discoveryActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        provider.getUiEventBus().post(new BluetoothDiscoveryCommand(false));
        //provider.getUiEventBus().post(new CyclicRefreshCommand(false));
        discoveryActive = false;
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
        final RecyclerView deviceListView = (RecyclerView) deviceView.findViewById(R.id.deviceList);

        // Layout for the list
        final int layoutColumnCount = 3;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), layoutColumnCount);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return sectionedAdapter.isSectionHeaderPosition(position) ? layoutColumnCount : 1;
            }
        });
        deviceListView.setLayoutManager(layoutManager);

        // List adapter
        listAdapter = new DeviceListAdapter(this.listener, this.provider);


        discoveryStatus = (TextView) deviceView.findViewById(R.id.discoveryStatusText);
        final Button discoveryButton = (Button) deviceView.findViewById(R.id.discoveryButton);
        discoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toggle = toggleDiscovery();
                if (toggle) {
                    discoveryButton.setText(getResources().getText(R.string.btn_pause_discovery));
                    updateStatusText();
                } else {
                    discoveryButton.setText(getResources().getText(R.string.btn_start_discovery));
                    updateStatusText();
                }
            }
        });

        sectionedAdapter = new SectionedDeviceListAdapter(this.getActivity(), R.layout.section, R.id.section_text, listAdapter);
        deviceListView.setAdapter(sectionedAdapter);

        return deviceView;
    }

    private void updateStatusText(){
        if (discoveryActive) {
            discoveryStatus.setText("Discovery active. "+listAdapter.size()+" found.");
        } else {
            discoveryStatus.setText("Discovery stopped. "+listAdapter.size()+" found.");
        }
    }

    public void onEventMainThread(DeviceLoaded msg){
        Map<String, Integer> deviceCategories = listAdapter.addOrUpdate(msg.getDevice());
        sectionedAdapter.updateSections(deviceCategories);
        updateStatusText();
    }

    public void onEventMainThread(DeviceChanged msg){
        Map<String, Integer> deviceCategories = listAdapter.addOrUpdate(msg.getDevice());
        sectionedAdapter.updateSections(deviceCategories);
    }

    @Override
    public void deviceClicked(Device d) {
        Log.d("Fluxron", "Requesting data from " + d.getAddress());
        provider.getUiEventBus().post(new BluetoothTestCommand(d.getAddress()));
    }

    @Override
    public void deviceButtonPressed(Device d) {
        // Add the device to the kitchen area !!!!
        if (addListener != null) {
            addListener.onDeviceAddRequested(d);
        }
    }

    public boolean toggleDiscovery(){
        provider.getUiEventBus().post(new BluetoothDiscoveryCommand(!discoveryActive));
        discoveryActive = !discoveryActive;

        return discoveryActive;
    }

    public void setListener(IDeviceAddedListener l) {
        this.addListener = l;
    }
}
