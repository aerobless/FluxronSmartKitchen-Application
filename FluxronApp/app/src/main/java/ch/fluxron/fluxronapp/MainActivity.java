package ch.fluxron.fluxronapp;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import ch.fluxron.fluxronapp.events.modelDal.BluetoothDiscoveryResponse;
import ch.fluxron.fluxronapp.events.modelUi.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.SaveKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.SimpleMessageResponse;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.ui.activities.CreateKitchenActivity;
import ch.fluxron.fluxronapp.ui.activities.KitchenActivity;

public class MainActivity extends AppCompatActivity {

    TextView textViewWidget;
    ch.fluxron.fluxronapp.ui.util.IEventBusProvider busProvider;
    private ArrayAdapter<String> listAdapter;
    private ListView kitchenListView ;
    private HashSet<String> kitchenIdentifiers = new HashSet<>();
    private ArrayList<String> kitchenList = new ArrayList<String>();

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

        //Bluetooth Discovery Prototyp
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(receiver, filter); // Don't forget to unregister during onDestroy
    }

    private void onKitchenListItemClick(AdapterView<?> parent, View view, int position, long id){
        Intent startOther = new Intent(this, KitchenActivity.class);
        startOther.putExtra("KITCHEN_ID", "sdlfjsdlkwoiewo--werwerlj");
        startActivity(startOther);
    }

    @Override
    protected void onStart() {
        super.onStart();

        busProvider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider)getApplication();
        busProvider.getUiEventBus().register(this);
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

    @Override
    public void onStop() {
        busProvider.getUiEventBus().unregister(this);
        super.onStop();
    }

    public void sendTestMessage(View btn){
        SaveKitchenCommand m = new SaveKitchenCommand();

        EditText kitName = (EditText) findViewById( R.id.kitchenName );
        m.setKitchen(new Kitchen(kitName.getText().toString()));
        busProvider.getUiEventBus().post(m);

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



        if (!kitchenIdentifiers.contains(msg.getKitchen().getId())) {
            kitchenList.add(msg.getKitchen().getName());
            kitchenIdentifiers.add(msg.getKitchen().getId());
        }
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, kitchenList);

        // Set the ArrayAdapter as the ListView's adapter.
        kitchenListView.setAdapter( listAdapter );
    }

    //Bluetooth Discovery Prototyp
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothDiscoveryResponse response = new BluetoothDiscoveryResponse(device.getName(), device.getAddress());
                busProvider.getUiEventBus().post(response);
            }
        }
    };
}
