package ch.fluxron.fluxronapp.events.modelUi.importExportOperations;

import android.net.Uri;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Notifies the finalization of a kitchen export
 */
public class KitchenExported extends RequestResponseConnection{
    private Uri location;

    /**
     * Specifies the location of the exported file
     * @param location Location of the exported file
     */
    public KitchenExported(Uri location) {
        this.location = location;
    }

    /**
     * Gets the location of the exported file
     * @return Location of the exported file
     */
    public Uri getLocation() {
        return location;
    }
}
