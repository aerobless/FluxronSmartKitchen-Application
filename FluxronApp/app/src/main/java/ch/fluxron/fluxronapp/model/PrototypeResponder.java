package ch.fluxron.fluxronapp.model;

import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectCreated;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenCreated;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;

/**
 * Responds to a message. FOR PROTOTYPE USAGE ONLY!!!
 */
public class PrototypeResponder {

    private IEventBusProvider provider;

    public PrototypeResponder(IEventBusProvider provider) {
        this.provider = provider;
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
    }

    public void onEventAsync(ObjectLoaded msg) {
        if (msg.getData() instanceof Kitchen) {
            KitchenLoaded event = new KitchenLoaded((Kitchen) msg.getData());

            for (int i = 1; i <= 10; i++) {
                event.getKitchen().getAreaList().add(new KitchenArea("mainPicture", event.getKitchen().getId(), i));
            }
            event.setConnectionId(msg);
            provider.getUiEventBus().post(event);
        }
    }

    public void onEventAsync(ObjectCreated msg) {
        if (msg.getData() instanceof Kitchen) {
            KitchenCreated event = new KitchenCreated((Kitchen) msg.getData());
            event.setConnectionId(msg);
            provider.getUiEventBus().post(event);
        }
    }
}
