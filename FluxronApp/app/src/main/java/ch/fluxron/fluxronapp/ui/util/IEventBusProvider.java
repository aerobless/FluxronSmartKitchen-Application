package ch.fluxron.fluxronapp.ui.util;

import de.greenrobot.event.EventBus;

/**
 * Interface for EventBusProvider.
 */
public interface IEventBusProvider {
    /**
     * Returns the event bus used to communicate between UI and BL
     * @return Event Bus
     */
    EventBus getUiEventBus();
}
