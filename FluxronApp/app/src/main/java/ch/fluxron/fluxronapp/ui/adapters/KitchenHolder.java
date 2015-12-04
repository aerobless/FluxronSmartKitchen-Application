package ch.fluxron.fluxronapp.ui.adapters;

import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.ImageLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadImageFromKitchenCommand;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Holds one kitchens view
 */
public class KitchenHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView title;
    private TextView description;
    private TextView deviceCount;
    private View parent;
    private ImageView img;
    private String imageRequestId;
    private Kitchen boundData;
    private IKitchenClickListener listener;
    private IEventBusProvider provider;

    /**
     * New holder
     * @param itemView View
     * @param listener Listener
     * @param provider Provider for the bus
     */
    public KitchenHolder(View itemView, IKitchenClickListener listener, IEventBusProvider provider) {
        super(itemView);

        this.parent = itemView;
        this.title = (TextView) itemView.findViewById(R.id.titleText);
        this.img = (ImageView) itemView.findViewById(R.id.kitchenImage);
        this.description = (TextView) itemView.findViewById(R.id.descriptionText);
        this.deviceCount = (TextView)itemView.findViewById(R.id.deviceCount);

        this.listener = listener;
        this.provider = provider;
        this.provider.getUiEventBus().register(this);

        parent.setOnClickListener(this);

    }

    /**
     * Image was loaded, use it
     * @param msg Message
     */
    public void onEventMainThread(ImageLoaded msg){
        if(msg.getConnectionId().equals(imageRequestId) && msg.getBmp() != null){
            img.setImageBitmap(msg.getBmp());
        }
    }

    /**
     * Binds this instance to another kitchen
     * @param k Kitchen
     */
    public void bind(final Kitchen k){
        boundData = k;
        title.setText(k.getName());
        description.setText(k.getDescription());
        deviceCount.setText(String.format(deviceCount.getResources().getString(R.string.device_count), k.getDeviceCount()));

        parent.post(new Runnable() {
            @Override
            public void run() {
                loadImage(k);
            }
        });
    }

    /**
     * Loads an image from a kitchen
     */
    private void loadImage(Kitchen k) {
        LoadImageFromKitchenCommand command = new LoadImageFromKitchenCommand(k.getId(), "mainPicture");
        command.setImageSize(new Point(img.getWidth(), 0));
        imageRequestId = command.getConnectionId();
        provider.getUiEventBus().post(command);
    }

    /**
     * Click occurred, notify listeners
     * @param v View
     */
    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.kitchenClicked(boundData);
        }
    }
}
