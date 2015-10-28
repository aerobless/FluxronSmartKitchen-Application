package ch.fluxron.fluxronapp.ui.activities.common;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    /**
     * Generates a temporary file name for image storage
     * @return File name with the pattern flx_img_yyyyMMdd_HHmmss
     */
    protected Uri getImageFileUri(){
        // Get safe storage directory for photos
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "flx_img_" );
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) mediaStorageDir.mkdirs();

        // Return Uri from that file
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg"));
    }
}
