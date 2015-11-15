package ch.fluxron.fluxronapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;

import ch.fluxron.fluxronapp.events.base.EventContinuation;
import ch.fluxron.fluxronapp.events.base.ITypedCallback;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.base.ResponseOK;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.AttachFileToObjectById;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.DeleteObjectById;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetFileStreamFromAttachment;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.IStreamProvider;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.ImageLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.AddDeviceToAreaCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.AttachImageToKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.ChangeDevicePosition;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.ChangeKitchenSettings;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.CreateKitchenAreaCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteDeviceFromArea;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeviceDeletedFromArea;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DevicePositionChanged;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenAreaLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadImageFromKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenArea;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.SaveKitchenCommand;
import ch.fluxron.fluxronapp.objectBase.DevicePosition;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;

/**
 * Manages all messages related to kitchens on a domain level
 */
public class KitchenManager {
    private IEventBusProvider provider;
    private BitmapCache kitchenImageCache;

    /**
     * Sets the event bus this manager should be operating on
     * @param provider Event Bus Provider
     */
    public KitchenManager(IEventBusProvider provider) {
        this.provider = provider;
        this.kitchenImageCache = new BitmapCache(1024*20); // 20 MBytes

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
        // Check if the image is in the cache and return instantly if needed
        String tmpCacheId = msg.getKitchenId() + "/" + msg.getImageName();

        if (msg.getImageSize() == null) {
            tmpCacheId += "(full)";
        }else{
            tmpCacheId += "(" + msg.getImageSize().x + "," + msg.getImageSize().y + ")";
        }
        final String cacheId = tmpCacheId;

        Bitmap cachedEntry = kitchenImageCache.get(cacheId);
        if(cachedEntry != null){
            fireImageLoaded(cachedEntry, msg);
            return;
        }

        GetFileStreamFromAttachment cmd = new GetFileStreamFromAttachment(msg.getKitchenId(), msg.getImageName(), new ITypedCallback<IStreamProvider>() {
            @Override
            public void call(IStreamProvider streamProvider) {
                Bitmap bmp = getFittingBitmap(streamProvider, msg.getImageSize());
                kitchenImageCache.put(cacheId, bmp);
                fireImageLoaded(bmp, msg);
            }
        });
        provider.getDalEventBus().post(cmd);
    }

