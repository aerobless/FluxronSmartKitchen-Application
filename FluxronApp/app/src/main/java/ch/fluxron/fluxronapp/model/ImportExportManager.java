package ch.fluxron.fluxronapp.model;

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

    public void onEventAsync(Object msg) {

    }
}
