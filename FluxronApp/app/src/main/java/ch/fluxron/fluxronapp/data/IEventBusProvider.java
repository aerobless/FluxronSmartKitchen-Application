package ch.fluxron.fluxronapp.data;

import de.greenrobot.event.EventBus;

/**
 * Interface for EventBusProvider.
 */
public interface IEventBusProvider {
    EventBus getDalEventBus();
}
