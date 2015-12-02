package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import android.graphics.Point;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests the load of an image related to a kitchen
 */
public class LoadImageFromKitchenCommand extends RequestResponseConnection{
    /**
     * Describes the method to fit the image into the requested bounds
     */
    public enum ImageFittingMode {
        /**
         * The smaller side of the image is rescaled to match the respective image bounds. Overflowing
         * pixels are cropped.
         */
        ScaleAndCropToFit,

        /**
         * Shrinks the longer side of the image to fit completely into the bounds. Aspect ratio is
         * preserved.
         */
        ShrinkToFit
    }

    private String kitchenId;
    private String imageName;
    private Point imageSize;
    private ImageFittingMode mode = ImageFittingMode.ShrinkToFit;

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

    /**
     * Returns the requested maximum image size
     * @return Maximum or null if not specified
     */
    public Point getImageSize() {
        return imageSize;
    }

    /**
     * Returns the requested maximum image size or null, if the full version should be loaded
     */
    public void setImageSize(Point imageSize) {
        this.imageSize = imageSize;
    }

    /**
     * Gets the image fitting mode for this command
     * @return Image fitting mode
     */
    public ImageFittingMode getMode() {
        return mode;
    }

    /**
     * Sets the image fitting mode for this command
     * @param mode Image fitting mode
     */
    public void setMode(ImageFittingMode mode) {
        this.mode = mode;
    }
}
