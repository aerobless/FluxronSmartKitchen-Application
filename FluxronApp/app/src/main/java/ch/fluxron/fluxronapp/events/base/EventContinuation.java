package ch.fluxron.fluxronapp.events.base;

import de.greenrobot.event.EventBus;

/**
 * Enables waiting for specific events in a connection
 * to create event chains.
 */
public final class EventContinuation<EventType extends RequestResponseConnection> {
    private String connection;
    private ITypedCallback<EventType> callback;
    private EventBus bus;
    private Class<EventType> clazz;
    private EventContinuation next;

    /**
     * Can't be instanced outside of itself on purpose
     */
    private EventContinuation(){

    }

    /**
     * Message incoming, check for type and connection id
     * @param msg
     */
    public void onEventAsync(RequestResponseConnection msg){
        if(msg.getConnectionId().equals(connection) && clazz.isInstance(msg)){
            bus.unregister(this);
            //noinspection unchecked
            callback.call((EventType) msg);
        }
    }

    /**
     * Starts a new message chain
     * @param bus Event bus to work with
     * @param originalMessage Original message with its connection Id
     * @param typeClass Class of the expected message
     * @param continuation Continuation callback that is called when the expected message is received
     * @param <T> Type of the expected message. Must be a subtype of RequestResponseConnection
     * @return Continuation handler. Can be ignored.
     */
    public static <T extends RequestResponseConnection> EventContinuation<T> createEventChain(EventBus bus, RequestResponseConnection originalMessage, Class<T> typeClass, ITypedCallback<T> continuation) {
        EventContinuation<T> head = new EventContinuation<>();
        head.connection = originalMessage.getConnectionId();
        head.callback = continuation;
        head.bus = bus;
        head.clazz = typeClass;
        head.bus.register(head);
        head.next = null;

        return head;
    }
}
