package ch.fluxron.fluxronapp.events.modelDal;

/**
 * Deletes an object by id
 */
public class DeleteObjectById {
    private String id;

    public DeleteObjectById(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
