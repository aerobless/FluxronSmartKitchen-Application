package ch.fluxron.fluxronapp.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.CreateKitchenAreaCommand;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.IAreaClickedListener;
import ch.fluxron.fluxronapp.ui.components.ListBubbleControl;
import ch.fluxron.fluxronapp.ui.fragments.AreaDetailFragment;
import ch.fluxron.fluxronapp.ui.fragments.AreaListFragment;

/**
 * Activity to choose and add kitchen areas. Also contains a display of the respective area with
 * all its devices.
 */
public class KitchenActivity extends FluxronBaseActivity implements IAreaClickedListener {
    public static final String PARAM_KITCHEN_ID = "KITCHEN_ID";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String EXTRA_SAVED_FILEPATH = "path";

    private String kitchenId;
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

        // Initialize the area list fragment
        Bundle par = new Bundle();
        par.putString(AreaListFragment.KITCHEN_ID, kitchenId);

        AreaListFragment fragment = new AreaListFragment();
        fragment.setArguments(par);
        fragment.setEventBusProvider(this.busProvider);
        fragment.setClickListener(this);

        // Set the fragment for the area list
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.kitchenArea, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
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
        if(tempFileName!=null) outState.putString(EXTRA_SAVED_FILEPATH, tempFileName.getPath());
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
                CreateKitchenAreaCommand cmd = new CreateKitchenAreaCommand(this.kitchenId, this.tempFileName);
                postMessage(cmd);
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
            Fragment listFragment = getFragmentManager().findFragmentById(R.id.kitchenArea);

            ListBubbleControl listBubbles = (ListBubbleControl)findViewById(R.id.bubbleControl);
            listBubbles.setNumberOfBubbles(msg.getKitchen().getAreaList().size());

            if (listFragment instanceof AreaListFragment) {
                AreaListFragment list = (AreaListFragment) listFragment;

                for (KitchenArea a : msg.getKitchen().getAreaList()) {
                    list.getListAdapter().addOrUpdate(a);
                }
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

    /**
     * A kitchen area was clicked: Navigate to the detail view
     * @param a Area that was clicked
     */
    @Override
    public void areaClicked(KitchenArea a) {
        // Fragment transition to area detail fragment
        AreaDetailFragment fragment = new AreaDetailFragment();

        Bundle b = new Bundle();
        b.putString(AreaDetailFragment.KITCHEN_ID, a.getKitchenId());
        b.putInt(AreaDetailFragment.AREA_RELATIVE_ID, a.getRelativeId());
        fragment.setArguments(b);
        fragment.setEventBusProvider(this.busProvider);
        fragment.setKitchenArea(a);

        getFragmentManager().beginTransaction()
         .replace(R.id.kitchenArea, fragment)
         .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
         .addToBackStack(null)
         .commit();

        // Hide the FAB
        animateFabAlpha(false);
    }

    /**
     * Animates the FAB alpha to the desired value
     * @param visible Button visibility after the animation
     */
    private void animateFabAlpha(boolean visible) {
        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.areaFAB);
        if (visible) {
            fab.show();
        }else{
            fab.hide();
        }
    }

    /**
     * The back button was pressed, we need to decide if we navigate on the fragment or the
     * activity stack
     */
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            requestKitchenLoad();
            animateFabAlpha(true);
        } else {
            super.onBackPressed();
        }
    }
}