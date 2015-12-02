package ch.fluxron.fluxronapp.data;

import de.greenrobot.event.EventBus;

/**
 * Interface for EventBusProvider.
 */
public interface IEventBusProvider {
    /**
     * Returns the event bus between DAL and BL
     * @return Event Bus
     */
    EventBus getDalEventBus();
}
