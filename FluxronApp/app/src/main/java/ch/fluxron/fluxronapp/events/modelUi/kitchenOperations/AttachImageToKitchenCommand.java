package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import android.net.Uri;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Reqeuests to Attach an image file to a kitchen
 */
public class AttachImageToKitchenCommand extends RequestResponseConnection{
    private String id;
    private Uri imagePath;

    /**
     * New delete kitchen command
     * @param id Id of the kitchen to delete
     */
    public AttachImageToKitchenCommand(String id, Uri imagePath){
        this.id = id;
        this.imagePath = imagePath;
    }

    /**
     * Returns the id of the kitchen to delete
     * @return Id of the kitchen to delete
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the path to the image file
     * @return Path to the image file
     */
    public Uri getImagePath() {
        return imagePath;
    }
}
