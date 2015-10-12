package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests the load of any object by its id.
 */
public class LoadObjectByIdCommand extends RequestResponseConnection {
    private String id;

    /**
     * Returns the id of the object
     * @return Id of the object
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the object
     * @param id id of the object
     */
    public LoadObjectByIdCommand(String id) {
        this.id = id;
    }
}
