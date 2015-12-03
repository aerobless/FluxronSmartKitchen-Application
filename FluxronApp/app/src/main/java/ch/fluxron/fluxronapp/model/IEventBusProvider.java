package ch.fluxron.fluxronapp.model;

import de.greenrobot.event.EventBus;

/**
 * Interface for EventBusProvider.
 */
public interface IEventBusProvider {

    /**
     * Gets the event bus to communicate between BL and DAL
     * @return Event bus
     */
    EventBus getDalEventBus();

    /**
     * Gets the event bus to communicate between BL and UI
     * @return Event bus
     */
    EventBus getUiEventBus();
}
