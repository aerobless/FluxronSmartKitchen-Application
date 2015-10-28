package ch.fluxron.fluxronapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;

import ch.fluxron.fluxronapp.events.base.ITypedCallback;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.AttachFileToObjectById;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.DeleteObjectById;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetFileStreamFromAttachment;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.IStreamProvider;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.ImageLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.AttachImageToKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.CreateKitchenAreaCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadImageFromKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.SaveKitchenCommand;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;

/**
 * Manages all messages related to kitchens on a domain level
 */
public class KitchenManager {
    private IEventBusProvider provider;

    /**
     * Sets the event bus this manager should be operating on
     * @param provider Event Bus Provider
     */
    public KitchenManager(IEventBusProvider provider) {
        this.provider = provider;
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
    }

    /**
     * Triggered when the save of a kitchen is requested
     * @param msg Message containing kitchen data
     */
    public void onEventAsync(SaveKitchenCommand msg) {
        SaveObjectCommand cmd = new SaveObjectCommand();
        cmd.setData(msg.getKitchen());
        cmd.setDocumentId(msg.getKitchen().getId());
        cmd.setConnectionId(msg);
        provider.getDalEventBus().post(cmd);
    }

    /**
     * Attaches an image to a kitchen if triggered
     * @param msg Message containing the image path
     */
    public void onEventAsync(AttachImageToKitchenCommand msg) {
        AttachFileToObjectById cmd = new AttachFileToObjectById(msg.getId(), msg.getImagePath(), "mainPicture");
        cmd.setConnectionId(msg);
        provider.getDalEventBus().post(cmd);
    }

    /**
     * Triggered when an image should be loaded
     * @param msg Message containing the loading request
     */
    public void onEventAsync(final LoadImageFromKitchenCommand msg) {
        GetFileStreamFromAttachment cmd = new GetFileStreamFromAttachment(msg.getKitchenId(), msg.getImageName(), new ITypedCallback<IStreamProvider>() {
            @Override
            public void call(IStreamProvider streamProvider) {
                Bitmap bmp = getFittingBitmap(streamProvider, msg.getImageSize());
                ImageLoaded event = new ImageLoaded(bmp);
                event.setConnectionId(msg);
                provider.getUiEventBus().post(event);
            }
        });
        provider.getDalEventBus().post(cmd);
    }

    /**
     * Loads a bitmap that fits inside the specified size. If the size is null, the bitmap will be
     * loaded in its full size.
     * @param stream Stream to load the bitmap from
     * @param imageSize Size to fit the bitmap in (or null if no fitting is requested)
     * @return Fitting bitmap
     */
    private Bitmap getFittingBitmap(IStreamProvider stream, Point imageSize) {
        if (imageSize == null) {
            return BitmapFactory.decodeStream(stream.openStream());
        }
        else {
            // Check the dimensions of the image
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream.openStream(), null, options);

            // Calculate the sample size for the requested size
            options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, imageSize.x, imageSize.y);

            // Decode with the new sample size
            options.inJustDecodeBounds = false;

            Bitmap loadedImage = BitmapFactory.decodeStream(stream.openStream(), new Rect(0, 0, 0, 0), options);

            // Calculate the bounds of the rescaled (proportional to ascpect ratio) image
            float aspect = (float)loadedImage.getWidth() / (float)loadedImage.getHeight();
            float newHeight = 1;
            float newWidth = 1;
            if (imageSize.x < imageSize.y && imageSize.x > 0) {
                newWidth = imageSize.x;
                newHeight = newWidth / aspect;
            }
            else if (imageSize.y > 0)  {
                newHeight = imageSize.y;
                newWidth = newHeight * aspect;
            }

            // Rescale to the newly calculated fitting bounds
            Bitmap result = Bitmap.createScaledBitmap(loadedImage, (int)newWidth, (int)newHeight, false);

            // Free the space used by the loadedImage
            loadedImage.recycle();

