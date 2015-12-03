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
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.BluetoothBondingCommand;
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
public class DeviceListFragment extends Fragment implements IDeviceClickListener {
    /**
     * Listener for device lists
     */
    public interface IDeviceAddedListener {
        /**
         * The user requested the addition of a device
         * @param d Device
         */
        void onDeviceAddRequested(Device d);
    }

    private IEventBusProvider provider;
    private DeviceListAdapter listAdapter;
    private SectionedDeviceListAdapter sectionedAdapter;
    private IDeviceClickListener listener;
    private boolean discoveryActive = true;
    private TextView discoveryStatus;
    private IDeviceAddedListener addListener;
    private Button discoveryButton;

    /**
     * Sets the listener for device clicks
     * @param listener Listener
     */
    public void setClickListener(IDeviceClickListener listener) {
        this.listener = listener;
    }

    /**
     * Fragment was started
     */
    @Override
    public void onStart() {
        super.onStart();
        provider.getUiEventBus().register(this);
        provider.getUiEventBus().post(new BluetoothDiscoveryCommand(true));
        provider.getUiEventBus().post(new CyclicRefreshCommand(CyclicRefreshCommand.ALL_DEVICES));
        discoveryActive = true;
    }

    /**
     * Fragment was stopped
     */
    @Override
    public void onStop() {
        super.onStop();
        provider.getUiEventBus().post(new BluetoothDiscoveryCommand(false));
        provider.getUiEventBus().post(new CyclicRefreshCommand(CyclicRefreshCommand.NONE));
        discoveryActive = false;
        provider.getUiEventBus().unregister(this);
    }

    /**
     * Saves the state of this fragment
     * @param outState State
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Creates the view for this fragment
     * @param inflater Inflater
     * @param container Container
     * @param savedInstanceState State
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_device_list, container, false);
        this.provider = (IEventBusProvider)getActivity().getApplicationContext();
        setClickListener(this);

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
        discoveryButton = (Button) deviceView.findViewById(R.id.discoveryButton);
        discoveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDiscovery();
            }
        });

        sectionedAdapter = new SectionedDeviceListAdapter(this.getActivity(), R.layout.section, R.id.section_text, listAdapter);
        deviceListView.setAdapter(sectionedAdapter);

        return deviceView;
    }

    /**
     * Toggles the discovery
     */
    public void toggleDiscovery() {
        setDiscoveryActive(!discoveryActive);
    }

    /**
     * Sets the discovery state
     * @param value Active
     */
    private void setDiscoveryActive(boolean value) {
        discoveryActive = value;
        provider.getUiEventBus().post(new BluetoothDiscoveryCommand(value));
        updateStatusText();
    }

    /**
     * Updates the status text
     */
    // TODO: Use resources for all texts
    private void updateStatusText() {
        if (discoveryActive) {
            discoveryButton.setText(getResources().getText(R.string.btn_pause_discovery));
            discoveryStatus.setText("Discovery active. " + listAdapter.size() + " found.");
        } else {
            discoveryButton.setText(getResources().getText(R.string.btn_start_discovery));
            discoveryStatus.setText("Discovery stopped. " + listAdapter.size() + " found.");
        }
    }

    /**
     * Device was loaded
     * @param msg Message
     */
    public void onEventMainThread(DeviceLoaded msg) {
        Map<String, Integer> deviceCategories = listAdapter.addOrUpdate(msg.getDevice());
        sectionedAdapter.updateSections(deviceCategories);
        updateStatusText();
    }

    /**
     * Device values or state changed
     * @param msg Message
     */
    public void onEventMainThread(DeviceChanged msg) {
        Map<String, Integer> deviceCategories = listAdapter.addOrUpdate(msg.getDevice());
        sectionedAdapter.updateSections(deviceCategories);
    }

    /**
     * Device was clicked
     * @param d Device
     */
    @Override
    public void deviceClicked(Device d) {
        Log.d("Fluxron", "Requesting data from " + d.getAddress());
        provider.getUiEventBus().post(new BluetoothTestCommand(d.getAddress()));
    }

    /**
     * The add / pair button of a device was clicked
     * @param d Device that should be paired or added
     */
    @Override
    public void deviceButtonPressed(Device d) {
        if (d.isBonded()) {
            // Add the device to the kitchen area
            if (addListener != null) {
                addListener.onDeviceAddRequested(d);
            }
        } else {
            //Request that the device is bonded first
            provider.getUiEventBus().post(new CyclicRefreshCommand(CyclicRefreshCommand.NONE)); //Disable CyclicRefresh
            setDiscoveryActive(false); //Disable Discovery
            provider.getUiEventBus().post(new BluetoothBondingCommand(d.getAddress()));
        }
    }

    /**
     * Sets the listener for device add events
     * @param l Listener
     */
    public void setListener(IDeviceAddedListener l) {
        this.addListener = l;
    }
}
