package ch.fluxron.fluxronapp.events.modelDal;

/**
 * Sends a command to load all kitchens matching a search term.
 */
public class FindKitchenCommand {
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    private String query;
}
