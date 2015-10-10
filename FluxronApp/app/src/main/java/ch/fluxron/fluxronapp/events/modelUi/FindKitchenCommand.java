package ch.fluxron.fluxronapp.events.modelUi;

/**
 * Sends a command to load all kitchens matching a search term.
 */
public class FindKitchenCommand {
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
