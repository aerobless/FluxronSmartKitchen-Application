package ch.fluxron.fluxronapp.context;

import android.app.Application;

import ch.fluxron.fluxronapp.eventsbase.IEventBusProvider;
import ch.fluxron.fluxronapp.model.PrototypeResponder;
import de.greenrobot.event.EventBus;

/**
 * Main Context for the Application
 */
public class FluxronApplication extends Application implements IEventBusProvider {
    private EventBus uiToModelEventBus;
    private PrototypeResponder responder;

    @Override
    public void onCreate() {
        super.onCreate();

        setUpEventBuses();
        setUpLayers();
    }

    private void setUpLayers() {
        responder = new PrototypeResponder(this);
    }

    private void setUpEventBuses(){
        uiToModelEventBus = new EventBus();
    }

    @Override
    public EventBus getEventBus() {
        return uiToModelEventBus;
    }
}
