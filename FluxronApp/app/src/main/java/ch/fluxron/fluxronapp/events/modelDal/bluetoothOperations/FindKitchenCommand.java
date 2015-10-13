package ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Sends a command to load all kitchens matching a search term.
 */
public class FindKitchenCommand extends RequestResponseConnection{
    private String query;

    /**
     * Returns the query
     * @return Query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query
     * @param query Query
     */
    public FindKitchenCommand(String query) {
        this.query = query;
    }
}
