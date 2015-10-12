package ch.fluxron.fluxronapp.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.SaveKitchenCommand;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;


public class CreateKitchenActivity extends FluxronBaseActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri tempFileName;

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
        TextView nameText = (TextView)findViewById(R.id.editTextName);
        TextView descText = (TextView)findViewById(R.id.editTextDescription);
        String name = nameText.getText().toString();
        String description = descText.getText().toString();

        Kitchen k = new Kitchen(name);
        k.setDescription(description);

        SaveKitchenCommand command = new SaveKitchenCommand(k);

        postMessage(command);

        //TODO: Wait for a response of type KitchenSaved
        // Edit this device
        Intent editDevice = new Intent(this, KitchenActivity.class);
        editDevice.putExtra("KITCHEN_ID", "xxx-dsf-er22-34234-d00");
        startActivity(editDevice);
        finish();
    }

    public void onCreatePictureClicked(View button) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempFileName = getImageFileUri();
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileName);
        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private Uri getImageFileUri(){
        // Get safe storage directory for photos
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "prototype" );
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) mediaStorageDir.mkdirs();

        // Return Uri from that file
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("restsat", "lsdajflkjsdflkasdf");
                Bitmap takenImage = BitmapFactory.decodeFile(tempFileName.getPath());
                takenImage = Bitmap.createScaledBitmap(takenImage, 100, 100, false );
                ImageView img = (ImageView)findViewById(R.id.imagePreview);
                img.setImageBitmap(takenImage);
            }
        }
    }
}