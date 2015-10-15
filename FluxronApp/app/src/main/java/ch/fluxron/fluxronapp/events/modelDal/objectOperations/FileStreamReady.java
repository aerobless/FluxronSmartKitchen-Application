package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import java.io.InputStream;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Notifies the subscribers about a stream being ready
 */
public class FileStreamReady extends RequestResponseConnection {
    private InputStream stream;

    /**
     * Sets the stream
     * @param stream Stream
     */
    public FileStreamReady(InputStream stream) {
        this.stream = stream;
    }

    /**
     * Gets the stream
     * @return The stream
     */
    public InputStream getStream() {
        return stream;
    }
}
