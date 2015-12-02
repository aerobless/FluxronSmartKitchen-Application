package ch.fluxron.fluxronapp.events.modelDal;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * A short message to be displayed on top of the UI.
 */
public class ToastProduced extends RequestResponseConnection{
    String message;

    /**
     * Notifies the requirement to display a toast
     * @param message Message to be displayed
     */
    public ToastProduced(String message) {
        this.message = message;
    }

    /**
     * Returns the message to be displayed
     * @return Message to be displayed
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message to be displayed
     * @param message Message to be displayed
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
