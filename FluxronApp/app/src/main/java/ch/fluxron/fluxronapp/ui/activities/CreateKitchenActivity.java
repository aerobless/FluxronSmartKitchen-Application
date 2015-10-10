package ch.fluxron.fluxronapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;


public class CreateKitchenActivity extends FluxronBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_kitchen);
    }

    public void onEventMainThread(Object msg){
        // TODO: Proper event handling for createKitchen
        postMessage(null);
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
