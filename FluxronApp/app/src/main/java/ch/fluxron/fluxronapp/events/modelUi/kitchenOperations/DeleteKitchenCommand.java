package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests deletion of a kitchen.
 */
public class DeleteKitchenCommand extends RequestResponseConnection {
    private String id;

    /**
     * New delete kitchen command
     * @param id Id of the kitchen to delete
     */
    public DeleteKitchenCommand(String id){
        this.id = id;
    }

    /**
     * Returns the id of the kitchen to delete
     * @return Id of the kitchen to delete
     */
    public String getId() {
        return id;
    }
}
