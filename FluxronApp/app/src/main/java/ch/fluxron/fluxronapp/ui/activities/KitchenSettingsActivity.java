package ch.fluxron.fluxronapp.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.ValidationErrorOccurred;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.ExportKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.KitchenExported;
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
    private String exportConnection;
    private String changeSettingsConnection;

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

    /**
     * Occurs when a validation error occurred
     * @param msg Event
     */
    public void onEventMainThread(ValidationErrorOccurred msg) {
        if (msg.getConnectionId().equals(this.changeSettingsConnection)) {
            TextView error = (TextView)findViewById(R.id.textViewSettingsError);
            error.setText(msg.getErrorMessageResourceId());
            this.animateFadeIn(error, false);
        }
    }

    /**
     * Interface compat
     * @param s Sequence
     * @param start Start
     * @param count Count
     * @param after Left
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Nothing to do here, only for interface compat
    }

    /**
     * Interface compat
     * @param s Sequence
     * @param start Start
     * @param before Before
     * @param count Count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Nothing to do here, only for interface compat
    }

    /**
     * Text changed, we need to update the values
     * @param s Values
     */
    @Override
    public void afterTextChanged(Editable s) {
        String name = ((TextView) findViewById(R.id.settingsEditName)).getText().toString();
        String description = ((TextView) findViewById(R.id.settingsEditDescription)).getText().toString();

        this.animateFadeOut(findViewById(R.id.textViewSettingsError), false, 150);

        // Text changed, send the changes
        ChangeKitchenSettingsCommand cmd = new ChangeKitchenSettingsCommand(kitchenId, name, description);
        this.changeSettingsConnection = cmd.getConnectionId();
        postMessage(cmd);
    }

    /**
     * User requested to share the kitchen via EMail
     * @param v Button
     */
    public void shareKitchen(View v){
        v.setEnabled(false);
        v.animate().alpha(0).setDuration(150).start();
        this.animateFadeOut(findViewById(R.id.textViewSettingsError), false);
        findViewById(R.id.exportSpinner).animate().alpha(1).setDuration(150).start();
        ExportKitchenCommand cmd = new ExportKitchenCommand(kitchenId);
        this.exportConnection = cmd.getConnectionId();
        postMessage(cmd);
    }

    /**
     * Occurs when a kitchen was exported
     * @param msg Event with uri location
     */
    public void onEventMainThread(KitchenExported msg) {
        // Get the kitchen properties
        if (msg.getConnectionId().equals(this.exportConnection)) {
            this.exportConnection = null;
            findViewById(R.id.exportButton).setEnabled(true);
            findViewById(R.id.exportButton).animate().alpha(1).setDuration(150).start();
            findViewById(R.id.exportSpinner).animate().alpha(0).setDuration(150).start();

            startMailWithAttachment(msg.getLocation());
        }
    }

    /**
     * Starts the sending of the exported EMail
     * @param location Location of the file to attach
     */
    private void startMailWithAttachment(Uri location) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/fluxron");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Share a kitchen");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Kitchen is attached.");
        emailIntent.putExtra(Intent.EXTRA_STREAM, location);
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
}