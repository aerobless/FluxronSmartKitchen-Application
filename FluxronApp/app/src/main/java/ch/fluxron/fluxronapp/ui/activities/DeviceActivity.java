package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ch.fluxron.fluxronapp.R;


public class DeviceActivity extends AppCompatActivity {
    ch.fluxron.fluxronapp.ui.util.IEventBusProvider busProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
    }

    @Override
    public void onStart() {
        super.onStart();

        busProvider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider)getApplication();
        busProvider.getUiEventBus().register(this);
    }

    @Override
    public void onStop() {
        busProvider.getUiEventBus().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(Object msg){
        busProvider.getUiEventBus();
    }

    public void onBackButtonClicked(View button){
        // Close this activity and navigate back to the activity
        // that is below on the stack.
        finish();
    }
}
