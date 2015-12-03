package ch.fluxron.fluxronapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;

import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.ui.activities.ApplicationSettingsActivity;
import ch.fluxron.fluxronapp.ui.activities.CreateKitchenActivity;
import ch.fluxron.fluxronapp.ui.activities.KitchenActivity;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.IKitchenClickListener;
import ch.fluxron.fluxronapp.ui.adapters.KitchenListAdapter;

/**
 * Main Activity of the activity, gets called when the app is started
 */
public class MainActivity extends FluxronBaseActivity implements IKitchenClickListener {

    private KitchenListAdapter listAdapter;
    private String searchConnection = "";

    /**
     * Called when a new isntance of this activity is created
     * @param savedInstanceState State that was saved
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register events for text changed so the search can immediately start on typing
        EditText kitchenSearchField = (EditText) findViewById(R.id.kitchenName);
        kitchenSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendSearchMessage();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        // Set the ListView's properties
        RecyclerView kitchenListView = (RecyclerView) findViewById(R.id.kitchenList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        kitchenListView.setLayoutManager(layoutManager);

        RecyclerView.ItemAnimator animator = kitchenListView.getItemAnimator();
        animator.setAddDuration(250);
        animator.setChangeDuration(250);
        animator.setMoveDuration(250);
        animator.setRemoveDuration(250);

        listAdapter = new KitchenListAdapter(this, this.busProvider);
        kitchenListView.setAdapter(listAdapter);
    }

    /**
     * Start of the activity
     */
    @Override
    public void onStart() {
        super.onStart();

        // Refresh the list with an empty search query
        listAdapter.clear();
        searchConnection = postMessage(new FindKitchenCommand(""));
    }

    /**
     * Sends a search message to find by name
     */
    public void sendSearchMessage(){
        String searchQuery = ((TextView)findViewById(R.id.kitchenName)).getText().toString();
        FindKitchenCommand cmd = new FindKitchenCommand(searchQuery);
        listAdapter.clear();
        searchConnection = postMessage(cmd);
    }

    /**
     * Navigates to the creation of a kitchen
     * @param btn Button that requested the navigation
     */
    public void navigateCreate(View btn){
        Intent startOther = new Intent(this, CreateKitchenActivity.class);
        startActivity(startOther);
    }

    /**
     * A kitchen was loaded
     * @param msg Message that contains the kitchen
     */
    public void onEventMainThread(KitchenLoaded msg){
        if (msg.getConnectionId().equals(searchConnection)) {
            listAdapter.addOrUpdate(msg.getKitchen());
        }
    }

    /**
     * Settings button was clicked
     * @param view Button that was clicked
     */
    public void onSettingsButtonClicked(View view){
        Intent startOther = new Intent(this, ApplicationSettingsActivity.class);
        startActivity(startOther);
    }

    /**
     * A kitchen was clicked and should be displayed
     * @param k Kitchen to navigate to
     */
    @Override
    public void kitchenClicked(Kitchen k) {
        Intent startOther = new Intent(this, KitchenActivity.class);
        startOther.putExtra(KitchenActivity.PARAM_KITCHEN_ID, k.getId());
        startActivity(startOther);
    }
}
