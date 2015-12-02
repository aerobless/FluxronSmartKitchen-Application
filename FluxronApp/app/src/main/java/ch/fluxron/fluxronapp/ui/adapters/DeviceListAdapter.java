package ch.fluxron.fluxronapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Adapter for the device search list. Handles the fragments for each row.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceHolder> {
    private List<Device> devices = new ArrayList<>();
    private HashMap<String, Integer> deviceIds = new HashMap<>();
    private IDeviceClickListener listener;
    private IEventBusProvider provider;

    /**
     * Adds a new device to the list. If the device already exists,
     * its cached values will be updated. Device equality is determined by using getAddress().
     *
     * @param d device to add or update.
     */
    public Map<String, Integer> addOrUpdate(Device d) {
        if (deviceIds.containsKey(d.getAddress())) {
            int position = deviceIds.get(d.getAddress());
            for (Device dev : devices) {
                if (dev.getAddress().equals(d.getAddress())) {
                    dev.setBonded(d.isBonded());
                }
            }
            notifyItemChanged(position);
        } else {
            int newPosition = devices.size();
            devices.add(d);
            deviceIds.put(d.getAddress(), newPosition);
            notifyItemInserted(newPosition);
        }
        return sortList();
    }

    /**
     * Sorts the list of elements
     *
     * @return Sorted map
     */
    public Map<String, Integer> sortList() {
        Map<String, Integer> deviceCategories = new LinkedHashMap<>();
        Collections.sort(devices, new Comparator<Device>() {
            @Override
            public int compare(Device dev1, Device dev2) {
                String cat1 = dev1.getDeviceType();
                String cat2 = dev2.getDeviceType();
                return cat1.compareTo(cat2);
            }
        });
        int i = 1;
        for (Device d : devices) {
            String cat = d.getDeviceType();
            Integer deviceCountPerCategory = deviceCategories.get(cat);
            if (deviceCountPerCategory == null) {
                deviceCategories.put(cat, 1);
            } else {
                deviceCategories.put(cat, deviceCountPerCategory + 1);
            }
            deviceIds.put(d.getAddress(), i);
            notifyItemChanged(i);
            i++;
        }
        return deviceCategories;
    }

    /**
     * Gets the number of devices
     *
     * @return Number of devices
     */
    public int size() {
        return devices.size();
    }

    /**
     * Sets the instance listening to device clicks
     *
     * @param listener Listener
     */
    public DeviceListAdapter(IDeviceClickListener listener, IEventBusProvider provider) {
        this.listener = listener;
        this.provider = provider;
    }

    /**
     * Removes all entries from the list
     */
    public void clear() {
        devices.clear();
        deviceIds.clear();
        notifyDataSetChanged();
    }

    /**
     * creates a view holder
     *
     * @param parent   Parent
     * @param viewType Type
     * @return Holder
     */
    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.listrow_device, parent, false);
        return new DeviceHolder(itemView, listener, this.provider);
    }

    /**
     * Binds a view holder
     *
     * @param holder   Holder
     * @param position Position
     */
    @Override
    public void onBindViewHolder(DeviceHolder holder, int position) {
        if (devices.size() > position) {
            holder.bind(devices.get(position));
        }
    }

    /**
     * Returns the item count
     *
     * @return Item count
     */
    @Override
    public int getItemCount() {
        return devices.size();
    }
}
