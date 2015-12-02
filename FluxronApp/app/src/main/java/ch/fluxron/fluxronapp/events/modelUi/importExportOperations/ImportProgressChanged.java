package ch.fluxron.fluxronapp.events.modelUi.importExportOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Notifies a progress change
 */
public class ImportProgressChanged extends RequestResponseConnection{
    private int total;
    private int current;
    private String objectId;

    /**
     * Notifies a progress change
     * @param total Total number of steps
     * @param current Current step
     */
    public ImportProgressChanged(int total, int current) {
        this.total = total;
        this.current = current;
    }

    /**
     * Returns the total number of steps
     * @return Total number of steps
     */
    public int getTotal() {
        return total;
    }

    /**
     * Gets the current step index
     * @return Current step index
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Returns the object id
     * @return Object id
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * Sets the object id
     * @param objectId Object id
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
