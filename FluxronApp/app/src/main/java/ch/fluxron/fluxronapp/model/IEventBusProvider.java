package ch.fluxron.fluxronapp.model;

import de.greenrobot.event.EventBus;

/**
 * Interface for EventBusProvider.
 */
public interface IEventBusProvider {

    EventBus getDalEventBus();
    EventBus getUiEventBus();
}
