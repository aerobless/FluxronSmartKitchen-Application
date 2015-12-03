package ch.fluxron.fluxronapp.model;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * A in-memory cache for
 */
public class BitmapCache extends LruCache<String, Bitmap> {

    /**
     * @param maxSize Max size in Kbytes
     */
    public BitmapCache(int maxSize) {
        super(maxSize);
    }

    /**
     * Returns the size of the cache item
     * @param key Key
     * @param value Value
     * @return Size
     */
    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getByteCount() / 1024;
    }
}
