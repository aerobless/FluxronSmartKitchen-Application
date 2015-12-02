package ch.fluxron.fluxronapp.events.base;

/**
 * Base class for providing a synchronous callback.
 */
public abstract class SynchronousReplyEvent<T> {
    private ITypedCallback<T> callback;

    /**
     * Creates a new synchronous reply
     * @param callback Callback function that should be called on command completion
     */
    public SynchronousReplyEvent(ITypedCallback<T> callback){
        this.callback = callback;
    }

    /**
     * Run the specified callback
     */
    public void notifyCompletion(T result){
        if (callback !=null){
            callback.call(result);
        }
    }
}
