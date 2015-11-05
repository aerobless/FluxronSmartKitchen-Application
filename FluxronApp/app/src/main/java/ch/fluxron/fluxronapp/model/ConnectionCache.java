package ch.fluxron.fluxronapp.model;

import android.util.LruCache;

import ch.fluxron.fluxronapp.data.BTConnectionThread;

/**
 * Caches active connections. Evicted connections are ended properly.
 */
public class ConnectionCache extends LruCache<String, BTConnectionThread> {

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public ConnectionCache(int maxSize) {
        super(maxSize);
    }

    /**
     * The evicted connection is ended properly.
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, String key, BTConnectionThread oldValue, BTConnectionThread newValue) {
        oldValue.end();
    }
}
