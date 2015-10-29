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
import ch.fluxron.fluxronapp.ui.activities.CreateKitchenActivity;
import ch.fluxron.fluxronapp.ui.activities.DeviceListActivity;
import ch.fluxron.fluxronapp.ui.activities.KitchenActivity;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.IKitchenClickListener;
import ch.fluxron.fluxronapp.ui.adapters.KitchenListAdapter;

public class MainActivity extends FluxronBaseActivity implements IKitchenClickListener {

    private KitchenListAdapter listAdapter;
    private String searchConnection = "";

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

        listAdapter = new KitchenListAdapter(this, this.busProvider);
        kitchenListView.setAdapter(listAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Refresh the list with an empty search query
        listAdapter.clear();
        searchConnection = postMessage(new FindKitchenCommand(""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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

    public void sendBluetoothTestMessage(View btn){
        Intent startOther = new Intent(this, DeviceListActivity.class);
        startActivity(startOther);
        //postMessage(new BluetoothTestCommand());
    }

    public void sendSearchMessage(){
        String searchQuery = ((TextView)findViewById(R.id.kitchenName)).getText().toString();
        FindKitchenCommand cmd = new FindKitchenCommand(searchQuery);
        listAdapter.clear();
        searchConnection = postMessage(cmd);
    }

    public void navigateCreate(View btn){
        Intent startOther = new Intent(this, CreateKitchenActivity.class);
        startActivity(startOther);
    }

    public void onEventMainThread(KitchenLoaded msg){
        if (msg.getConnectionId().equals(searchConnection)) {
            listAdapter.addOrUpdate(msg.getKitchen());
        }
    }

    @Override
    public void kitchenClicked(Kitchen k) {
        Intent startOther = new Intent(this, KitchenActivity.class);
        startOther.putExtra(KitchenActivity.PARAM_KITCHEN_ID, k.getId());
        startActivity(startOther);
    }
}
