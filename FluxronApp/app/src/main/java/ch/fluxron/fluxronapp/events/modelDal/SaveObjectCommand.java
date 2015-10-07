package ch.fluxron.fluxronapp.events.modelDal;

/**
 * A simple message containing a text. PROTOTYPE USAGE ONLY!
 */
public class SaveObjectCommand {
    private Object data;
    private String documentId;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
