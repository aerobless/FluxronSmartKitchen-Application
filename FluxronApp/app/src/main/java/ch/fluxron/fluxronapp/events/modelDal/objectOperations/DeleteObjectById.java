package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Deletes an object by id
 */
public class DeleteObjectById extends RequestResponseConnection {
    private String id;

    /**
     * Sets the id of the object to be deleted
     * @param id Id of the object
     */
    public DeleteObjectById(String id){
        this.id = id;
    }

    /**
     * Gets the id of the object to be deleted
     * @return Id of the object
     */
    public String getId() {
        return id;
    }
}
