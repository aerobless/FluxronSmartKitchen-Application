package ch.fluxron.fluxronapp.ui.util;

import de.greenrobot.event.EventBus;

/**
 * Interface for EventBusProvider.
 */
public interface IEventBusProvider {
    /**
     * Gets the event bus between BL and UI
     * @return event bus
     */
    EventBus getUiEventBus();
}
