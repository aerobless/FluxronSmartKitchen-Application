package ch.fluxron.fluxronapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
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
import ch.fluxron.fluxronapp.ui.adapters.KitchenListAdapter;
import ch.fluxron.fluxronapp.ui.decorators.SpacesItemDecoration;


public class KitchenActivity extends FluxronBaseActivity {
    public static final String PARAM_KITCHEN_ID = "KITCHEN_ID";
    private String kitchenId;
    private AreaListAdapter listAdapter;

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

    @Override
    public void onStart() {
        super.onStart();

        // Request the load of the kitchen we are displaying
        postMessage(new LoadKitchenCommand(kitchenId));
    }

    public void onEventMainThread(KitchenLoaded msg){
        if(msg.getKitchen().getId().equals(kitchenId)) {
            // Set the name of the kitchen to the title text
            ((TextView) findViewById(R.id.kitchenNameTitle)).setText(msg.getKitchen().getName());

            // Fill the area adapter
            for(KitchenArea a : msg.getKitchen().getAreaList()) {
                listAdapter.addOrUpdate(a);
            }
        }
    }

    public void onBackButtonClicked(View button){
        // Close this kitchen and move back one view on the stack.
        finish();
    }

    public void onEditDeviceClicked(View button){
        // Edit this device
        Intent editDevice = new Intent(this, DeviceActivity.class);
        editDevice.putExtra("DEVICE_ID", "xxx-dsf-er22-34234-d00");
        startActivity(editDevice);
    }

    public void onDeleteButtonClicked(View button){
        // Send a deletion command
        postMessage(new DeleteKitchenCommand(kitchenId));
        finish();
    }
}
