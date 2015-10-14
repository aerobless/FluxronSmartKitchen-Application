package ch.fluxron.fluxronapp.ui.activities.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;
/**
 * Base class for all activities in the Fluxron tool app. Provides common functions like finding the
 * message bus and message posting.
 */
public class FluxronBaseActivity extends AppCompatActivity{
    protected IEventBusProvider busProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        busProvider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider)getApplication();
    }

    /**
     * Is called whenever this activity is brought to the user
     */
    @Override
    public void onStart() {
        super.onStart();

        busProvider.getUiEventBus().register(this);
    }

    /**
     * Is called whenever this activity is hidden from the user
     */
    @Override
    public void onStop() {
        busProvider.getUiEventBus().unregister(this);
        super.onStop();
    }

    /**
     * Posts a message to the underlying event bus
     * @param msg Message to be posted, null messages will be ignored
     */
    protected void postMessage(Object msg){
        if(msg!=null) {
            busProvider.getUiEventBus().post(msg);
        }
    }

    /**
     * Sends a connection-based message and returns its connection identifier
     * @param msg Message to be sent
     * @return Connection identifier as a string
     */
    protected String postMessage(RequestResponseConnection msg){
        if (msg!=null) {
            String id = msg.getConnectionId();
            postMessage((Object) msg);
            return id;
        }
        return null;
    }
}
