package ch.fluxron.fluxronapp.objectBase;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a detailed view (area) of a kitchen.
 */
public class KitchenArea {
    private String imageName;
    private int relativeId;
    @JsonIgnore
    private String kitchenId;

    /**
     * Creates a new empty area (do not use)
     */
    public KitchenArea(){

    }

    /**
     * Creates a new instance of KitchenArea
     * @param imageName Name of the image associated with this area and kitchen
     * @param kitchenId Id of the Kitchen
     * @param relativeId Id relative to the kitchen. This should be unique per kitchen.
     */
    public KitchenArea(String imageName, String kitchenId, int relativeId) {
        this.imageName = imageName;
        this.kitchenId = kitchenId;
        this.relativeId = relativeId;
    }

    /**
     * Returns the relative id.
     * @return Relative id.
     */
    public int getRelativeId() {
        return relativeId;
    }

    /**
     * Sets the kitchen Id of this area. Warning: This value will not be stored.
     * @param kitchenId
     */
    public void setKitchenId(String kitchenId) {
        this.kitchenId = kitchenId;
    }

    /**
     * Returns the id of the related kitchen
     * @return Kitchen id
     */
    public String getKitchenId() {
        return kitchenId;
    }

    /**
     * Name of the associated image resource
     * @return Image resource
     */
    public String getImageName() {
        return imageName;
    }
}
