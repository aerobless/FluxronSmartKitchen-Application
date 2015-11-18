package ch.fluxron.fluxronapp.events.modelUi.importExportOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests the export of a kitchen
 */
public class ExportKitchenCommand extends RequestResponseConnection{
    private String kitchenId;

    /**
     * Specifies the Id of the kitchen to be exported
     * @param kitchenId Id of the kitchen
     */
    public ExportKitchenCommand(String kitchenId) {
        this.kitchenId = kitchenId;
    }

    /**
     * Gets the Id of the kitchen that should be exported
     * @return Id of the kitchen that should be exported
     */
    public String getKitchenId() {
        return kitchenId;
    }
}
