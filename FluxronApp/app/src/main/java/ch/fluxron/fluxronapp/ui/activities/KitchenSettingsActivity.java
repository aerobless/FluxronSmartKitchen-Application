package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenCommand;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;

/**
 * Activity to change settings for the kitchen
 */
public class KitchenSettingsActivity extends FluxronBaseActivity {
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
}