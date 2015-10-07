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

                Document readDoc = database.getDocument(doc.getId());
                //Log.d("FLUXRON", String.valueOf(readDoc.getProperties()));

                retrieveKitchens();

            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }
    }

    public void retrieveKitchens(){
        View kitchenList = database.getView("byObjectType");
        if (kitchenList != null) {
            kitchenList.setMap(
                    new Mapper() {
                        @Override
                        public void map(Map<String, Object> document,
                                        Emitter emitter) {
                    /* Emit data to matieralized view */
                            emitter.emit(
                                    (Object) document.get("type"), document.get("name"));
                        }
                    }, "1" /* The version number of the mapper... */
            );
        }
        kitchenList = database.getView("byObjectType");
        Query orderedQuery = kitchenList.createQuery();
        orderedQuery.setStartKey("ch.fluxron.fluxronapp.objectBase.Kitchen");
        orderedQuery.setEndKey("ch.fluxron.fluxronapp.objectBase.Kitchen");
        orderedQuery.setLimit(10);
        QueryEnumerator results = null;

        try {
            results = orderedQuery.run();
            for (Iterator<QueryRow> it = results; it.hasNext();) {
                QueryRow row = it.next();
                //String docId = String.valueOf(row.getDocument().getProperties().get("type"));
                String kitchenName = String.valueOf(row.getValue());
                String kitchenId = String.valueOf(row.getDocumentId());
                //Log.d("FLUXRON", kitchenName);
                provider.getDalEventBus().post(new KitchenLoaded(new Kitchen(kitchenId, kitchenName)));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
