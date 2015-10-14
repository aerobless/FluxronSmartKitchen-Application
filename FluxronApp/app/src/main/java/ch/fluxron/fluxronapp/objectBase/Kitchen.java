package ch.fluxron.fluxronapp.objectBase;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A kitchen.
 */
public class Kitchen {
    private String name;
    @JsonProperty("_id")
    private String id;
    private String description;

    /**
     * Default empty constructor used for deserialization
     */
    public Kitchen() {

    }

    public Kitchen(String name) {
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
}
