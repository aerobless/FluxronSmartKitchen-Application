package ch.fluxron.fluxronapp.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.AttachImageToKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenCreated;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.SaveKitchenCommand;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;


public class CreateKitchenActivity extends FluxronBaseActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String EXTRA_SAVED_FILEPATH = "path";
    private Uri tempFileName;
    private String saveRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_kitchen);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_SAVED_FILEPATH, tempFileName.getPath());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedPath =savedInstanceState.getString(EXTRA_SAVED_FILEPATH);
        if(savedPath != null) {
            this.tempFileName = Uri.parse(savedPath);
            loadImageToPreview();
        }
    }

    public void onEventMainThread(KitchenCreated msg){
        // if the kitchen loaded was based on our request
        if(msg.getConnectionId().equals(saveRequestId)){
            Intent editDevice = new Intent(this, KitchenActivity.class);
            editDevice.putExtra(KitchenActivity.PARAM_KITCHEN_ID, msg.getKitchen().getId());

            // Attach the image to the kitchen
            AttachImageToKitchenCommand attachCommand = new AttachImageToKitchenCommand(msg.getKitchen().getId(), tempFileName);
            postMessage(attachCommand);

            startActivity(editDevice);
            finish();
        }
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

        // Send a command and remember its connection id
        SaveKitchenCommand command = new SaveKitchenCommand(k);
        saveRequestId = command.getConnectionId();
        postMessage(command);
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
                loadImageToPreview();
            }
        }
    }

    private void loadImageToPreview() {
        ImageView img = (ImageView)findViewById(R.id.imagePreview);
        Bitmap takenImage = BitmapFactory.decodeFile(tempFileName.getPath());

        takenImage = Bitmap.createScaledBitmap(takenImage, 250, 250, false);
        img.setImageBitmap(takenImage);
    }
}