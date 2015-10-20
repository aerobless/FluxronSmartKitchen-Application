package ch.fluxron.fluxronapp.events.base;

/**
 * Base class for providing a synchronous callback.
 */
public abstract class SynchronousReplyEvent {
    private Runnable callback;

    public SynchronousReplyEvent(Runnable callback){
        this.callback = callback;
    }

    /**
     * Run the specified callback
     */
    public void runCallback(){
        if (callback !=null) callback.run();
    }
}
