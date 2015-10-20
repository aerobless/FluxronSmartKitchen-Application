package ch.fluxron.fluxronapp.model;

/**
 * A callback with a typed parameter
 */
public interface ITypedCallback<T> {
    void call(T value);
}
