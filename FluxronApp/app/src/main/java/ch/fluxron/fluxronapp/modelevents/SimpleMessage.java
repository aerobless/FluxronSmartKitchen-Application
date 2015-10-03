package ch.fluxron.fluxronapp.modelevents;

/**
 * A simple message containing a text. PROTOTYPE USAGE ONLY!
 */
public class SimpleMessage {
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    private String messageText;
}