            return result;
        }
    }

    /**
     * Calculates the biggest sample size that makes sure the image uses minumum space while not
     * getting smaller than requestedWidth and requestedHeight
     * @param width Width of the image
     * @param height Height of the image
     * @param requestedWidth Minimum width that is requested
     * @param requestedHeight Minimum height that is requested
     * @return Sample size (power of two)
     */
    private int calculateSampleSize(int width, int height, int requestedWidth, int requestedHeight) {
       int sampleSize = 1;

        if (height > requestedHeight || width > requestedWidth) {
             final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Next power of 2, height and width should be larger than the requested size
            while ((halfHeight / sampleSize) > requestedHeight
                    && (halfWidth / sampleSize) > requestedWidth) {
                sampleSize *= 2;
            }
        }

        return sampleSize;
    }

    /**
     * Triggered when a kitchen search by name should be performed
     * @param msg Message containing the search query
     */
    public void onEventAsync(FindKitchenCommand msg) {
        ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand cmd = new ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand(msg.getQuery());
        cmd.setConnectionId(msg);

        provider.getDalEventBus().post(cmd);
    }

    /**
     * Triggered when a kitchen should be loaded
     * @param cmd Request containing the kitchen id
     */
    public void onEventAsync(LoadKitchenCommand cmd){
        LoadObjectByIdCommand dbCommand = new LoadObjectByIdCommand(cmd.getId());
        dbCommand.setConnectionId(cmd);
        provider.getDalEventBus().post(dbCommand);
    }

    /**
     * Triggered when a kitchen should be deleted
     * @param msg Message containing the kitchen id
     */
    public void onEventAsync(DeleteKitchenCommand msg) {
        DeleteObjectById delete = new DeleteObjectById(msg.getId());
        delete.setConnectionId(msg);
        provider.getDalEventBus().post(delete);
    }

    /**
     * Triggered when a new image should be added as a kitchen area
     * @param msg Message containing kitchen id and image path
     */
    public void onEventAsync(final CreateKitchenAreaCommand msg) {
        // Load the kitchen and create the area
        GetObjectByIdCommand getOp = new GetObjectByIdCommand(msg.getId(), new ITypedCallback<Object>() {
            @Override
            public void call(Object value) {
                if (value != null && value instanceof Kitchen) {
                    // Attach the area to the kitchen
                    addAreaToKitchen((Kitchen) value, msg.getImagePath());
                }
            }
        });
        this.provider.getDalEventBus().post(getOp);
    }

    /**
     * Adds a new kitchen area to a kitchen
     * @param k Kitchen
     * @param imageName Name of the image to attach
     */
    public void addAreaToKitchen(Kitchen k, Uri imageName){
        // Determine new relative id for this object as
        // max(relativeId) + 1
        int maxId = 0;
        for(KitchenArea area : k.getAreaList()){
            maxId = Math.max(area.getRelativeId(), maxId);
        }

        // Create the area and add it to the kitchen
        String storedImageName = "a_" + (maxId+1);
        KitchenArea a = new KitchenArea(storedImageName, k.getId(), maxId+1);
        k.getAreaList().add(a);

        // Save the kitchen
        SaveObjectCommand saveCommand = new SaveObjectCommand();
        saveCommand.setDocumentId(k.getId());
        saveCommand.setData(k);
        this.provider.getDalEventBus().post(saveCommand);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Attach the image to the kitchen
        AttachFileToObjectById attachCommand = new AttachFileToObjectById(k.getId(), imageName, storedImageName);
        this.provider.getDalEventBus().post(attachCommand);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Fire a kitchen changed message to the UI
        KitchenLoaded change = new KitchenLoaded(k);
        this.provider.getUiEventBus().post(change);
    }

    /**
     * An object was loaded. If it was a kitchen, prepare it and send it to the ui bus
     * @param msg Message
     */
    public void onEventAsync(ObjectLoaded msg) {
        if (msg.getData() instanceof Kitchen) {
            KitchenLoaded event = new KitchenLoaded((Kitchen) msg.getData());

            for(KitchenArea a : event.getKitchen().getAreaList()){
                a.setKitchenId(event.getKitchen().getId());
            }

            event.setConnectionId(msg);
            provider.getUiEventBus().post(event);
        }
    }
}
