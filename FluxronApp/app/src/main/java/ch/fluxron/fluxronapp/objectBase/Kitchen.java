package ch.fluxron.fluxronapp.objectBase;

/**
 * A kitchen.
 */
public class Kitchen {
    private String name;
    private String id;
    private String description;

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
