package ch.fluxron.fluxronapp.ui.adapters;

import android.content.Context;
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
public class KitchenListAdapter extends BaseAdapter{
    private List<Kitchen> kitchens = new ArrayList<>();
    private HashMap<String, Integer> kitchenIds = new HashMap<>();
    private LayoutInflater inflater;

    /**
     * Creates a new KitchenListAdapter
     * @param context Application context this adapter is running in. Used to read the LayoutInflater.
     */
    public KitchenListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return kitchens.size();
    }

    @Override
    public Object getItem(int position) {
        return kitchens.get(position);
    }

    @Override
    public long getItemId(int position) {
        // Get item ID is not used by Android itself and
        // we have no use for a long-value as an id.
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.listrow_kitchen, null);

        TextView title = (TextView) vi.findViewById(R.id.titleText);
        TextView description = (TextView) vi.findViewById(R.id.descriptionText);

        title.setText(kitchens.get(position).getName());
        description.setText(kitchens.get(position).getDescription());

        return vi;
    }

    /**
     * Adds a new kitchen to the list. If the kitchen already exists,
     * its cached values will be updated. Kitchen equality is determined by using getId().
     * @param k Kitchen to add or update.
     */
    public void addOrUpdate(Kitchen k){
        if (kitchenIds.containsKey(k.getId()))
        {
            int position = kitchenIds.get(k.getId()).intValue();
        }
        else
        {
            int newPosition = kitchens.size();
            kitchens.add(k);
            kitchenIds.put(k.getId(), newPosition);
        }
        notifyDataSetChanged();
    }

    /**
     * Removes all entries from the list
     */
    public void clear(){
        kitchens.clear();
        kitchenIds.clear();
    }
}
