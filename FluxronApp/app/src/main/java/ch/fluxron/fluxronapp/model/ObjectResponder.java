package ch.fluxron.fluxronapp.model;

import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectCreated;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenCreated;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;

/**
 * Responds to ObjectCreated messages.
 */
public class ObjectResponder {

    private IEventBusProvider provider;

    /**
     * Instantiates a new ObjectResponder.
     *
     * @param provider
     */
    public ObjectResponder(IEventBusProvider provider) {
        this.provider = provider;
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
    }

    /**
     * Listens to ObjectCreated events.
     *
     * @param msg
     */
    public void onEventAsync(ObjectCreated msg) {
        if (msg.getData() instanceof Kitchen) {
            KitchenCreated event = new KitchenCreated((Kitchen) msg.getData());
            event.setConnectionId(msg);
            provider.getUiEventBus().post(event);
        }
    }
}
