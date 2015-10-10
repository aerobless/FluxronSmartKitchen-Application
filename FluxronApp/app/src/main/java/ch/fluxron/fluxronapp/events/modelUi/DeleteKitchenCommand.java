package ch.fluxron.fluxronapp.events.modelUi;

/**
 * Requests deletion of a kitchen.
 */
public class DeleteKitchenCommand {
    private String id;

    public DeleteKitchenCommand(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
