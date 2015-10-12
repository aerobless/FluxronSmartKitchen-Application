package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Notifies listeners about an objects loading being completed.
 */
public class ObjectLoaded extends RequestResponseConnection {
    private String id;
    private Object data;

    /**
     * Creates a new command
     * @param id Id of the loaded object
     * @param data Object data
     */
    public ObjectLoaded(String id, Object data){
        this.id = id;
        this.data = data;
    }

    /**
     * Returns the id of the object
     * @return Id of the object
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the object data
     * @return Object data
     */
    public Object getData() {
        return data;
    }
}
