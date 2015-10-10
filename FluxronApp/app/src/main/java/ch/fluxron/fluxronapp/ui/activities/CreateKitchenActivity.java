package ch.fluxron.fluxronapp.ui.activities;

import android.content.Intent;
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

    public void onBackButtonClicked(View button){
        // User cancelled the process
        finish();
    }

    public void createNewKitchen(View button){
        /*TextView nameText = (TextView)findViewById(R.id.editTextName);
        TextView descText = (TextView)findViewById(R.id.editTextName);
        String name = nameText.getText().toString();
        String description = descText.getText().toString();

        Kitchen k = new Kitchen(name);
        k.setDescription(description);

        SaveKitchenCommand command = new SaveKitchenCommand();
        command.setKitchen(k);

        busProvider.getUiEventBus().post(command);*/

        //TODO: Wait for a response of type KitchenSaved
        // Edit this device
        Intent editDevice = new Intent(this, KitchenActivity.class);
        editDevice.putExtra("KITCHEN_ID", "xxx-dsf-er22-34234-d00");
        startActivity(editDevice);
        finish();
    }
}
