package ch.fluxron.fluxronapp.events.modelUi;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * A short message to be displayed on top of the UI.
 */
public class ToastProduced extends RequestResponseConnection {
    String message;

    /**
     * Instantiates a new ToastProduced event.
     *
     * @param message Message to display
     */
    public ToastProduced(String message) {
        this.message = message;
    }

    /**
     * Returns the message text
     *
     * @return Message to display
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message text
     *
     * @param message Message to display
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
