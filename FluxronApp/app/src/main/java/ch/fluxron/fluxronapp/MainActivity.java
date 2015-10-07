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
import android.widget.TextView;

import ch.fluxron.fluxronapp.eventsbase.IEventBusProvider;
import ch.fluxron.fluxronapp.modelevents.BluetoothDiscoveryRequest;
import ch.fluxron.fluxronapp.modelevents.BluetoothDiscoveryResponse;
import ch.fluxron.fluxronapp.modelevents.SimpleMessage;
import ch.fluxron.fluxronapp.modelevents.SimpleMessageResponse;

public class MainActivity extends AppCompatActivity {

    TextView textViewWidget;
    ch.fluxron.fluxronapp.ui.util.IEventBusProvider busProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewWidget = (TextView)this.findViewById(R.id.prototypeMessageList);

        busProvider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider)getApplication();
        busProvider.getUiEventBus().register(this);

        //Bluetooth Discovery Prototyp
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter); // Don't forget to unregister during onDestroy
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
        SimpleMessage m = new SimpleMessage();
        m.setMessageText("test");
        busProvider.getUiEventBus().post(m);

        busProvider.getUiEventBus().post(new BluetoothDiscoveryRequest());
    }

    public void onEventMainThread(SimpleMessageResponse msg){
        textViewWidget.setText(msg.getMessageText());
    }

    public void onEventMainThread(BluetoothDiscoveryResponse msg){
        textViewWidget.setText(msg.getDeviceName() +" "+msg.getDeviceMAC());
        Log.d("FLUXRON.PROTOTYPE", msg.getDeviceName() +" "+msg.getDeviceMAC());
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
