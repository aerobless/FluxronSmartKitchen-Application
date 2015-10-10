package ch.fluxron.fluxronapp.data;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.View;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.Map;

import ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelDal.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelDal.SaveObjectCommand;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

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
                //TODO: change type to constant
                properties.put("type",cmd.getData().getClass().getCanonicalName());
                doc.putProperties(properties);

            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Triggered when a find kitchen command is issued
     * @param cmd Command
     */
    public void onEventAsync(FindKitchenCommand cmd) {
        retrieveKitchens(cmd.getQuery());
    }

    /**
     * Reads all the kitchens containing the query text in their name
     * @param query Query, ignored if null
     */
    public void retrieveKitchens(String query){
        View kitchenList = database.getView("objectsByType");
        if (kitchenList != null) {
            kitchenList.setMap(
                    new Mapper() {
                        @Override
                        public void map(Map<String, Object> document, Emitter emitter) {
                            emitter.emit((Object)document.get("type"), null);
                        }
                    }, "1"
            );
        }

        kitchenList = database.getView("objectsByType");
        Query orderedQuery = kitchenList.createQuery();
        orderedQuery.setStartKey("ch.fluxron.fluxronapp.objectBase.Kitchen");
        orderedQuery.setEndKey("ch.fluxron.fluxronapp.objectBase.Kitchen");
        QueryEnumerator results;

        try {
            results = orderedQuery.run();
            for (Iterator<QueryRow> it = results; it.hasNext();) {
                QueryRow row = it.next();

                String kitchenName = String.valueOf(row.getDocument().getProperty("name"));
                String kitchenId = String.valueOf(row.getDocumentId());
                String kitchenDescription = String.valueOf(row.getDocument().getProperty("description"));

                if(query == null || kitchenName.contains(query)) {
                    
                    Kitchen data = new Kitchen(kitchenId, kitchenName);
                    data.setDescription(kitchenDescription);
                    provider.getDalEventBus().post(new KitchenLoaded(data));
                }
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
