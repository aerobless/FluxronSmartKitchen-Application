package ch.fluxron.fluxronapp.events.base;

/**
 * A callback with a typed parameter
 */
public interface ITypedCallback<T> {
    void call(T value);
}
