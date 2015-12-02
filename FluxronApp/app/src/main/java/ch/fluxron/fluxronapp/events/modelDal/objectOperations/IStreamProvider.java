package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import java.io.InputStream;

/**
 * Provides access to an input stream
 */
public interface IStreamProvider {
    /**
     * Opens and returns the stream
     * @return Open stream
     */
    InputStream openStream();
}
