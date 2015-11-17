package ch.fluxron.fluxronapp.events.modelUi;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * A short message to be displayed on top of the UI.
 */
public class ToastProduced extends RequestResponseConnection {
    String message;

    public ToastProduced(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
