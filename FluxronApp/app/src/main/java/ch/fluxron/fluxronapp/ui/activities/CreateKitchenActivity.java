package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ch.fluxron.fluxronapp.R;


public class CreateKitchenActivity extends AppCompatActivity {
    ch.fluxron.fluxronapp.ui.util.IEventBusProvider busProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_kitchen);

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
}
