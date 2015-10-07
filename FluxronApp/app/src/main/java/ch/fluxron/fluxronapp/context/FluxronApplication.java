package ch.fluxron.fluxronapp.context;

import android.app.Application;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ch.fluxron.fluxronapp.data.LocalDatabase;
import ch.fluxron.fluxronapp.eventsbase.IEventBusProvider;
import ch.fluxron.fluxronapp.model.PrototypeResponder;
import de.greenrobot.event.EventBus;

/**
 * Main Context for the Application
 */
public class FluxronApplication extends Application implements ch.fluxron.fluxronapp.ui.util.IEventBusProvider {
    private EventBus uiToModelEventBus;
    private EventBus dalToModelEventBus;
    private IEventBusProvider uiToModelProvider;
    private IEventBusProvider dalToModelProvider;
    private PrototypeResponder responder;
    private Manager couchbaseManager;
    private Database couchbaseDB;
    private LocalDatabase localDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        setUpEventBuses();
        setUpLayers();
    }

    private void setUpEventBuses(){
        uiToModelEventBus = new EventBus();
        uiToModelProvider = new IEventBusProvider() {
            @Override
            public EventBus getEventBus() {
                return getUiEventBus();
            }
        };

        dalToModelEventBus = new EventBus();
        dalToModelProvider = new IEventBusProvider() {
            @Override
            public EventBus getEventBus() {
                return getDalEventBus();
            }
        };
    }

    private void setUpLayers() {
        // Business layer
        responder = new PrototypeResponder(uiToModelProvider);

        setupDal();
    }

    private void setupDal() {
        try {
            couchbaseManager = new Manager(new AndroidContext(this.getApplicationContext()), Manager.DEFAULT_OPTIONS);
            couchbaseDB = couchbaseManager.getDatabase("protobase");
            localDatabase = new LocalDatabase(dalToModelProvider, couchbaseDB);
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
