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

    public Kitchen(String name) {
        this();
        this.id = null;
        this.name = name;
    }

    public Kitchen(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public List<KitchenArea> getAreaList(){
        return areas;
    }

    @JsonIgnore
    private int deviceCountCache =0;
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
