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

import ch.fluxron.fluxronapp.eventsbase.IEventBusProvider;
import ch.fluxron.fluxronapp.model.PrototypeResponder;
import de.greenrobot.event.EventBus;

/**
 * Main Context for the Application
 */
public class FluxronApplication extends Application implements IEventBusProvider {
    private EventBus uiToModelEventBus;
    private EventBus dalToModelEventBus;
    private PrototypeResponder responder;
    private Manager couchbaseManager;
    private Database couchbaseDB;

    @Override
    public void onCreate() {
        super.onCreate();

        setUpEventBuses();
        setUpLayers();
    }

    private void setUpLayers() {
        // Business layer
        responder = new PrototypeResponder(this);

        // DAL
        try {
            couchbaseManager = new Manager(new AndroidContext(this.getApplicationContext()), Manager.DEFAULT_OPTIONS);
            try {
                couchbaseDB = couchbaseManager.getDatabase("protobase");

                Document doc = couchbaseDB.getExistingDocument("testdoc");

                if (doc==null) {
                    doc = couchbaseDB.getDocument("testdoc");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("testvalue", "Some Text Here");
                    doc.putProperties(map);
                }
                else {
                    Log.d("FLUXRON.PROTOTYPE", "retrievedDocument=" + String.valueOf(doc.getProperties()));
                }
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpEventBuses(){
        uiToModelEventBus = new EventBus();
        dalToModelEventBus = new EventBus();
    }

    @Override
    public EventBus getEventBus() {
        return uiToModelEventBus;
    }
}
