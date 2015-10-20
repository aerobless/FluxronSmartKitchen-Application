package ch.fluxron.fluxronapp.events.base;

/**
 * Base class for providing a synchronous callback.
 */
public abstract class SynchronousReplyEvent<T> {
    private ITypedCallback<T> callback;

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
