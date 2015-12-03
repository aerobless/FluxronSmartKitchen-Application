package ch.fluxron.fluxronapp.events.modelUi;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * A short message to be displayed on top of the UI.
 */
public class ToastProduced extends RequestResponseConnection {
    String message;

    /**
     * Creates a new toast produced event
     * @param message Message text
     */
    public ToastProduced(String message) {
        this.message = message;
    }

    /**
     * Returns the message text
     * @return Message text
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message text
     * @param message Message text
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
