package ch.fluxron.fluxronapp.ui.adapters;

import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.ImageLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadImageFromKitchenCommand;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Holds one kitchens view
 */
public class AreaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private View parent;
    private ImageView img;
    private CardView card;
    private IEventBusProvider provider;
    private String imageRequestId;
    private KitchenArea boundData;
    private IAreaClickedListener listener;

    /**
     * New holder
     *
     * @param itemView Item view
     * @param listener Listener
     * @param provider Provider for the bus
     */
    public AreaHolder(View itemView, IAreaClickedListener listener, IEventBusProvider provider) {
        super(itemView);

        this.listener = listener;
        this.parent = itemView;
        this.img = (ImageView) itemView.findViewById(R.id.areaImage);
        this.provider = provider;
        this.card = (CardView) itemView.findViewById(R.id.areaCard);

        parent.setOnClickListener(this);
        card.setPreventCornerOverlap(false);
    }

    /**
     * Image was loaded, use it
     *
     * @param msg Message
     */
    public void onEventMainThread(ImageLoaded msg) {
        if (msg.getConnectionId().equals(imageRequestId) && msg.getBmp() != null) {
            img.setImageBitmap(msg.getBmp());

            // Image was loaded, no need to use the event bus anymore
            if (this.provider.getUiEventBus().isRegistered(this)) {
                this.provider.getUiEventBus().unregister(this);
            }
        }
    }

    /**
     * Binds the kitchen area
     *
     * @param k Area
     */
    public void bind(final KitchenArea k) {
        boundData = k;
        img.setImageBitmap(null);
        parent.post(new Runnable() {
            @Override
            public void run() {
                loadImage(k);
            }
        });
    }

    /**
     * Loads an image from a kitchen area
     */
    private void loadImage(KitchenArea k) {
        LoadImageFromKitchenCommand command = new LoadImageFromKitchenCommand(k.getKitchenId(), k.getImageName());
        command.setImageSize(new Point(0, img.getHeight())); // 0 for dynamic width
        imageRequestId = command.getConnectionId();

        // only register for messages, when we actually expect them
        if (!this.provider.getUiEventBus().isRegistered(this)) {
            this.provider.getUiEventBus().register(this);
        }
        provider.getUiEventBus().post(command);
    }

    /**
     * Clicked, notify the listener
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.areaClicked(boundData);
        }
    }
}
