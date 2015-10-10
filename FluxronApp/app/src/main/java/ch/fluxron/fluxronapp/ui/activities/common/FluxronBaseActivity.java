package ch.fluxron.fluxronapp.ui.activities.common;

import android.support.v7.app.AppCompatActivity;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;
/**
 * Base class for all activities in the Fluxron tool app. Provides common functions like finding the
 * message bus and message posting.
 */
public class FluxronBaseActivity extends AppCompatActivity{
    private IEventBusProvider busProvider;

    /**
     * Is called whenever this activity is brought to the user
     */
    @Override
    public void onStart() {
        super.onStart();

        busProvider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider)getApplication();
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
}
