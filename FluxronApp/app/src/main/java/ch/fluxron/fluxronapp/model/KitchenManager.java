package ch.fluxron.fluxronapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.base.EventContinuation;
import ch.fluxron.fluxronapp.events.base.ITypedCallback;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.base.ResponseOK;
import ch.fluxron.fluxronapp.events.base.WaitForResponse;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.AttachFileToObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.DeleteObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetAllAttachmentStreamsFromObjectCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetFileStreamFromAttachmentCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.IStreamProvider;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.ImageLoaded;
import ch.fluxron.fluxronapp.events.modelUi.ValidationErrorOccurred;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.AddDeviceToAreaCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.AttachImageToKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.ChangeDevicePositionCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.ChangeKitchenSettingsCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.CreateKitchenAreaCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteAreaFromKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteDeviceFromAreaCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeviceFromAreaDeleted;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DevicePositionChanged;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenAreaLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadImageFromKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenAreaCommand;
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
    private final static String ATTACHMENT_NAME_MAIN_PICTURE = "mainPicture";

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

        List<ValidationErrorOccurred> errors = validateKitchen(msg.getKitchen());

        if (errors.size() == 0) {
            SaveObjectCommand cmd = new SaveObjectCommand();
            cmd.setData(msg.getKitchen());
            cmd.setDocumentId(msg.getKitchen().getId());
            cmd.setConnectionId(msg);
            provider.getDalEventBus().post(cmd);
        }
        else
        {
            raiseErrors(msg, errors);
        }
    }

    /**
     * Posts all error message to the UI bus
     * @param msg Original message
     * @param errors Error messages
     */
    private void raiseErrors(RequestResponseConnection msg, List<ValidationErrorOccurred> errors) {
        for (ValidationErrorOccurred error : errors) {
            error.setConnectionId(msg);
            provider.getUiEventBus().post(error);
        }
    }

    /**
     * Validates the properties of a kitchen and returns a list of errors if any were found
     * @param kitchen Kitchen to be validated
     * @return List of errors. If this list is empty, the validation succeeded
     */
    private List<ValidationErrorOccurred> validateKitchen(Kitchen kitchen) {
        ArrayList<ValidationErrorOccurred> errors = new ArrayList<>();

        if (kitchen.getName() == null || kitchen.getName().trim().equals("")) {
            errors.add(new ValidationErrorOccurred(R.string.validation_error_no_name));
        }

        return errors;
    }

    /**
     * Attaches an image to a kitchen if triggered
     * @param msg Message containing the image path
     */
    public void onEventAsync(AttachImageToKitchenCommand msg) {
        List<ValidationErrorOccurred> errors = validateAttachment(msg.getImagePath());

        if (errors.size() == 0) {
            AttachFileToObjectByIdCommand cmd = new AttachFileToObjectByIdCommand(msg.getId(), msg.getImagePath(), ATTACHMENT_NAME_MAIN_PICTURE);

            WaitForResponse<RequestResponseConnection> waiter = new WaitForResponse<>();
            RequestResponseConnection response = waiter.postAndWait(provider.getDalEventBus(), cmd, RequestResponseConnection.class);

            if (response instanceof ResponseOK) {
                ResponseOK attachmentOk = new ResponseOK();
                attachmentOk.setConnectionId(msg);
                provider.getUiEventBus().post(attachmentOk);
            }
        }
        else {
            raiseErrors(msg, errors);

            // Load the attachments, if the mainPicture is not found, remove the kitchen
            final String kitchenId = msg.getId();
            GetAllAttachmentStreamsFromObjectCommand loadStreams = new GetAllAttachmentStreamsFromObjectCommand(kitchenId, new ITypedCallback<Map<String, InputStream>>() {
                @Override
                public void call(Map<String, InputStream> streams){
                    // Close all streams, we don't need them
                    for(InputStream s : streams.values()){
                        try {s.close();}
                        catch (IOException e) { e.printStackTrace(); }
                    }
                    // If there were no streams, we need to remove the kitchen since it is invalid
                    if (streams.size() == 0) {
                        DeleteObjectByIdCommand command = new DeleteObjectByIdCommand(kitchenId);
                        provider.getDalEventBus().post(command);
                    }
                }
            });
            provider.getDalEventBus().post(loadStreams);
        }
    }

    /**
     * Validates an attachments
     * @param imagePath Path to the image
     * @return Validation errors, if any were found
     */
    private List<ValidationErrorOccurred> validateAttachment(Uri imagePath) {
        List<ValidationErrorOccurred> errors = new ArrayList<>();
        if (imagePath == null || imagePath.getPath().trim().equals("")) {
            errors.add(new ValidationErrorOccurred(R.string.validation_error_no_image));
        }else {
            File testFile = new File(imagePath.getPath());
            if (!testFile.exists()) {
                errors.add(new ValidationErrorOccurred(R.string.validation_error_no_image));
            }
        }
        return errors;
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

        GetFileStreamFromAttachmentCommand cmd = new GetFileStreamFromAttachmentCommand(msg.getKitchenId(), msg.getImageName(), new ITypedCallback<IStreamProvider>() {
            @Override
            public void call(IStreamProvider streamProvider) {
                Bitmap bmp = getFittingBitmap(streamProvider, msg.getImageSize(), msg.getMode());
                kitchenImageCache.put(cacheId, bmp);
                fireImageLoaded(bmp, msg);
            }
        });
        provider.getDalEventBus().post(cmd);
    }

    /**
     * Requests the deletion of a kitchen area
     * @param msg Command
     */
    public void onEventAsync(final DeleteAreaFromKitchenCommand msg) {
        // Load the kitchen and delete the area
        GetObjectByIdCommand getOp = new GetObjectByIdCommand(msg.getKitchenId(), new ITypedCallback<Object>() {
            @Override
            public void call(Object value) {
                if (value != null && value instanceof Kitchen) {
                    // Attach the area to the kitchen
                    removeAreaFromKitchen((Kitchen) value, msg);
                }
            }
        });
        provider.getDalEventBus().post(getOp);
    }

    /**
     * Removes an area from the kitchen
     * @param kitchen Kitchen
     * @param cmd Command
     */
    private void removeAreaFromKitchen(Kitchen kitchen, DeleteAreaFromKitchenCommand cmd) {
        List<KitchenArea> areas = kitchen.getAreaList();

        for(int i =0; i< areas.size(); i++ )
        {
            if (areas.get(i).getRelativeId() == cmd.getAreaId()) {
                areas.remove(i);
                break;
            }
        }

        SaveObjectCommand save = new SaveObjectCommand();
        save.setData(kitchen);
        save.setDocumentId(cmd.getKitchenId());

        // Wait until the save is done
        new WaitForResponse<>().postAndWait(provider.getDalEventBus(), save, RequestResponseConnection.class);

        // Issue a new kitchen load
        LoadObjectByIdCommand loadCommand = new LoadObjectByIdCommand(cmd.getKitchenId());
        loadCommand.setConnectionId(cmd);
        provider.getDalEventBus().post(loadCommand);
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
     * @param mode Describes if the image should be shrunk to fit into the bounds or if it should be
     *             cropped to match the dimensions
     * @return Fitting bitmap
     */
    private Bitmap getFittingBitmap(IStreamProvider stream, Point imageSize, LoadImageFromKitchenCommand.ImageFittingMode mode) {
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

            Bitmap result;
            if (mode == LoadImageFromKitchenCommand.ImageFittingMode.ShrinkToFit) {
                result = shrinkImageToFit(stream.openStream(), options, imageSize.x, imageSize.y);
            } else if (mode == LoadImageFromKitchenCommand.ImageFittingMode.ScaleAndCropToFit) {
                result = scaleAndCropToFit(stream.openStream(), options, imageSize.x, imageSize.y);
            }
            else {
                result  = Bitmap.createBitmap(10,10, Bitmap.Config.RGB_565);
            }

            return result;
        }
    }

    /**
     * Scales an image along its smaller side and crops off the remaining pixels
     * @param inputStream Stream to load the image from
     * @param options Options from the original stream
     * @param requestedWidth Width
     * @param requestedHeight Height
     * @return Cropped off bitmap
     */
    private Bitmap scaleAndCropToFit(InputStream inputStream, BitmapFactory.Options options, int requestedWidth, int requestedHeight) {
        float aspect = (float)options.outWidth / (float)options.outHeight;

        // Calculate the final size of the scaled image before cropping
        float newWidth;
        float newHeight;

        if (options.outWidth > options.outHeight) {
            // Landscape picture, cut left and right,
            // scale by height
            newHeight = requestedHeight;
            newWidth = newHeight * aspect;
        } else {
            // Vertical picture, cut top and bottom,
            // scale by width
            newWidth = requestedWidth;
            newHeight = newWidth / aspect;
        }

        // Load with the rescaled version with the smaller dimension matching
        Bitmap loadedImage = loadWithOptimalSampleSize(inputStream, options, (int)newWidth, (int)newHeight);

        // Calculate the x/y of the crop operation
        int x = (int)(newWidth / 2 - requestedWidth / 2);
        int y = (int)(newHeight / 2 - requestedHeight / 2);

        Bitmap result = Bitmap.createBitmap(loadedImage, x, y, requestedWidth, requestedHeight);

        // Free memory as we don't need the original image anymore
        loadedImage.recycle();

        return result;
    }

    /**
     * Shrinks the bitmap from the InputStream to fit completely inside the given width and height
     * @param inputStream Stream to load the image from
     * @param options Options from the original stream
     * @param requestedWidth Width of the resulting image
     * @param requestedHeight Height of the resulting image
     * @return Image fitting inside the given width and height
     */
    private Bitmap shrinkImageToFit(InputStream inputStream, BitmapFactory.Options options, int requestedWidth, int requestedHeight) {
        // Load the bitmap with an optimal sample size
        Bitmap loadedImage = loadWithOptimalSampleSize(inputStream, options, requestedWidth, requestedHeight);

        // Calculate the bounds of the rescaled (proportional to aspect ratio) image
        float aspect = (float)loadedImage.getWidth() / (float)loadedImage.getHeight();
        float newHeight = 1;
        float newWidth = 1;
        if (requestedWidth > 0 && requestedHeight > 0) {
            if (requestedWidth < requestedHeight) {
                newWidth = requestedWidth;
                newHeight = newWidth / aspect;
            }
            else {
                newHeight = requestedHeight;
                newWidth = newHeight * aspect;
            }
        }
        else if (requestedWidth > 0) {
            newWidth = requestedWidth;
            newHeight = newWidth / aspect;
        }
        else if (requestedHeight > 0)  {
            newHeight = requestedHeight;
            newWidth = newHeight * aspect;
        }

        // Rescale to the newly calculated fitting bounds
        Bitmap result = Bitmap.createScaledBitmap(loadedImage, (int) newWidth, (int) newHeight, false);

        // Free the space used by the loadedImage
        loadedImage.recycle();

        return result;
    }

    /**
     * Loads an image with an optimal sample size to reduce memory usage
     * @param stream Stream to load the bitmap from
     * @param options Options of the original stream containing outWidth and outHeight
     * @param requestedWidth Width constraint
     * @param requestedHeight Height constraint
     * @return Bitmap loaded with an optimal sample size
     */
    private Bitmap loadWithOptimalSampleSize(InputStream stream, BitmapFactory.Options options, int requestedWidth, int requestedHeight) {
        // Calculate the sample size for the requested size
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, requestedWidth, requestedHeight);

        // Decode with the new sample size
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(stream, new Rect(0, 0, 0, 0), options);
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
        DeleteObjectByIdCommand delete = new DeleteObjectByIdCommand(msg.getId());
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
        final AttachFileToObjectByIdCommand attachCommand = new AttachFileToObjectByIdCommand(kitchenId, imageName, storedImageName);

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
    public void onEventAsync(final ChangeDevicePositionCommand msg){
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
    public void onEventAsync(final ChangeKitchenSettingsCommand msg){
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
    public void onEventAsync(final DeleteDeviceFromAreaCommand msg){
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

    /**
     * Deletes a device
     * @param kitchen Kitchen
     * @param msg Command
     */
    private void deleteDevice(Kitchen kitchen, DeleteDeviceFromAreaCommand msg) {
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
            DeviceFromAreaDeleted event = new DeviceFromAreaDeleted(msg.getKitchenId(), msg.getAreaId(), msg.getDeviceId());
            provider.getUiEventBus().post(event);
        }
    }

    /**
     * Changes the kitchen settings as requested in the message
     * @param value kitchen
     * @param msg message
     */
    private void changeKitchenSettings(Kitchen value, ChangeKitchenSettingsCommand msg) {
        value.setName(msg.getKitchenName());
        value.setDescription(msg.getKitchenDescription());

        List<ValidationErrorOccurred> errors = validateKitchen(value);

        if (errors.size() == 0) {
            // Save the kitchen
            final SaveObjectCommand saveCommand = new SaveObjectCommand();
            saveCommand.setDocumentId(value.getId());
            saveCommand.setData(value);
            provider.getDalEventBus().post(saveCommand);
        }
        else {
            raiseErrors(msg, errors);
        }
    }

    /**
     * Changes the device position according to the message
     * @param msg Message
     */
    private void moveDevicePosition(Kitchen kitchen, ChangeDevicePositionCommand msg) {
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
    public void onEventAsync(final LoadKitchenAreaCommand msg){
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

    /**
     * Loads an area
     * @param kitchen Kitchen
     * @param msg Command
     */
    private void loadArea(Kitchen kitchen, LoadKitchenAreaCommand msg) {
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
                pos.setPosition(new Point(msg.getPosition().x, msg.getPosition().y));
                
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
