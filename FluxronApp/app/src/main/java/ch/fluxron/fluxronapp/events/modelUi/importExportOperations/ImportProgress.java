package ch.fluxron.fluxronapp.events.modelUi.importExportOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Notifies a progress change
 */
public class ImportProgress extends RequestResponseConnection{
    private int total;
    private int current;

    public ImportProgress(int total, int current) {
        this.total = total;
        this.current = current;
    }

    public int getTotal() {
        return total;
    }

    public int getCurrent() {
        return current;
    }
}
