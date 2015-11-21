package ch.fluxron.fluxronapp.model;

import android.net.Uri;
import android.os.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import ch.fluxron.fluxronapp.events.base.ITypedCallback;
import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;
import ch.fluxron.fluxronapp.events.base.WaitForResponse;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.AttachStreamToObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetAllAttachmentStreamsFromObjectCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.ExportKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.ImportKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.KitchenExported;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.LoadImportMetadata;
import ch.fluxron.fluxronapp.events.modelUi.importExportOperations.MetadataLoaded;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

/**
 * Manages import and export requests for kitchens
 */
public class ImportExportManager {
    private static String ENTRY_MANIFEST = "manifest.json";
    private static String ENTRY_KITCHEN = "kitchen.json";
    private List<String> reservedEntries;
    private IEventBusProvider provider;

    /**
     * Sets the event bus this manager should be operating on
     * @param provider Event Bus Provider
     */
    public ImportExportManager(IEventBusProvider provider) {
        this.provider = provider;

        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);

        reservedEntries = new ArrayList<>();
        reservedEntries.add(ENTRY_KITCHEN);
        reservedEntries.add(ENTRY_MANIFEST);
    }

    public void onEventAsync(ImportKitchenCommand msg) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Open the zip file
            ZipFile file = new ZipFile(msg.getLocation().getPath());

            // Load the manifest
            ZipEntry manifestEntry = file.getEntry(ENTRY_MANIFEST);
            FluxronManifest manifest = mapper.readValue(file.getInputStream(manifestEntry), FluxronManifest.class);

            if (!Kitchen.class.getName().equals(manifest.getObjectType())) {
                return;
            }

            // Create the object on the database
            ZipEntry kitchenEntry = file.getEntry(ENTRY_KITCHEN);
            Kitchen kitchen = mapper.readValue(file.getInputStream(kitchenEntry), Kitchen.class);

            SaveObjectCommand saveCommand = new SaveObjectCommand();
            saveCommand.setData(kitchen);
            saveCommand.setDocumentId(manifest.getObjectId());

            // Wait for the creation of the kitchen before we proceed
            WaitForResponse<RequestResponseConnection> waitForSave = new WaitForResponse<>();
            waitForSave.postAndWait(provider.getDalEventBus(), saveCommand, RequestResponseConnection.class);

            // Loop through all the other files
            // leave RESERVED_ENTRIES out
            Enumeration<? extends ZipEntry> entryList = file.entries();
            while (entryList.hasMoreElements()){
                ZipEntry attachmentEntry = entryList.nextElement();
                if (!reservedEntries.contains(attachmentEntry.getName())) {
                    // Copy attachment
                    AttachStreamToObjectByIdCommand attachCommand = new AttachStreamToObjectByIdCommand(manifest.getObjectId(), file.getInputStream(attachmentEntry), attachmentEntry.getName());

                    WaitForResponse<RequestResponseConnection> waitForAttachment = new WaitForResponse<>();
                    waitForAttachment.postAndWait(provider.getDalEventBus(), attachCommand, RequestResponseConnection.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEventAsync(LoadImportMetadata msg) {
        try {
            FluxronManifest manifest = getMetadataFromUri(msg.getLocation());

            MetadataLoaded loaded = new MetadataLoaded(manifest);
            loaded.setConnectionId(msg);
            provider.getUiEventBus().post(loaded);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FluxronManifest getMetadataFromUri(Uri location) throws IOException {
        ZipFile file = new ZipFile(location.getPath());
        ZipEntry entry = file.getEntry(ENTRY_MANIFEST);

        ObjectMapper mapper = new ObjectMapper();
        FluxronManifest parsed = mapper.readValue(file.getInputStream(entry), FluxronManifest.class);

        return parsed;
    }

    public void onEventAsync(final ExportKitchenCommand msg) {
        // Laden der Küche
        GetObjectByIdCommand cmd = new GetObjectByIdCommand(msg.getKitchenId(), new ITypedCallback<Object>() {
            @Override
            public void call(Object value) {
                if(value instanceof Kitchen){
                    exportKitchen((Kitchen)value, msg);
                }
            }
        });
        provider.getDalEventBus().post(cmd);
    }

    private void exportKitchen(final Kitchen kitchen, final ExportKitchenCommand msg) {
        // Laden der Attachmentstreams
        GetAllAttachmentStreamsFromObjectCommand cmd = new GetAllAttachmentStreamsFromObjectCommand(kitchen.getId(), new ITypedCallback<Map<String, InputStream>>() {
            @Override
            public void call(Map<String, InputStream> streams) {
                createKitchenZipFile(kitchen, msg, streams);
            }
        });
        provider.getDalEventBus().post(cmd);
    }

    private void createKitchenZipFile(Kitchen kitchen, ExportKitchenCommand msg, Map<String, InputStream> streams) {
        // Storage directory
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "flx_export");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) mediaStorageDir.mkdirs();

        // Return Uri from that file
        File destinationFile = new File(mediaStorageDir.getPath() + File.separator + "test.fluxron");

        try {
            OutputStream outputStream = new FileOutputStream(destinationFile);
            ZipOutputStream zipFile = new ZipOutputStream(outputStream);

            // Write the manifest file
            writeManifest(zipFile, kitchen);

            // Write the object
            writeObject(zipFile, kitchen);

            // write and close streams
            writeAnCloseAllStreams(zipFile, streams);

            zipFile.flush();
            zipFile.close();

            // Success, send a message
            KitchenExported event = new KitchenExported(Uri.fromFile(destinationFile));
            event.setConnectionId(msg);
            provider.getUiEventBus().post(event);
        } catch (java.io.IOException e) {

        }
    }

    private void writeAnCloseAllStreams(ZipOutputStream zipFile, Map<String, InputStream> streams) throws IOException {
        for(Map.Entry<String, InputStream> stream : streams.entrySet()) {
            ZipEntry entry = new ZipEntry(stream.getKey());
            zipFile.putNextEntry(entry);

            copyStreamToZip(stream.getValue(), zipFile);

            zipFile.closeEntry();
        }
    }

    private void copyStreamToZip(InputStream stream, ZipOutputStream zipFile) throws IOException {
        byte[] buffer = new byte[256];
        int bytesRead;

        while ((bytesRead = stream.read(buffer))>=0) {
            zipFile.write(buffer, 0, bytesRead);
        }

        stream.close();
    }

    private void writeObject(ZipOutputStream zipFile, Kitchen kitchen) throws IOException {
        ZipEntry entry = new ZipEntry(ENTRY_KITCHEN);
        zipFile.putNextEntry(entry);

        // Create converter
        ObjectMapper jsonConverter = new ObjectMapper();
        byte[] bytes = jsonConverter.writeValueAsBytes(kitchen);

        // save to zip file
        zipFile.write(bytes);

        zipFile.closeEntry();
    }

    private void writeManifest(ZipOutputStream zipFile, Kitchen kitchen) throws IOException {
        ZipEntry entry = new ZipEntry(ENTRY_MANIFEST);
        zipFile.putNextEntry(entry);

        // Create manifest
        FluxronManifest manifest = new FluxronManifest();
        manifest.setObjectName(kitchen.getName());
        manifest.setObjectDescription(kitchen.getDescription());
        manifest.setObjectType(Kitchen.class.getName());
        manifest.setObjectId(kitchen.getId());
        manifest.setSaveDate(new Date());

        // Create converter
        ObjectMapper jsonConverter = new ObjectMapper();
        byte[] bytes = jsonConverter.writeValueAsBytes(manifest);

        // save to zip file
        zipFile.write(bytes);

        zipFile.closeEntry();
    }
}
