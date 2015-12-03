package ch.fluxron.fluxronapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Adapter for the kitchen search list. Handles the fragments for each row.
 */
public class KitchenListAdapter extends RecyclerView.Adapter<KitchenHolder> {
    private List<Kitchen> kitchens = new ArrayList<>();
    private HashMap<String, Integer> kitchenIds = new HashMap<>();
    private IKitchenClickListener listener;
    private IEventBusProvider provider;

    /**
     * Adds a new kitchen to the list. If the kitchen already exists,
     * its cached values will be updated. Kitchen equality is determined by using getId().
     * @param k Kitchen to add or update.
     */
    public void addOrUpdate(Kitchen k){
        if (kitchenIds.containsKey(k.getId()))
        {
            int position = kitchenIds.get(k.getId());
            notifyItemChanged(position);
        }
        else
        {
            int newPosition = kitchens.size();
            kitchens.add(k);
            kitchenIds.put(k.getId(), newPosition);
            notifyItemInserted(newPosition);
        }
    }

    /**
     * Sets the instance listening to kitchen clicks
     * @param listener Listener
     */
    public KitchenListAdapter(IKitchenClickListener listener, IEventBusProvider provider) {
        this.listener = listener;
        this.provider = provider;
    }

    /**
     * Removes all entries from the list
     */
    public void clear(){
        kitchens.clear();
        kitchenIds.clear();
        notifyDataSetChanged();
    }

    /**
     * View holder should be created
     * @param parent Parent layout group
     * @param viewType Type of the view (ignored)
     * @return View holder
     */
    @Override
    public KitchenHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.listrow_kitchen, parent, false);
        return new KitchenHolder(itemView, listener, this.provider);
    }

    /**
     * Viewholder should be bound to the specific item
     * @param holder Holder
     * @param position Position
     */
    @Override
    public void onBindViewHolder(KitchenHolder holder, int position) {
        holder.bind(kitchens.get(position));
    }

    /**
     * Gets the number of items in this list
     * @return Number of items
     */
    @Override
    public int getItemCount() {
        return kitchens.size();
    }
}
