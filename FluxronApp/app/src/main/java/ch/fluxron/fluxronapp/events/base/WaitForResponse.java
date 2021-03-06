package ch.fluxron.fluxronapp.events.base;

import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;

/**
 * Waits for the response to a message
 */
public class WaitForResponse<T extends RequestResponseConnection> {
    private String waitConnection;
    private EventBus bus;
    private Class<T> expectedResponse;
    private CountDownLatch latch = new CountDownLatch(1);
    private T responseMessage;
    private RequestResponseConnection originalMessage;

    /**
     * Posts the specified message and halts the current thread until a message of the specified
     * type is received. WARNING: This only works with events that are processed in onEventAsynch!
     * @param bus Event bus to operate on
     * @param msg Message that should be sent out
     * @param expectedResponse Expected response class, i.e. ResponseOK
     * @return Response message of type T
     */
    public T postAndWait(EventBus bus, RequestResponseConnection msg, Class<T> expectedResponse) {
        this.bus = bus;
        this.bus.register(this);
        this.waitConnection = msg.getConnectionId();
        this.expectedResponse = expectedResponse;
        this.originalMessage = msg;

        this.bus.post(msg);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return responseMessage;
    }

    /**
     * Called when a message that operates based on RequestResponseConnection is received
     * @param msg Message
     */
    @SuppressWarnings("unchecked")
    public void onEventAsync(RequestResponseConnection msg) {
        if (msg != originalMessage && waitConnection.equals(msg.getConnectionId()) && expectedResponse.isInstance(msg)) {
            this.responseMessage = (T)msg;
            unlockAndClear();
        }
    }

    /**
     * Unlocks the thread and clears up all references to avoid memory leaks
     */
    private void unlockAndClear() {
        this.bus.unregister(this);
        this.originalMessage = null;
        latch.countDown();
    }
}
