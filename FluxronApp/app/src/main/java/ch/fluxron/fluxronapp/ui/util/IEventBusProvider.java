package ch.fluxron.fluxronapp.ui.util;

import de.greenrobot.event.EventBus;

/**
 * Interface for EventBusProvider.
 */
public interface IEventBusProvider {

    EventBus getUiEventBus();
}
