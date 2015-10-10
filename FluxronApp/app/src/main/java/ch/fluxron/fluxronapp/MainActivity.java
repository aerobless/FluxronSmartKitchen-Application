package ch.fluxron.fluxronapp;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ch.fluxron.fluxronapp.events.modelDal.BluetoothDiscoveryResponse;
import ch.fluxron.fluxronapp.events.modelUi.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.KitchenLoaded;

import ch.fluxron.fluxronapp.events.modelUi.SimpleMessageResponse;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.ui.activities.CreateKitchenActivity;
import ch.fluxron.fluxronapp.ui.activities.KitchenActivity;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.KitchenListAdapter;

public class MainActivity extends FluxronBaseActivity {

    TextView textViewWidget;
    private KitchenListAdapter listAdapter;
    private ListView kitchenListView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewWidget = (TextView)this.findViewById(R.id.prototypeMessageList);
        kitchenListView = (ListView) findViewById(R.id.kitchenList);

        kitchenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onKitchenListItemClick(parent, view, position, id);
            }
        });

        // Set the ListView's adapter.
        listAdapter = new KitchenListAdapter(this);
        kitchenListView.setAdapter(listAdapter);

        //Bluetooth Discovery Prototyp
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(receiver, filter); // Don't forget to unregister during onDestroy
    }

    @Override
    public void onStart() {
        super.onStart();

        postMessage(new FindKitchenCommand());
    }

    private void onKitchenListItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent startOther = new Intent(this, KitchenActivity.class);
        startOther.putExtra(KitchenActivity.PARAM_KITCHEN_ID, ((Kitchen)listAdapter.getItem(position)).getId());
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

    public void sendTestMessage(View btn){
        String searchQuery = ((TextView)findViewById(R.id.kitchenName)).getText().toString();
        FindKitchenCommand cmd = new FindKitchenCommand();
        cmd.setQuery(searchQuery);
        listAdapter.clear();
        postMessage(cmd);

        //busProvider.getUiEventBus().post(new BluetoothDiscoveryRequest());
    }

    public void navigateCreate(View btn){
        Intent startOther = new Intent(this, CreateKitchenActivity.class);
        startActivity(startOther);
    }

    public void onEventMainThread(SimpleMessageResponse msg){
        textViewWidget.setText(msg.getMessageText());
    }

    public void onEventMainThread(BluetoothDiscoveryResponse msg){
        textViewWidget.setText(msg.getDeviceName() +" "+msg.getDeviceMAC());
        Log.d("FLUXRON.PROTOTYPE", msg.getDeviceName() + " " + msg.getDeviceMAC());
    }

    public void onEventMainThread(KitchenLoaded msg){
        Log.d("FLUXRON.PROTOTYPE", msg.getKitchen().getName());

        listAdapter.addOrUpdate(msg.getKitchen());
    }

    //Bluetooth Discovery Prototyp
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothDiscoveryResponse response = new BluetoothDiscoveryResponse(device.getName(), device.getAddress());
                postMessage(response);
            }
        }
    };
}
