package ch.fluxron.fluxronapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ch.fluxron.fluxronapp.events.modelDal.objectOperations.AttachFileToObjectById;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.DeleteObjectById;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.FileStreamReady;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetFileStreamFromAttachment;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.ImageLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.AttachImageToKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadImageFromKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.SaveKitchenCommand;

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
    public void onEventAsync(LoadImageFromKitchenCommand msg) {
        GetFileStreamFromAttachment cmd = new GetFileStreamFromAttachment(msg.getKitchenId(), msg.getImageName());
        cmd.setConnectionId(msg);
        provider.getDalEventBus().post(cmd);
    }

    /**
     * Triggered when a file stream is ready to load an image resource
     * @param msg Message containing the stream handle
     */
    public void onEventAsync(FileStreamReady msg){
        Bitmap bmp = BitmapFactory.decodeStream(msg.getStream());
        ImageLoaded event = new ImageLoaded(bmp);
        event.setConnectionId(msg);
        provider.getUiEventBus().post(event);
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
}
