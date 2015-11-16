package ch.fluxron.fluxronapp.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.InjectDevicesCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.AddDeviceToAreaCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.ChangeDevicePositionCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteDeviceFromAreaCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeviceFromAreaDeleted;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.CreateKitchenAreaCommand;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.objectBase.DevicePosition;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.IAreaClickedListener;
import ch.fluxron.fluxronapp.ui.components.KitchenAreaDisplay;
import ch.fluxron.fluxronapp.ui.components.ListBubbleControl;
import ch.fluxron.fluxronapp.ui.fragments.AreaDetailFragment;
import ch.fluxron.fluxronapp.ui.fragments.AreaListFragment;
import ch.fluxron.fluxronapp.ui.fragments.DeviceListFragment;

/**
 * Activity to choose and add kitchen areas. Also contains a display of the respective area with
 * all its devices.
 */
public class KitchenActivity extends FluxronBaseActivity implements IAreaClickedListener, DeviceListFragment.IDeviceAddedListener, KitchenAreaDisplay.IKitchenAreaListener {
    public static final String PARAM_KITCHEN_ID = "KITCHEN_ID";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String EXTRA_SAVED_FILEPATH = "path";

    private String kitchenId;
    private Uri tempFileName;
    private KitchenArea currentArea;

    /**
     * Expects the Kitchen Id as an intent extra and creates a list adapter
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        kitchenId = getIntent().getExtras().getString(PARAM_KITCHEN_ID);

        // Initialize the area list fragment only if no instance state is stored
        if (savedInstanceState == null) {
            createAreaList();
        }
    }

    private void createAreaList() {
        Bundle par = new Bundle();
        par.putString(AreaListFragment.KITCHEN_ID, kitchenId);

        AreaListFragment fragment = new AreaListFragment();
        fragment.setArguments(par);
        fragment.setEventBusProvider(this.busProvider);
        fragment.setClickListener(this);

        // Set the fragment for the area list
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.kitchenArea, fragment);
        ft.setCustomAnimations(R.anim.activity_in, R.anim.activity_out);
        ft.commit();
    }

    /**
     * Request the loading of the kitchen
     */
    @Override
    public void onStart() {
        super.onStart();

        changeChildDisplayState();

        requestKitchenLoad();
    }

    private void changeChildDisplayState() {
        boolean bIsDetailView = false;
        int count = getFragmentManager().getBackStackEntryCount();

        if (count > 0) {
            String currentName = getFragmentManager().getBackStackEntryAt(count - 1).getName();

            bIsDetailView = AreaDetailFragment.class.getName().equals(currentName);
        }
        
        animateEditButton(bIsDetailView);
        animateFabAlpha(!bIsDetailView);
        animateSettingsIcon(!bIsDetailView);
        animateBubbleBar(!bIsDetailView);
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
            currentArea = null;
            ((TextView) findViewById(R.id.kitchenNameTitle)).setText(msg.getKitchen().getName());
            Fragment listFragment = getFragmentManager().findFragmentById(R.id.kitchenArea);

            ListBubbleControl listBubbles = (ListBubbleControl)findViewById(R.id.bubbleControl);
            listBubbles.setNumberOfBubbles(msg.getKitchen().getAreaList().size());
            listBubbles.setCurrentBubble(0);

            if (listFragment instanceof AreaListFragment) {
                AreaListFragment list = (AreaListFragment) listFragment;

                for (KitchenArea a : msg.getKitchen().getAreaList()) {
                    list.getListAdapter().addOrUpdate(a);
                }
            }
        }

