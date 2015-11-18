package ch.fluxron.fluxronapp.model;

import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.ImportKitchenCommand;

/**
 * Manages import and export requests for kitchens
 */
public class ImportExportManager {
    private IEventBusProvider provider;

    /**
     * Sets the event bus this manager should be operating on
     * @param provider Event Bus Provider
     */
    public ImportExportManager(IEventBusProvider provider) {
        this.provider = provider;

        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
    }

    public void onEventAsync(ImportKitchenCommand msg) {

    }
}
