package ch.fluxron.fluxronapp.ui.activities.common;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.modelUi.ToastProduced;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;
/**
 * Base class for all activities in the Fluxron tool app. Provides common functions like finding the
 * message bus and message posting.
 */
public class FluxronBaseActivity extends AppCompatActivity{
    protected IEventBusProvider busProvider;
    private static int ANIMATE_OUT_MILLISECONDS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
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
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "flx_img" );
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) mediaStorageDir.mkdirs();

        // Return Uri from that file
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg"));
    }

    public IEventBusProvider getBusProvider() {
        return busProvider;
    }

    public void animateFadeOut(final View v, final boolean setVisibility){
        v.animate().alpha(0).setDuration(ANIMATE_OUT_MILLISECONDS).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (setVisibility) v.setVisibility(View.GONE);
            }
        }).start();
    }

    public void animateFadeOut(final View v, final boolean setVisibility, int delay){
        v.animate().alpha(0).setDuration(ANIMATE_OUT_MILLISECONDS).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (setVisibility) v.setVisibility(View.GONE);
            }
        }).setStartDelay(delay).start();
    }

    public void animateFadeIn(final View v, final boolean setVisibility){
        if(setVisibility) v.setVisibility(View.VISIBLE);
        v.animate().alpha(1).setDuration(ANIMATE_OUT_MILLISECONDS).start();
    }

    public void animateFadeIn(final View v, final boolean setVisibility, final float targetAlpha){
        if(setVisibility) v.setVisibility(View.VISIBLE);
        v.animate().alpha(targetAlpha).setDuration(ANIMATE_OUT_MILLISECONDS).start();
    }

    /**
     * Listens to toast messages and displays them as overlay.
     * @param msg
     */
    public void onEventMainThread(ToastProduced msg) {
        showToast(msg.getMessage());
    }

    public void showToast(String message){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), message, duration);
        toast.show();
    }
}
