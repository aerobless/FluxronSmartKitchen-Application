package ch.fluxron.fluxronapp.ui.activities;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.ImportKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.LoadImportMetadata;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.MetadataLoaded;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;

/**
 * Activity to import a kitchen
 */
public class KitchenImportActivity extends FluxronBaseActivity {
    private String importMetadataConnection;

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
            LoadImportMetadata cmd = new LoadImportMetadata(theUri);
            this.importMetadataConnection = cmd.getConnectionId();
            postMessage(cmd);
            
            findViewById(R.id.importProgressBar).animate().alpha(1).setDuration(250).start();
            findViewById(R.id.infoPanel).animate().alpha(2).setDuration(250).start();
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
     * Metadata was loaded, ask the user if he wants to import it
     * @param msg Event
     */
    public void onEventMainThread(MetadataLoaded msg) {
        if (msg.getConnectionId().equals(this.importMetadataConnection)) {
            importMetadataConnection = null;

            ((TextView)findViewById(R.id.manifestName)).setText(msg.getMetadata().getObjectName());
            ((TextView)findViewById(R.id.manifestDescription)).setText(msg.getMetadata().getObjectDescription());

            Date savedAt = msg.getMetadata().getSaveDate();
            ((TextView) findViewById(R.id.manifestSaveDate)).setText(DateFormat.getDateTimeInstance().format(savedAt));

            findViewById(R.id.importProgressBar).animate().alpha(0).setDuration(250).start();
            findViewById(R.id.infoPanel).animate().alpha(1).setDuration(250).start();
        }
    }
}