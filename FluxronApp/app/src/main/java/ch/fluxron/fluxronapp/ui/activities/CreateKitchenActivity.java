package ch.fluxron.fluxronapp.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.base.ResponseOK;
import ch.fluxron.fluxronapp.events.modelUi.ValidationErrorOccurred;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.AttachImageToKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenCreated;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.SaveKitchenCommand;
import ch.fluxron.fluxronapp.objectBase.Kitchen;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;

/**
 * Creates a new kitchen
 */
public class CreateKitchenActivity extends FluxronBaseActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String EXTRA_SAVED_FILEPATH = "path";
    private final int ANIM_DURATION_OUT = 100;
    private final int ANIM_DURATION_IN = 300;
    private Uri tempFileName;
    private String saveRequestId;
    private String attachmentRequestId;
    private String kitchenId;

    /**
     * Creates this activity
     * @param savedInstanceState State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_kitchen);
    }

    /**
     * Saves the file name of the temp image
     * @param outState State
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(tempFileName != null) {
            outState.putString(EXTRA_SAVED_FILEPATH, tempFileName.getPath());
        }
    }

    /**
     * Restores the file name of the temp image from the state
     * @param savedInstanceState State
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedPath =savedInstanceState.getString(EXTRA_SAVED_FILEPATH);
        if(savedPath != null) {
            this.tempFileName = Uri.parse(savedPath);
            loadImageToPreview();
        }
    }

    /**
     * Validation failed, show message to the user
     * @param msg Message
     */
    public void onEventMainThread(ValidationErrorOccurred msg){
        if (msg.getConnectionId().equals(saveRequestId) || msg.getConnectionId().equals(this.attachmentRequestId)) {
            displayError(msg);
        }
    }

    /**
     * Animate the error view and scroll to it so the user can always see it
     * @param msg Error
     */
    private void displayError(ValidationErrorOccurred msg) {
        ScrollView scroller = (ScrollView)findViewById(R.id.createKitchenScroller);
        scroller.smoothScrollTo(0,0);

        TextView errorText = (TextView)findViewById(R.id.textViewError);
        errorText.setText(msg.getErrorMessageResourceId());
        errorText.animate().alpha(1).setDuration(ANIM_DURATION_IN);
    }

    /**
     * Kitchen created, save the image to it
     * @param msg Message
     */
    public void onEventMainThread(KitchenCreated msg){
        // if the kitchen loaded was based on our request
        if(msg.getConnectionId().equals(saveRequestId)){

            this.kitchenId = msg.getKitchen().getId();

            // Attach the image to the kitchen
            AttachImageToKitchenCommand attachCommand = new AttachImageToKitchenCommand(msg.getKitchen().getId(), tempFileName);
            this.attachmentRequestId = attachCommand.getConnectionId();
            postMessage(attachCommand);
        }
    }

    /**
     * Creation was successful
     * @param msg Message
     */
    public void onEventMainThread(ResponseOK msg) {
        if(msg.getConnectionId().equals(this.attachmentRequestId) && this.kitchenId!=null){
            Intent editKitchen = new Intent(this, KitchenActivity.class);
            editKitchen.putExtra(KitchenActivity.PARAM_KITCHEN_ID, kitchenId);

            startActivity(editKitchen);
            finish();
        }
    }

    /**
     * Send a command to create the new kitchen
     * @param button Button
     */
    public void createNewKitchen(View button){
        // Animate out the error view
        findViewById(R.id.textViewError).animate().alpha(0).setDuration(ANIM_DURATION_OUT);

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

    /**
     * Picture should be taken
     * @param button Button
     */
    public void onCreatePictureClicked(View button) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempFileName = getImageFileUri();
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileName);
        startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Picture was taken
     * @param requestCode Request code
     * @param resultCode Result code
     * @param data Data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                loadImageToPreview();
            }
        }
    }

    /**
     * Load the image and resize it to fit inside the the view
     */
    private void loadImageToPreview() {
        ImageView img = (ImageView)findViewById(R.id.imagePreview);
        View noImg = findViewById(R.id.noImageInformation);

        Bitmap takenImage = BitmapFactory.decodeFile(tempFileName.getPath());

        takenImage = Bitmap.createScaledBitmap(takenImage, 250, 250, false);
        img.setImageBitmap(takenImage);
        noImg.setVisibility(View.GONE);
    }
}