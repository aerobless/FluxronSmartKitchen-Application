package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.ExportKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.ChangeKitchenSettingsCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenCommand;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;

/**
 * Activity to change settings for the kitchen
 */
public class KitchenSettingsActivity extends FluxronBaseActivity implements TextWatcher {
    public static final String PARAM_KITCHEN_ID = "KITCHEN_ID";

    private String kitchenId;
    private String loadConnection;

    /**
     * Expects the Kitchen Id as an intent extra and creates a list adapter
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_settings);

        kitchenId = getIntent().getExtras().getString(PARAM_KITCHEN_ID);

        ((TextView) findViewById(R.id.settingsEditName)).addTextChangedListener(this);
        ((TextView) findViewById(R.id.settingsEditDescription)).addTextChangedListener(this);
    }

    /**
     * Request the loading of the kitchen
     */
    @Override
    public void onStart() {
        super.onStart();

        requestKitchenLoad();
    }

    /**
     * Request the load of the kitchen
     */
    private void requestKitchenLoad() {
        LoadKitchenCommand cmd = new LoadKitchenCommand(kitchenId);
        loadConnection = cmd.getConnectionId();
        postMessage(cmd);
    }

    /**
     * Saves the temporary filename of the image to restore it after instance
     * recreation
     * @param outState Saved instance state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PARAM_KITCHEN_ID, kitchenId);
    }

    /**
     * Restores the image path after instance recreation
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.kitchenId = savedInstanceState.getString(PARAM_KITCHEN_ID);
    }

    /**
     * Occurs when a kitchen was loaded
     * @param msg Event
     */
    public void onEventMainThread(KitchenLoaded msg) {
        // Get the kitchen properties
        if (msg.getConnectionId().equals(this.loadConnection)) {
            ((TextView) findViewById(R.id.settingsEditName)).setText(msg.getKitchen().getName());
            ((TextView) findViewById(R.id.settingsEditDescription)).setText(msg.getKitchen().getDescription());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Nothing to do here, only for interface compat
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Nothing to do here, only for interface compat
    }

    @Override
    public void afterTextChanged(Editable s) {
        String name = ((TextView) findViewById(R.id.settingsEditName)).getText().toString();
        String description = ((TextView) findViewById(R.id.settingsEditDescription)).getText().toString();

        // Text changed, send the changes
        ChangeKitchenSettingsCommand cmd = new ChangeKitchenSettingsCommand(kitchenId, name, description);
        postMessage(cmd);
    }

    /**
     * User requested to share the kitchen via EMail
     * @param v Button
     */
    public void shareKitchen(View v){
        ExportKitchenCommand cmd = new ExportKitchenCommand(kitchenId);
        postMessage(cmd);
        // TODO: Wait for export result, then show EMail Intent
        //       http://stackoverflow.com/questions/6078099/android-intent-for-sending-email-with-attachment
    }
}