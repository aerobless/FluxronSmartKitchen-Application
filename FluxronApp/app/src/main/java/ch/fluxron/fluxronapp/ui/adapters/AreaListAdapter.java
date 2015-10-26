package ch.fluxron.fluxronapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Adapter for the kitchen area list. Handles the views for each row.
 */
public class AreaListAdapter extends RecyclerView.Adapter<AreaHolder> {
    private List<KitchenArea> areas = new ArrayList<>();
    private HashMap<Integer, Integer> areaIds = new HashMap<>();
    private IEventBusProvider provider;

    /**
     * Adds a new area to the list. If the area already exists,
     * its cached values will be updated. Area equality is determined by using getRelativeId().
     * This means you should never mix areas of multiple kitchens inside the same adapter!
     * @param a Area to add or update.
     */
    public void addOrUpdate(KitchenArea a){
        if (areaIds.containsKey(a.getRelativeId()))
        {
            int position = areaIds.get(a.getRelativeId());
            notifyItemChanged(position);
        }
        else
        {
            int newPosition = areaIds.size();
            areas.add(a);
            areaIds.put(a.getRelativeId(), newPosition);
            notifyItemInserted(newPosition);
        }
    }

    /**
     * Sets the event bus
     * @param provider Event bus access
     */
    public AreaListAdapter(IEventBusProvider provider) {
        this.provider = provider;
    }

    /**
     * Removes all entries from the list
     */
    public void clear(){
        areas.clear();
        areaIds.clear();
        notifyDataSetChanged();
    }

    @Override
    public AreaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_item_area_card, parent, false);

        return new AreaHolder(itemView, this.provider);
    }

    @Override
    public void onBindViewHolder(AreaHolder holder, int position) {
        holder.bind(areas.get(position));
    }

    @Override
    public int getItemCount() {
        return areas.size();
    }
}