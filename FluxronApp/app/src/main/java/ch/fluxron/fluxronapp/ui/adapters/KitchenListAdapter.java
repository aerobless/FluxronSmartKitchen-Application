package ch.fluxron.fluxronapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Adapter for the kitchen search list. Handles the fragments for each row.
 */
public class KitchenListAdapter extends RecyclerView.Adapter<KitchenHolder> {
    private List<Kitchen> kitchens = new ArrayList<>();
    private HashMap<String, Integer> kitchenIds = new HashMap<>();

    /**
     * Adds a new kitchen to the list. If the kitchen already exists,
     * its cached values will be updated. Kitchen equality is determined by using getId().
     * @param k Kitchen to add or update.
     */
    public void addOrUpdate(Kitchen k){
        if (kitchenIds.containsKey(k.getId()))
        {
            int position = kitchenIds.get(k.getId()).intValue();
            notifyItemChanged(position);
        }
        else
        {
            int newPosition = kitchens.size();
            kitchens.add(k);
            kitchenIds.put(k.getId(), newPosition);
            notifyItemInserted(newPosition);
        }

        Log.d("FLUXRON.PROTOTYPE", " kitchen ");
    }

    /**
     * Removes all entries from the list
     */
    public void clear(){
        kitchens.clear();
        kitchenIds.clear();
        notifyDataSetChanged();
    }

    @Override
    public KitchenHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.listrow_kitchen, parent, false);
        Log.d("FLUXRON.PROTOTYPE", " holder created");
        return new KitchenHolder(itemView);
    }

    @Override
    public void onBindViewHolder(KitchenHolder holder, int position) {
        holder.bind(kitchens.get(position));
        Log.d("FLUXRON.PROTOTYPE", " holder bound " + position);
    }

    @Override
    public int getItemCount() {
        Log.d("FLUXRON.PROTOTYPE", " itemcount " + kitchens.size());
        return kitchens.size();
    }
}
