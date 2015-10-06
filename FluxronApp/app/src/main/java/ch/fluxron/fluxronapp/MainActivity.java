package ch.fluxron.fluxronapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ch.fluxron.fluxronapp.eventsbase.IEventBusProvider;
import ch.fluxron.fluxronapp.modelevents.BluetoothDeviceListRequest;
import ch.fluxron.fluxronapp.modelevents.SimpleMessage;
import ch.fluxron.fluxronapp.modelevents.SimpleMessageResponse;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    TextView textViewWidget;
    IEventBusProvider busProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewWidget = (TextView)this.findViewById(R.id.prototypeMessageList);

        busProvider = (IEventBusProvider)getApplication();
        busProvider.getEventBus().register(this);
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
        busProvider.getEventBus().unregister(this);
        super.onStop();
    }

    public void sendTestMessage(View btn){
        SimpleMessage m = new SimpleMessage();
        m.setMessageText("test");
        busProvider.getEventBus().post(m);

        busProvider.getEventBus().post(new BluetoothDeviceListRequest());
    }

    public void onEventMainThread(SimpleMessageResponse msg){
        textViewWidget.setText(msg.getMessageText());
    }
}
