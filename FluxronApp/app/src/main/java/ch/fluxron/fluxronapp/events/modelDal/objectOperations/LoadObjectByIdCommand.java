package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

/**
 * Requests the load of any object by its id.
 */
public class LoadObjectByIdCommand {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
