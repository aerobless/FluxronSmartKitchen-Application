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
 * Main activity of the application
 */
public class MainActivity extends FluxronBaseActivity implements IKitchenClickListener {

    private KitchenListAdapter listAdapter;
    private String searchConnection = "";

    /**
     * Activity created
     * @param savedInstanceState State
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
     * Activity started
     */
    @Override
    public void onStart() {
        super.onStart();

        // Refresh the list with an empty search query
        listAdapter.clear();
        searchConnection = postMessage(new FindKitchenCommand(""));
    }

    /**
     * Creates the option menu
     * @param menu Menu
     * @return Menu created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Option was selected
     * @param item Items
     * @return Success
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sends a search message
     */
    public void sendSearchMessage(){
        String searchQuery = ((TextView)findViewById(R.id.kitchenName)).getText().toString();
        FindKitchenCommand cmd = new FindKitchenCommand(searchQuery);
        listAdapter.clear();
        searchConnection = postMessage(cmd);
    }

    /**
     * Navigates to kitchen creation
     * @param btn Button
     */
    public void navigateCreate(View btn){
        Intent startOther = new Intent(this, CreateKitchenActivity.class);
        startActivity(startOther);
    }

    /**
     * Kitchen was loaded, add it to the list
     * @param msg Message
     */
    public void onEventMainThread(KitchenLoaded msg){
        if (msg.getConnectionId().equals(searchConnection)) {
            listAdapter.addOrUpdate(msg.getKitchen());
        }
    }

    /**
     * Settings button was clicked
     * @param view Settings button
     */
    public void onSettingsButtonClicked(View view){
        Intent startOther = new Intent(this, ApplicationSettingsActivity.class);
        startActivity(startOther);
    }

    /**
     * Kitchen was clicked, open it
     * @param k Kitchen
     */
    @Override
    public void kitchenClicked(Kitchen k) {
        Intent startOther = new Intent(this, KitchenActivity.class);
        startOther.putExtra(KitchenActivity.PARAM_KITCHEN_ID, k.getId());
        startActivity(startOther);
    }
}
