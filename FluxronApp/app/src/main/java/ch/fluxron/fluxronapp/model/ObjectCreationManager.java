package ch.fluxron.fluxronapp.model;

import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectCreated;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenCreated;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Responds to generic object message
 */
public class ObjectCreationManager {

    private IEventBusProvider provider;

    /**
     * Creates a new object creation manager
     * @param provider Provider
     */
    public ObjectCreationManager(IEventBusProvider provider) {
        this.provider = provider;
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
    }

    /**
     * Object was created
     * @param msg Message
     */
    public void onEventAsync(ObjectCreated msg) {
        if (msg.getData() instanceof Kitchen) {
            KitchenCreated event = new KitchenCreated((Kitchen) msg.getData());
            event.setConnectionId(msg);
            provider.getUiEventBus().post(event);
        }
    }
}
