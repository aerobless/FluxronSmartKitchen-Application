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
     * Progress changed
     * @param total Total step count
     * @param current Current step
     */
    public ImportProgressChanged(int total, int current) {
        this.total = total;
        this.current = current;
    }

    /**
     * Returns the total step count
     * @return Total step count
     */
    public int getTotal() {
        return total;
    }

    /**
     * Gets the current step index
     * @return Step index
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Returns the id of the object
     * @return Id of the object
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
