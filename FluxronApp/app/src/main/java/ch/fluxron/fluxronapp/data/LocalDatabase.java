package ch.fluxron.fluxronapp.data;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import ch.fluxron.fluxronapp.events.modelDal.SaveObjectCommand;

/**
 * Listens to eventbus messages. Stores data persistently.
 */
public class LocalDatabase {
    private IEventBusProvider provider;
    private Database database;

    public LocalDatabase(IEventBusProvider provider, Database database) {
        this.provider = provider;
        this.database = database;
        this.provider.getDalEventBus().register(this);
    }

    public void onEventAsync(SaveObjectCommand cmd) {
        Document doc;
        if(cmd.getDocumentId() == null){
            doc = database.createDocument();
        } else {
            doc = database.getExistingDocument(cmd.getDocumentId());
        }
        if(doc != null){
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> properties = mapper.convertValue(cmd.getData(), Map.class);
            try {
                doc.putProperties(properties);

                Document readDoc = database.getDocument(doc.getId());
                Log.d("FLUXRON", String.valueOf(readDoc.getProperties()));

            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }
}
