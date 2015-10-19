package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import android.util.Size;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Reqests the load of an image related to a kitchen
 */
public class LoadImageFromKitchenCommand extends RequestResponseConnection{
    private String kitchenId;
    private String imageName;

    /**
     * Sets the id and the image name to load
     * @param kitchenId Id of the kitchen
     * @param imageName Name of the image
     */
    public LoadImageFromKitchenCommand(String kitchenId, String imageName) {
        this.kitchenId = kitchenId;
        this.imageName = imageName;
    }

    /**
     * Id of the kitchen
     * @return Id of the kitchen
     */
    public String getKitchenId() {
        return kitchenId;
    }

    /**
     * Name of the image
     * @return Name of the image
     */
    public String getImageName() {
        return imageName;
    }
}