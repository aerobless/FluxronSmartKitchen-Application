package ch.fluxron.fluxronapp.events.modelUi.kitchenOperations;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Sends a command to load all kitchens matching a search term
 */
public class FindKitchenCommand extends RequestResponseConnection{
    private String query;

    /**
     * Sets the query
     * @param query Query
     */
    public FindKitchenCommand(String query) {
        this.query = query;
    }

    /**
     * Returns the query string
     * @return Query string
     */
    public String getQuery() {
        return query;
    }
}
