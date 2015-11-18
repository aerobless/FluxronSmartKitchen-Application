package ch.fluxron.fluxronapp.ui.activities;
import android.net.Uri;
import android.os.Bundle;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.ImportKitchenCommand;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;

/**
 * Activity to import a kitchen
 */
public class KitchenImportActivity extends FluxronBaseActivity {
    private String importConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_kitchen);
    }

    /**
     * Request the import of the kitchen
     */
    @Override
    public void onStart() {
        super.onStart();

        Uri theUri = getIntent().getData();
        if (theUri != null) {
            ImportKitchenCommand cmd = new ImportKitchenCommand(theUri);
            this.importConnection = cmd.getConnectionId();
            postMessage(cmd);
        }
    }

    /**
     * Saves the temporary filename of the kitchen to restore it after instance
     * recreation
     * @param outState Saved instance state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Restores the kitchen path after instance recreation
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Dummy
     * @param msg Event
     */
    public void onEventMainThread(Object msg) {

    }
}