package ch.fluxron.fluxronapp.objectBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * A kitchen.
 */
public class Kitchen {
    private String name;
    @JsonProperty("_id")
    private String id;
    private String description;
    @JsonProperty("areas")
    private List<KitchenArea> areas;

    /**
     * Default empty constructor used for deserialization
     */
    public Kitchen() {
        areas = new ArrayList<>();
    }

    /**
     * Creates a new kitchen
     * @param name Name of the kitchen
     */
    public Kitchen(String name) {
        this();
        this.id = null;
        this.name = name;
    }

    /**
     * Creates a new kitchen
     * @param id Id of the kitchen
     * @param name Name of the kitchen
     */
    public Kitchen(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the name of the kitchen
     * @return Name of the kitchen
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the kitchen
     * @param name Name of the kitchen
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the id of the kitchen
     * @return Id of the kitchen
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the description of the kitchen
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the kitchen
     * @param description Description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the list of all kitchen areas
     * @return List of all ereas
     */
    @JsonIgnore
    public List<KitchenArea> getAreaList(){
        return areas;
    }

    @JsonIgnore
    private int deviceCountCache =0;

    /**
     * Returns the number of devices in this kitchen
     * @return Device count
     */
    @JsonIgnore
    public int getDeviceCount(){
        if (deviceCountCache > 0) return deviceCountCache;
        deviceCountCache=0;
        HashSet<String> deviceIds = new HashSet<>(200);
        for(KitchenArea a : areas) {
            for(DevicePosition p : a.getDevicePositionList()){
                deviceIds.add(p.getDeviceId());
            }
        }

        return deviceCountCache = deviceIds.size();
    }
}
