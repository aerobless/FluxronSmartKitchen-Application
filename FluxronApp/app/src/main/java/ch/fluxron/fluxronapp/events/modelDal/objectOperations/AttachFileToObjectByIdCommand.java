package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import android.net.Uri;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests the storage of a file's content
 */
public class AttachFileToObjectByIdCommand extends RequestResponseConnection {
    private Uri fileUri;
    private String documentId;
    private String attachmentName;

    /**
     * Requests the attachment of a files content to a document
     * @param documentId Id of the document
     * @param file File URI
     * @param attachmentName Name that the newly created attachment should have, if this name
     *                       already exists, the existing attachment is overridden
     */
    public AttachFileToObjectByIdCommand(String documentId, Uri file, String attachmentName){
        this.documentId = documentId;
        this.fileUri = file;
        this.attachmentName = attachmentName;
    }

    /**
     * Returns the file URI
     * @return File URI
     */
    public Uri getFileUri() {
        return fileUri;
    }

    /**
     * Returns the document ID
     * @return Document ID
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Returns the attachment name
     * @return Name of the attachment
     */
    public String getAttachmentName() {
        return attachmentName;
    }
}
