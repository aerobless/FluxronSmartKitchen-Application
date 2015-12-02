package ch.fluxron.fluxronapp.context;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Environment;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.File;
import java.io.IOException;

import ch.fluxron.fluxronapp.data.Bluetooth;
import ch.fluxron.fluxronapp.data.LocalDatabase;
import ch.fluxron.fluxronapp.data.mocking.FakeBluetooth;
import ch.fluxron.fluxronapp.model.DeviceManager;
import ch.fluxron.fluxronapp.model.ImportExportManager;
import ch.fluxron.fluxronapp.model.KitchenManager;
import ch.fluxron.fluxronapp.model.ObjectCreationManager;
import ch.fluxron.fluxronapp.model.UserManager;
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
    private ObjectCreationManager responder;
    private DeviceManager deviceManager;
    private UserManager userManager;
    private Manager couchbaseManager;
    private Database couchbaseDB;
    private LocalDatabase localDatabase;
    private Bluetooth bluetooth;
    private FakeBluetooth fakeBluetooth;
    private KitchenManager kitchenManger;
    private ImportExportManager importExport;

    /**
     * Called when the application is created
     */
    @Override
    public void onCreate() {
        super.onCreate();

        setUpEventBuses();
        setUpLayers();
        cleanUpTempDirectories();
    }

    /**
     * Cleans up all temporary folders at the start of the application
     */
    private void cleanUpTempDirectories() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                File pictureStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "flx_img" );
                File exportStorageDir = new File(Environment.getExternalStorageDirectory(), "flx_export");

                File[] pictures = pictureStorageDir.listFiles();
                File[] exports = exportStorageDir.listFiles();

                if (pictures !=null) {
                    for (File f : pictures) {
                        f.delete();
                    }
                }

                if(exports!=null) {
                    for (File f : exports) {
                        f.delete();
                    }
                }
            }
        });
    }

    /**
     * Sets up both event buses used in this application
     */
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

    /**
     * Initializes all layers except the UI layer which is handled by android itself
     */
    private void setUpLayers() {
        // Business Layer
        setupBL();

        // Data Access Layer
        setupDal();
    }

    /**
     * Sets up all business layer components
     */
    private void setupBL() {
        responder = new ObjectResponder(modelProvier);
        deviceManager = new DeviceManager(modelProvier);
        userManager = new UserManager(modelProvier);
        kitchenManger = new KitchenManager(modelProvier);
        importExport = new ImportExportManager(modelProvier);
    }

    /**
     * Sets up all data layer components
     */
    private void setupDal() {
        try {
            couchbaseManager = new Manager(new AndroidContext(this.getApplicationContext()), Manager.DEFAULT_OPTIONS);
            couchbaseDB = couchbaseManager.getDatabase("protobase");
            localDatabase = new LocalDatabase(dalToModelProvider, couchbaseDB, this.getContentResolver());
            bluetooth = new Bluetooth(dalToModelProvider, this.getApplicationContext());
            fakeBluetooth = new FakeBluetooth(dalToModelProvider);
        } catch (IOException | CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the event bus that should be used between DAL and BL
     * @return Event Bus
     */
    public EventBus getDalEventBus() {
        return dalToModelEventBus;
    }

    /**
     * Returns the event bus that should be used between UI and BL
     * @return Event Bus
     */
    public EventBus getUiEventBus() {
        return uiToModelEventBus;
    }
}
