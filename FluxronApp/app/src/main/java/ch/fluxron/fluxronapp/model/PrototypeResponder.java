package ch.fluxron.fluxronapp.model;

import ch.fluxron.fluxronapp.eventsbase.IEventBusProvider;
import ch.fluxron.fluxronapp.modelevents.SimpleMessage;
import ch.fluxron.fluxronapp.modelevents.SimpleMessageResponse;

/**
 * Responds to a message. FOR PROTOTYPE USAGE ONLY!!!
 */
public class PrototypeResponder {

    private IEventBusProvider provider;

    public PrototypeResponder(IEventBusProvider provider) {
        this.provider = provider;
        provider.getEventBus().register(this);
    }

    public void onEventAsync(SimpleMessage msg) throws InterruptedException {
        for (int i =0; i < 30; i++) {
            SimpleMessageResponse response = new SimpleMessageResponse();
            response.setMessageText("hello " + i);
            provider.getEventBus().post(response);
            Thread.sleep(500);
        }
    }
}
