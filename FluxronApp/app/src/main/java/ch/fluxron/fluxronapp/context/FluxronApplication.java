package ch.fluxron.fluxronapp.context;

import android.app.Application;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

import ch.fluxron.fluxronapp.data.Bluetooth;
import ch.fluxron.fluxronapp.data.LocalDatabase;
import ch.fluxron.fluxronapp.model.PrototypeResponder;
import de.greenrobot.event.EventBus;

/**
 * Main Context for the Application
 */
public class FluxronApplication extends Application implements ch.fluxron.fluxronapp.ui.util.IEventBusProvider {
    private EventBus uiToModelEventBus;
    private EventBus dalToModelEventBus;
    private ch.fluxron.fluxronapp.ui.util.IEventBusProvider uiToModelProvider;
    private ch.fluxron.fluxronapp.data.IEventBusProvider dalToModelProvider;
    private ch.fluxron.fluxronapp.model.IEventBusProvider modelProvier;
    private PrototypeResponder responder;
    private Manager couchbaseManager;
    private Database couchbaseDB;
    private LocalDatabase localDatabase;
    private Bluetooth bluetooth;

    @Override
    public void onCreate() {
        super.onCreate();

        setUpEventBuses();
        setUpLayers();
    }

    private void setUpEventBuses(){
        uiToModelEventBus = new EventBus();
        uiToModelProvider = new ch.fluxron.fluxronapp.ui.util.IEventBusProvider() {
            @Override
            public EventBus getUiEventBus() {
                return uiToModelEventBus;
            }
        };

        dalToModelEventBus = new EventBus();
        dalToModelProvider = new ch.fluxron.fluxronapp.data.IEventBusProvider() {
            @Override
            public EventBus getDalEventBus() {
                return dalToModelEventBus;
            }
        };

        modelProvier = new ch.fluxron.fluxronapp.model.IEventBusProvider(){
            @Override
            public EventBus getDalEventBus() {
                return dalToModelEventBus;
            }

            @Override
            public EventBus getUiEventBus() {
                return uiToModelEventBus;
            }
        };
    }

    private void setUpLayers() {
        // Business layer
        responder = new PrototypeResponder(modelProvier);

        setupDal();
    }

    private void setupDal() {
        try {
            couchbaseManager = new Manager(new AndroidContext(this.getApplicationContext()), Manager.DEFAULT_OPTIONS);
            couchbaseDB = couchbaseManager.getDatabase("protobase");
            localDatabase = new LocalDatabase(dalToModelProvider, couchbaseDB);
            bluetooth = new Bluetooth(dalToModelProvider);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public EventBus getDalEventBus() {
        return dalToModelEventBus;
    }

    public EventBus getUiEventBus() {
        return uiToModelEventBus;
    }
}
