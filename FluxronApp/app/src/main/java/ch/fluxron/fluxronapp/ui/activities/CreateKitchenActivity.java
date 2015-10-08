package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.SaveKitchenCommand;
import ch.fluxron.fluxronapp.objectBase.Kitchen;


public class CreateKitchenActivity extends AppCompatActivity {
    ch.fluxron.fluxronapp.ui.util.IEventBusProvider busProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_kitchen);
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

    public void createNewKitchen(View button){
        TextView nameText = (TextView)findViewById(R.id.editTextName);
        TextView descText = (TextView)findViewById(R.id.editTextName);
        String name = nameText.getText().toString();
        String description = descText.getText().toString();

        Kitchen k = new Kitchen(name);
        k.setDescription(description);

        SaveKitchenCommand command = new SaveKitchenCommand();
        command.setKitchen(k);

        busProvider.getUiEventBus().post(command);

        finish();//TODO: Wait for a response of type KitchenSaved
    }
}
