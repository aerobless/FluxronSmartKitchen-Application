package ch.fluxron.fluxronapp.model;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipOutputStream;

import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.ExportKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.ImportKitchenCommand;

/**
 * Manages import and export requests for kitchens
 */
public class ImportExportManager {
    private IEventBusProvider provider;

    /**
     * Sets the event bus this manager should be operating on
     * @param provider Event Bus Provider
     */
    public ImportExportManager(IEventBusProvider provider) {
        this.provider = provider;

        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
    }

    public void onEventAsync(ImportKitchenCommand msg) {

    }

    public void onEventAsync(ExportKitchenCommand msg) {
        // Storage directory
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "flx_export");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) mediaStorageDir.mkdirs();

        // Return Uri from that file
        File destinationFile = new File(mediaStorageDir.getPath() + File.separator + "test.fluxron");

        try {
            OutputStream outputStream = new FileOutputStream(destinationFile);
            ZipOutputStream zipFile = new ZipOutputStream(outputStream);
            zipFile.flush();
            zipFile.close();
        } catch (java.io.IOException e) {

        }
    }
}
