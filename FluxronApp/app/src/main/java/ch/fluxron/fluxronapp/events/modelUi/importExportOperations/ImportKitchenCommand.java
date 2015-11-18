package ch.fluxron.fluxronapp.events.modelUi.importExportOperations;

import android.net.Uri;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests the import of a kitchen from a URI
 */
public class ImportKitchenCommand extends RequestResponseConnection {
    private Uri location;

    /**
     * Sets the file location for this import request
     * @param location Location of the file
     */
    public ImportKitchenCommand(Uri location) {
        this.location = location;
    }

    /**
     * Returns the location of the file for this import request
     * @return Location of the file
     */
    public Uri getLocation() {
        return location;
    }
}
