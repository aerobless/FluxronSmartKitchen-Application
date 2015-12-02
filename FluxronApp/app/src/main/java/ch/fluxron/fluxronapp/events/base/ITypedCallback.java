package ch.fluxron.fluxronapp.events.base;

/**
 * A callback with a typed parameter
 */
public interface ITypedCallback<T> {
    /**
     * Callback function
     * @param value Callback parameter
     */
    void call(T value);
}
