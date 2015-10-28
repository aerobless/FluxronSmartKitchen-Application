package ch.fluxron.fluxronapp.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenCommand;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.AreaListAdapter;
import ch.fluxron.fluxronapp.ui.decorators.SpacesItemDecoration;

/**
 * Activity to choose and add kitchen areas. Also contains a display of the respective area with
 * all its devices.
 */
public class KitchenActivity extends FluxronBaseActivity {
    public static final String PARAM_KITCHEN_ID = "KITCHEN_ID";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String EXTRA_SAVED_FILEPATH = "path";

    private String kitchenId;
    private AreaListAdapter listAdapter;
    private Uri tempFileName;

    /**
     * Expects the Kitchen Id as an intent extra and creates a list adapter
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        kitchenId = getIntent().getExtras().getString(PARAM_KITCHEN_ID);

        // Set the list's properties
        RecyclerView kitchenListView = (RecyclerView) findViewById(R.id.areaList);

        // Layout for the list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        kitchenListView.setLayoutManager(layoutManager);

        // Item decoration for the list
        SpacesItemDecoration deco = new SpacesItemDecoration(10);
        kitchenListView.addItemDecoration(deco);

        // List adapter
        listAdapter = new AreaListAdapter(this.busProvider);
        kitchenListView.setAdapter(listAdapter);
    }

    /**
     * Request the loading of the kitchen
     */
    @Override
    public void onStart() {
        super.onStart();

        // Request the load of the kitchen we are displaying
        postMessage(new LoadKitchenCommand(kitchenId));
    }

    /**
     * Saves the temporary filename of the image to restore it after instance
     * recreation
     * @param outState Saved instance state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_SAVED_FILEPATH, tempFileName.getPath());
    }

    /**
     * Restores the image path after instance recreation
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedPath = savedInstanceState.getString(EXTRA_SAVED_FILEPATH);
        if(savedPath != null) {
            this.tempFileName = Uri.parse(savedPath);
        }
    }

    /**
     * Checks if there was an image taken and kicks of the creation of a new kitche area
     * @param requestCode Request code
     * @param resultCode Result code
     * @param data Original intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Picture has been taken, create a new area
                // TODO: Saving of Kitchen area
            }
        }
    }

    /**
     * Occurs when a kitchen was loaded
     * @param msg Event
     */
    public void onEventMainThread(KitchenLoaded msg) {
        // Set the name of the kitchen as title text and
        // fill the list adapter with the data when the kitchen is loaded
        if (msg.getKitchen().getId().equals(kitchenId)) {
            ((TextView) findViewById(R.id.kitchenNameTitle)).setText(msg.getKitchen().getName());

            for (KitchenArea a : msg.getKitchen().getAreaList()) {
                listAdapter.addOrUpdate(a);
            }
        }
    }

    /**
     * Back button was pressed
     * @param button Button that was pressed
     */
    public void onBackButtonClicked(View button) {
        // Close this kitchen and move back one view on the stack.
        finish();
    }

    /**
     * Not in use currently
     * @param button Button that was pressed
     */
    public void onEditDeviceClicked(View button) {
        // Edit this device
        Intent editDevice = new Intent(this, DeviceActivity.class);
        editDevice.putExtra("DEVICE_ID", "xxx-dsf-er22-34234-d00");
        startActivity(editDevice);
    }

    /**
     * The user requested the deletion of the kitchen
     * @param button Button that was pressed
     */
    public void onDeleteButtonClicked(View button) {
        // Send a deletion command
        postMessage(new DeleteKitchenCommand(kitchenId));
        finish();
    }

    /**
     * The user requested the creation of a kitchen
     * @param button Button that was pressed
     */
    public void createArea(View button) {
        // The user wants to create a new Kitchen Area by taking a picture
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempFileName = getImageFileUri();
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileName);
        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
}