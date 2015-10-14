package ch.fluxron.fluxronapp.events.modelDal.objectOperations;

import android.net.Uri;

import java.io.File;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Requests the storage of a file's content
 */
public class AttachFileToObjectById extends RequestResponseConnection {
    private Uri fileUri;
    private String documentId;
    private String attachmentName;

    public AttachFileToObjectById(String documentId, Uri file, String attachmentName){
        this.documentId = documentId;
        this.fileUri = file;
        this.attachmentName = attachmentName;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getAttachmentName() {
        return attachmentName;
    }
}
