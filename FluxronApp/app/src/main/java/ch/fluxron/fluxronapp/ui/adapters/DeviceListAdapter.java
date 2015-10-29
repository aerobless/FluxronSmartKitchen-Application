package ch.fluxron.fluxronapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.model.Device;
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
     * @param d device to add or update.
     */
    public void addOrUpdate(Device d){
        if (deviceIds.containsKey(d.getAddress()))
        {
            int position = deviceIds.get(d.getAddress());
            notifyItemChanged(position);
        }
        else
        {
            int newPosition = devices.size();
            devices.add(d);
            deviceIds.put(d.getAddress(), newPosition);
            notifyItemInserted(newPosition);
        }
    }

    /**
     * Sets the instance listening to device clicks
     * @param listener Listener
     */
    public DeviceListAdapter(IDeviceClickListener listener, IEventBusProvider provider) {
        this.listener = listener;
        this.provider = provider;
    }

    /**
     * Removes all entries from the list
     */
    public void clear(){
        devices.clear();
        deviceIds.clear();
        notifyDataSetChanged();
    }

    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.listrow_device, parent, false);
        return new DeviceHolder(itemView, listener, this.provider);
    }

    @Override
    public void onBindViewHolder(DeviceHolder holder, int position) {
        holder.bind(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
