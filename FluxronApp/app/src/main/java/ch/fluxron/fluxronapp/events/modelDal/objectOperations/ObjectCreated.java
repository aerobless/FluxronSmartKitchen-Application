package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Notifies subscribers about an objects creation
 */
public class ObjectCreated extends RequestResponseConnection {
    private Object data;

    private String id;

    /**
     * Returns the Object that was created
     * @return Object
     */
    public Object getData() {
        return data;
    }

    /**
     * Gets the id of the created Object
     * @return Id
     */
    public String getId() {
        return id;
    }

}
