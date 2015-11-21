package ch.fluxron.fluxronapp.events.modelUi.importExportOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.model.FluxronManifest;

/**
 * Signals that metadata was loaded
 */
public class MetadataLoaded extends RequestResponseConnection {
    private FluxronManifest metadata;
    private boolean idCollision = false;

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

    /**
     * Is this Id already in the database
     * @return Collision found
     */
    public boolean isIdCollision() {
        return idCollision;
    }

    /**
     * Sets, wether this will be a collision or not
     * @param idCollision Collision
     */
    public void setIdCollision(boolean idCollision) {
        this.idCollision = idCollision;
    }
}
