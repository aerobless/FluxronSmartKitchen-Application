package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

/**
 * Requests the load of any object by its type.
 */
public class LoadObjectByTypeCommand {
    String type;

    /**
     * Requests the load of any object by its type
     * @param type Fully qualified type name
     */
    public LoadObjectByTypeCommand(String type) {
        this.type = type;
    }

    /**
     * Returns the type name
     * @return Name of the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the fully qualified type name of the objects to be loaded
     * @param type Type name
     */
    public void setType(String type) {
        this.type = type;
    }
}