        // Send all the devices contained in this kitchen to the device manager
        List<KitchenArea> kitchenAreas = msg.getKitchen().getAreaList();
        Set<DevicePosition> devices = new HashSet<>();
        for(KitchenArea area:kitchenAreas){
            for(DevicePosition dp:area.getDevicePositionList()){
                devices.add(dp);
            }
        }
        InjectDevicesCommand idf = new InjectDevicesCommand(devices);
        busProvider.getUiEventBus().post(idf);
    }

    /**
     * Occurs when a device was removed from an area
     * @param msg Event
     */
    public void onEventMainThread(DeviceFromAreaDeleted msg) {
        if (currentArea!= null && currentArea.getKitchenId().equals(msg.getKitchenId()) && currentArea.getRelativeId() == msg.getAreaId()){
            Fragment f = getFragmentManager().findFragmentById(R.id.kitchenArea);
            if (f instanceof AreaDetailFragment) {
                ((AreaDetailFragment)f).removeDevice(msg.getDeviceId());
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
     * The user requested to switch to the edit mode
     * @param button Button that was pressed
     */
    public void onEditButtonClicked(View button) {
        showDeviceSelectionList();
        animateEditButton(false);
    }

    /**
     * The user requested to edit the kitchen settings
     * @param button Button that was pressed
     */
    public void onSettingsButtonClicked(View button) {
        Intent editSettings = new Intent(this, KitchenSettingsActivity.class);
        editSettings.putExtra(KitchenSettingsActivity.PARAM_KITCHEN_ID, kitchenId);
        startActivity(editSettings);
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
        currentArea = a;

        // Fragment transition to area detail fragment
        AreaDetailFragment fragment = new AreaDetailFragment();

        Bundle b = new Bundle();
        b.putString(AreaDetailFragment.KITCHEN_ID, a.getKitchenId());
        b.putInt(AreaDetailFragment.AREA_RELATIVE_ID, a.getRelativeId());
        fragment.setArguments(b);
        fragment.setEventBusProvider(this.busProvider);
        fragment.setKitchenArea(a);
        fragment.setAreaListener(this);

        getFragmentManager().beginTransaction()
         .replace(R.id.kitchenArea, fragment)
         .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
         .addToBackStack(AreaDetailFragment.class.getName())
         .commit();

        // Hide the FAB and bubble bar
        animateFabAlpha(false);
        animateBubbleBar(false);
        animateSettingsIcon(false);

        // Show the button for area editing
        animateEditButton(true);
    }

    @Override
    public void areaScrolled(int pos) {
        ListBubbleControl c = (ListBubbleControl)findViewById(R.id.bubbleControl);
        c.setCurrentBubble(pos);
    }

    /**
     * Changes the state of the area edit button
     * @param visible Visible or not
     */
    private void animateEditButton(boolean visible) {
        if(visible){
            animateFadeIn(findViewById(R.id.editViewButton), true);
        }
        else {
            animateFadeOut(findViewById(R.id.editViewButton), true);
        }
    }

    /**
     * Changes the state of the settings button
     * @param visible Visible or not
     */
    private void animateSettingsIcon(boolean visible) {
        if(visible){
            animateFadeIn(findViewById(R.id.settingsButton), true);
        }
        else {
            animateFadeOut(findViewById(R.id.settingsButton), true);
        }
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
     * Shows or hides the bubble bar depending on the given boolean value
     * @param visible Visible or not
     */
    private void animateBubbleBar(boolean visible){
        final ListBubbleControl bubble = (ListBubbleControl)findViewById(R.id.bubbleControl);
        if(visible){
            bubble.setVisibility(View.VISIBLE);
        }
        else {
            bubble.setVisibility(View.GONE);
        }
    }

    /**
     * Shows the device selection list via fragment transactions
     */
    private void showDeviceSelectionList(){
        DeviceListFragment fragment = new DeviceListFragment();
        fragment.setEventBusProvider(this.busProvider);
        fragment.setListener(this);
        findViewById(R.id.deviceListLayout).setVisibility(View.VISIBLE);
        getFragmentManager().beginTransaction()
                .replace(R.id.deviceListLayout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(DeviceListFragment.class.getName())
                .commit();

        // Set to edit mode
        setEditMode(true);
    }

    /**
     * Sets the edit mode of the area detail fragment
     */
    private void setEditMode(boolean canEdit) {
        Fragment f = getFragmentManager().findFragmentById(R.id.kitchenArea);
        if (f instanceof AreaDetailFragment) {
            ((AreaDetailFragment)f).setEditMode(canEdit);
        }
    }

    /**
     * The back button was pressed, we need to decide if we navigate on the fragment or the
     * activity stack
     */
    @Override
    public void onBackPressed() {
        FragmentManager manager = getFragmentManager();
        int currentCount = manager.getBackStackEntryCount();
        if (currentCount > 0) {
            String currentName = manager.getBackStackEntryAt(currentCount-1).getName();

            // Pop the last fragment transaction off of the stack
            // and change all the overlaying controls into their correct state
            getFragmentManager().popBackStack();
            if (DeviceListFragment.class.getName().equals(currentName)) {
                findViewById(R.id.deviceListLayout).setVisibility(View.GONE);
                setEditMode(false);
                animateEditButton(true);
            }
            else if (AreaDetailFragment.class.getName().equals(currentName)){

                if (currentCount == 1) {
                    createAreaList();
                }

                requestKitchenLoad();
                animateFabAlpha(true);
                animateBubbleBar(true);
                animateSettingsIcon(true);
                animateEditButton(false);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDeviceAddRequested(Device d) {
        // Send a message to the business logic that the device should be added
        AddDeviceToAreaCommand command = new AddDeviceToAreaCommand();
        command.setKitchenArea(currentArea);
        command.setDevice(d);
        postMessage(command);
    }

    @Override
    public void devicePositionChanged(KitchenArea area, String deviceId, int x, int y) {
        // Send a message to the business logic that the device should be moved
        ChangeDevicePositionCommand command = new ChangeDevicePositionCommand(new Point(x,y), area.getKitchenId(), area.getRelativeId(), deviceId);
        postMessage(command);
    }

    @Override
    public void deviceDeleted(KitchenArea area, String deviceId) {
        // Device should be removed from the kitchen area
        DeleteDeviceFromAreaCommand command = new DeleteDeviceFromAreaCommand(area.getKitchenId(), area.getRelativeId(), deviceId);
        postMessage(command);
    }
}