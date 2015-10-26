package ch.fluxron.fluxronapp.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private Bitmap image;

    public AreaHolder(View itemView, IEventBusProvider provider) {
        super(itemView);

        this.parent = itemView;
        this.img = (ImageView) itemView.findViewById(R.id.areaImage);
        this.provider = provider;
        this.card = (CardView) itemView.findViewById(R.id.areaCard);

        parent.setOnClickListener(this);
        card.setPreventCornerOverlap(false);
    }

    public void onEventMainThread(ImageLoaded msg){
        if(msg.getConnectionId().equals(imageRequestId) && msg.getBmp() != null){
            if (image !=null) image.recycle();
            image = msg.getBmp();
            img.setImageBitmap(image);

            // Image was loaded, no need to use the event bus anymore
            if (this.provider.getUiEventBus().isRegistered(this)) {
                this.provider.getUiEventBus().unregister(this);
            }
        }
    }

    /**
     * Binds the kitchen area
     * @param k Area
     */
    public void bind(final KitchenArea k){
        boundData = k;
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
        command.setImageSize(new Point(img.getWidth(), img.getHeight()));
        imageRequestId = command.getConnectionId();

        // only register for messages, when we actually expect them
        if (!this.provider.getUiEventBus().isRegistered(this)) {
            this.provider.getUiEventBus().register(this);
        }
        provider.getUiEventBus().post(command);
    }

    @Override
    public void onClick(View v) {
        /*if(listener != null){
            listener.kitchenClicked(boundData);
        }*/
    }
}
