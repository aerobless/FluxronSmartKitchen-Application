package ch.fluxron.fluxronapp.data;

import com.couchbase.lite.Database;

import ch.fluxron.fluxronapp.eventsbase.IEventBusProvider;

/**
 * Listens to eventbus messages. Stores data persistently.
 */
public class LocalDatabase {
    private IEventBusProvider provider;
    private Database database;

    public LocalDatabase(IEventBusProvider provider, Database database) {
        this.provider = provider;
        this.database = database;
        this.provider.getEventBus().register(this);
    }


    public void onEventAsync(Object msg) {
    }
}
