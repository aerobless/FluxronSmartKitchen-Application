package ch.fluxron.fluxronapp.events.modelUi.importExportOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.model.FluxronManifest;

/**
 * Signals that metadata was loaded
 */
public class MetadataLoaded extends RequestResponseConnection {
    private FluxronManifest metadata;

    /**
     * Sets the loaded data
     * @param metadata Data
     */
    public MetadataLoaded(FluxronManifest metadata) {
        this.metadata = metadata;
    }

    /**
     * Returns the loaded metadata
     * @return Metadata
     */
    public FluxronManifest getMetadata() {
        return metadata;
    }
}
