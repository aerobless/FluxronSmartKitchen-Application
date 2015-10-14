package ch.fluxron.fluxronapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import ch.fluxron.fluxronapp.events.modelUi.BluetoothTestCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;

import ch.fluxron.fluxronapp.events.modelUi.SimpleMessageResponse;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.ui.activities.CreateKitchenActivity;
import ch.fluxron.fluxronapp.ui.activities.KitchenActivity;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.KitchenListAdapter;

public class MainActivity extends FluxronBaseActivity {

    private KitchenListAdapter listAdapter;
    private ListView kitchenListView ;
    private String searchConnection = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kitchenListView = (ListView) findViewById(R.id.kitchenList);

        kitchenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onKitchenListItemClick(parent, view, position, id);
            }
        });

        // Register events for text changed so the search can immediately start on typing
        EditText kitchenSearchField = (EditText) findViewById(R.id.kitchenName);
        kitchenSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { sendSearchMessage(); }

            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });

        // Set the ListView's adapter.
        listAdapter = new KitchenListAdapter(this);
        kitchenListView.setAdapter(listAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Refresh the list with an empty search query
        listAdapter.clear();
        searchConnection = postMessage(new FindKitchenCommand(""));
    }

    private void onKitchenListItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent startOther = new Intent(this, KitchenActivity.class);
        startOther.putExtra(KitchenActivity.PARAM_KITCHEN_ID, ((Kitchen) listAdapter.getItem(position)).getId());
        startActivity(startOther);
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
        postMessage(new BluetoothTestCommand());
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
        Log.d("FLUXRON.PROTOTYPE", msg.getConnectionId() + " vs. " + searchConnection);

        if (msg.getConnectionId().equals(searchConnection)) {
            listAdapter.addOrUpdate(msg.getKitchen());
        }
    }
}
