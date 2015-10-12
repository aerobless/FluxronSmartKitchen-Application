package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests the load of a kitchen by id
 */
public class LoadKitchenCommand extends RequestResponseConnection {
    private String id;

    /**
     * Creates a new command to load a kitchen by id
     * @param id Id of the kitchen
     */
    public LoadKitchenCommand(String id) {
        this.id = id;
    }

    /**
     * Gets the id of the kitchen to load
     * @return Id of the kitchen
     */
    public String getId() {
        return id;
    }
}
