package ch.fluxron.fluxronapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Holds one kitchens view
 */
public class KitchenHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView title;
    TextView description;
    View parent;
    Kitchen boundData;
    IKitchenClickListener listener;

    public KitchenHolder(View itemView, IKitchenClickListener listener) {
        super(itemView);

        this.parent = itemView;
        this.title = (TextView) itemView.findViewById(R.id.titleText);
        this.description = (TextView) itemView.findViewById(R.id.descriptionText);
        this.listener = listener;

        parent.setOnClickListener(this);
    }

    public void bind(Kitchen k){
        boundData = k;
        title.setText(k.getName());
        description.setText(k.getDescription());
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.kitchenClicked(boundData);
        }
    }
}
