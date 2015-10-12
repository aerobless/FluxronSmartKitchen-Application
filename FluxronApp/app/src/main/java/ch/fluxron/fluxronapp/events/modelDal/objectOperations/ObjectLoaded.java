package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

/**
 * Notifies listeners about an objects loading being completed.
 */
public class ObjectLoaded {
    private String id;
    private Object data;

    public ObjectLoaded(String id, Object data){
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public Object getData() {
        return data;
    }
}
