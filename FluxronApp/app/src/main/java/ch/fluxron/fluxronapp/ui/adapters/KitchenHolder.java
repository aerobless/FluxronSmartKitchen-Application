package ch.fluxron.fluxronapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Holds one kitchens view
 */
public class KitchenHolder extends RecyclerView.ViewHolder{
    TextView title;
    TextView description;

    public KitchenHolder(View itemView) {
        super(itemView);
        this.title = (TextView) itemView.findViewById(R.id.titleText);
        this.description = (TextView) itemView.findViewById(R.id.descriptionText);
    }

    public void bind(Kitchen k){
        title.setText(k.getName());
        description.setText(k.getDescription());
    }
}
