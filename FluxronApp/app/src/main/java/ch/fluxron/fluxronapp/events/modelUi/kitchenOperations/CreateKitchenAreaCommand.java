package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import android.net.Uri;

/**
 * Signals the request to save a kitchen area
 */
public class CreateKitchenAreaCommand {
    private String id;
    private Uri imagePath;

    /**
     * New create kitchen area command
     * @param id        Id of the kitchen to delete
     * @param imagePath Uri of the image
     */
    public CreateKitchenAreaCommand(String id, Uri imagePath) {
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
