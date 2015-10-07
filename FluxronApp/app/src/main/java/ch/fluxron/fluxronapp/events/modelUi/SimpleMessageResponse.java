package ch.fluxron.fluxronapp.events.modelUi;

/**
 * A simple message containing a text. PROTOTYPE USAGE ONLY!
 */
public class SimpleMessageResponse {
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    private String messageText;
}
