package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

/**
 * Requests the load of any object by its type.
 */
public class LoadObjectByTypeCommand {
    String type;

    public LoadObjectByTypeCommand(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
