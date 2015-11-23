package ch.fluxron.fluxronapp.events.modelUi.importExportOperations;

import android.content.ContentResolver;
import android.net.Uri;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests the import of a kitchen from a URI
 */
public class ImportKitchenCommand extends RequestResponseConnection {
    private Uri location;
    private ContentResolver resolver;

    /**
     * Sets the file location for this import request
     * @param location Location of the file
     */
    public ImportKitchenCommand(Uri location, ContentResolver resolver) {
        this.location = location;
        this.resolver = resolver;
    }

    /**
     * Returns the location of the file for this import request
     * @return Location of the file
     */
    public Uri getLocation() {
        return location;
    }

    /**
     * Gets the resolver
     * @return Resolver
     */
    public ContentResolver getResolver() {
        return resolver;
    }
}
