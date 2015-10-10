package ch.fluxron.fluxronapp.events.modelUi;

/**
 * Requests the load of a kitchen
 */
public class LoadKitchenCommand {
    private String id;

    public LoadKitchenCommand(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
