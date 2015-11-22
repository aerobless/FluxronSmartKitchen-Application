package ch.fluxron.fluxronapp.events.modelUi;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Notifies the event of a validation error
 */
public class ValidationErrorOccurred extends RequestResponseConnection {
    private int errorMessageResourceId;

    /**
     * Sets the error message
     * @param errorMessageResourceId Error message
     */
    public ValidationErrorOccurred(int errorMessageResourceId) {
        this.errorMessageResourceId = errorMessageResourceId;
    }

    /**
     * Gets the error message
     * @return Error message
     */
    public int getErrorMessageResourceId() {
        return errorMessageResourceId;
    }
}