    /**
     * Sends a message to the UI containing a loaded bitmap
     * @param bmp Bitmap that was loaded
     * @param msg Message that triggered the load
     */
    private void fireImageLoaded(Bitmap bmp, RequestResponseConnection msg) {
        ImageLoaded event = new ImageLoaded(bmp);
        event.setConnectionId(msg);
        provider.getUiEventBus().post(event);
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
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeStream(stream.openStream(), new Rect(0,0,0,0), options);
        }
        else {
            // Check the dimensions of the image
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
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
            if (imageSize.x > 0 && imageSize.y > 0) {
                if (imageSize.x < imageSize.y) {
                    newWidth = imageSize.x;
                    newHeight = newWidth / aspect;
                }
                else {
                    newHeight = imageSize.y;
                    newWidth = newHeight * aspect;
                }
            }
            else if (imageSize.x > 0) {
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
    public void addAreaToKitchen(Kitchen k, final Uri imageName){
        final String kitchenId = k.getId();

        // Determine new relative id for this object as
        // max(relativeId) + 1
        int maxId = 0;
        for(KitchenArea area : k.getAreaList()){
            maxId = Math.max(area.getRelativeId(), maxId);
        }

        // Create the area and add it to the kitchen
        final String storedImageName = "a_" + (maxId+1);
        KitchenArea a = new KitchenArea(storedImageName, kitchenId, maxId+1);
        k.getAreaList().add(a);

        // Command for saving the kitchen
        final SaveObjectCommand saveCommand = new SaveObjectCommand();
        saveCommand.setDocumentId(k.getId());
        saveCommand.setData(k);

        // Command for file attachment
        final AttachFileToObjectById attachCommand = new AttachFileToObjectById(kitchenId, imageName, storedImageName);

        // Command for notifying the UI
        final KitchenLoaded change = new KitchenLoaded(k);

        // Continue by attaching an Image
        EventContinuation.createEventChain(provider.getDalEventBus(), saveCommand, ResponseOK.class, new ITypedCallback<ResponseOK>() {
            @Override
            public void call(ResponseOK value) {
                KitchenManager.this.provider.getDalEventBus().post(attachCommand);
            }
        });

        // Continue by notifying the ui bus about the newly created object
        EventContinuation.createEventChain(provider.getDalEventBus(), attachCommand, ResponseOK.class, new ITypedCallback<ResponseOK>() {
            @Override
            public void call(ResponseOK value) {
                KitchenManager.this.provider.getUiEventBus().post(change);
            }
        });

        // Start the message chain
        this.provider.getDalEventBus().post(saveCommand);
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

    /**
     * Triggered when a device should be added to an area
     * @param msg command
     */
    public void onEventAsync(final AddDeviceToAreaCommand msg){
        // Load the kitchen and attach the device to the area
        GetObjectByIdCommand getOp = new GetObjectByIdCommand(msg.getKitchenArea().getKitchenId(), new ITypedCallback<Object>() {
            @Override
            public void call(Object value) {
                if (value != null && value instanceof Kitchen) {
                    // Attach the device to the area
                    addDeviceToArea((Kitchen) value, msg);
                }
            }
        });
        this.provider.getDalEventBus().post(getOp);
    }


    /**
     * Triggered when a device should be added to an area
     * @param msg command
     */
    public void onEventAsync(final ChangeDevicePosition msg){
        // Load the kitchen and attach the device to the area
        GetObjectByIdCommand getOp = new GetObjectByIdCommand(msg.getKitchenId(), new ITypedCallback<Object>() {
            @Override
            public void call(Object value) {
                if (value != null && value instanceof Kitchen) {
                    // Attach the device to the area
                    moveDevicePosition((Kitchen) value, msg);
                }
            }
        });
        this.provider.getDalEventBus().post(getOp);
    }

    /**
     * Triggered when a kitchens settings should be changed
     * @param msg command
     */
    public void onEventAsync(final ChangeKitchenSettings msg){
        // Load the kitchen and attach the device to the area
        GetObjectByIdCommand getOp = new GetObjectByIdCommand(msg.getKitchenId(), new ITypedCallback<Object>() {
            @Override
            public void call(Object value) {
                if (value != null && value instanceof Kitchen) {
                    // Attach the device to the area
                    changeKitchenSettings((Kitchen) value, msg);
                }
            }
        });
        this.provider.getDalEventBus().post(getOp);
    }

    /**
     * Triggered when a device should be deleted
     * @param msg command
     */
    public void onEventAsync(final DeleteDeviceFromArea msg){
        // Load the kitchen and attach the device to the area
        GetObjectByIdCommand getOp = new GetObjectByIdCommand(msg.getKitchenId(), new ITypedCallback<Object>() {
            @Override
            public void call(Object value) {
                if (value != null && value instanceof Kitchen) {
                    deleteDevice((Kitchen) value, msg);
                }
            }
        });
        this.provider.getDalEventBus().post(getOp);
    }

    private void deleteDevice(Kitchen kitchen, DeleteDeviceFromArea msg) {
        // Find the area
        KitchenArea foundArea = null;
        for(KitchenArea a : kitchen.getAreaList()){
            if (a.getRelativeId() == msg.getAreaId()) {
                foundArea = a;
                break;
            }
        }

        // Find the position
        if(foundArea!=null) {
            DevicePosition foundPosition = null;

            for (DevicePosition pos : foundArea.getDevicePositionList()) {
                if (pos.getDeviceId().equals(msg.getDeviceId())) {
                    foundPosition = pos;
                    break;
                }
            }

            // Remove
            foundArea.getDevicePositionList().remove(foundPosition);

            // Save the kitchen
            final SaveObjectCommand saveCommand = new SaveObjectCommand();
            saveCommand.setDocumentId(kitchen.getId());
            saveCommand.setData(kitchen);
            provider.getDalEventBus().post(saveCommand);

            // Notify the deletion
            DeviceDeletedFromArea event = new DeviceDeletedFromArea(msg.getKitchenId(), msg.getAreaId(), msg.getDeviceId());
            provider.getUiEventBus().post(event);
        }
    }

    /**
     * Changes the kitchen settings as requested in the message
     * @param value kitchen
     * @param msg message
     */
    private void changeKitchenSettings(Kitchen value, ChangeKitchenSettings msg) {
        value.setName(msg.getKitchenName());
        value.setDescription(msg.getKitchenDescription());

        // Save the kitchen
        final SaveObjectCommand saveCommand = new SaveObjectCommand();
        saveCommand.setDocumentId(value.getId());
        saveCommand.setData(value);
        provider.getDalEventBus().post(saveCommand);
    }

    /**
     * Changes the device position according to the message
     * @param msg Message
     */
    private void moveDevicePosition(Kitchen kitchen, ChangeDevicePosition msg) {
        // Find the area
        KitchenArea foundArea = null;
        for(KitchenArea a : kitchen.getAreaList()){
            if (a.getRelativeId() == msg.getAreaId()) {
                foundArea = a;
                break;
            }
        }

        if(foundArea!=null) {
            DevicePosition foundPosition = null;

            for(DevicePosition pos : foundArea.getDevicePositionList()){
                if (pos.getDeviceId().equals(msg.getDeviceId())) {
                    foundPosition = pos;
                    break;
                }
            }

            if (foundPosition != null){
                foundPosition.setPosition(new Point(msg.getPos()));

                // Save the kitchen
                final SaveObjectCommand saveCommand = new SaveObjectCommand();
                saveCommand.setDocumentId(kitchen.getId());
                saveCommand.setData(kitchen);
                provider.getDalEventBus().post(saveCommand);

                // Notify the change of position
                DevicePositionChanged event = new DevicePositionChanged(kitchen.getId(), foundArea.getRelativeId(), foundPosition);
                event.setConnectionId(msg);
                provider.getUiEventBus().post(event);
            }
        }
    }

    /**
     * Triggered when an area should be loaded
     * @param msg command
     */
    public void onEventAsync(final LoadKitchenArea msg){
        // Load the kitchen and attach the device to the area
        GetObjectByIdCommand getOp = new GetObjectByIdCommand(msg.getKitchenId(), new ITypedCallback<Object>() {
            @Override
            public void call(Object value) {
                if (value != null && value instanceof Kitchen) {
                    loadArea((Kitchen) value, msg);
                }
            }
        });
        this.provider.getDalEventBus().post(getOp);
    }

    private void loadArea(Kitchen kitchen, LoadKitchenArea msg) {
        // Find the area
        KitchenArea found = null;
        for(KitchenArea a : kitchen.getAreaList()){
            if (a.getRelativeId() == msg.getRelativeId()) {
                found = a;
                break;
            }
        }

        if (found != null) {
            KitchenAreaLoaded event = new KitchenAreaLoaded(found);
            found.setKitchenId(msg.getKitchenId());
            event.setConnectionId(msg);
            this.provider.getUiEventBus().post(event);
        }
    }

    /**
     * Adds a device to a loaded kitchen
     * @param kitchen Kitchen to use
     * @param msg Message containing the area and the device
     */
    private void addDeviceToArea(Kitchen kitchen, AddDeviceToAreaCommand msg) {
        // Find the area
        KitchenArea found = null;
        for(KitchenArea a : kitchen.getAreaList()){
            if (a.getRelativeId() == msg.getKitchenArea().getRelativeId()) {
                found = a;
                break;
            }
        }

        if(found!=null) {

            // Only add the device if it does not exist
            boolean exists = false;
            for(DevicePosition p : found.getDevicePositionList()) {
                if (p.getDeviceId().equals(msg.getDevice().getAddress())){
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                DevicePosition pos = new DevicePosition();
                pos.setPosition(new Point(100, 100));
                pos.setDeviceId(msg.getDevice().getAddress());
                pos.setCategory(msg.getDevice().getDeviceType());
                pos.setName(msg.getDevice().getName());
                found.getDevicePositionList().add(pos);

                // Save the kitchen
                final SaveObjectCommand saveCommand = new SaveObjectCommand();
                saveCommand.setDocumentId(kitchen.getId());
                saveCommand.setData(kitchen);
                provider.getDalEventBus().post(saveCommand);

                // Notify the change of position
                DevicePositionChanged event = new DevicePositionChanged(kitchen.getId(), found.getRelativeId(), pos);
                event.setConnectionId(msg);
                provider.getUiEventBus().post(event);
            }
        }
    }
}
