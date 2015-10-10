package ch.fluxron.fluxronapp.model;

import android.util.Log;

import ch.fluxron.fluxronapp.events.modelDal.BluetoothDiscoveryRequest;
import ch.fluxron.fluxronapp.events.modelDal.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.SaveKitchenCommand;

/**
 * Responds to a message. FOR PROTOTYPE USAGE ONLY!!!
 */
public class PrototypeResponder {

    private IEventBusProvider provider;

    public PrototypeResponder(IEventBusProvider provider) {
        this.provider = provider;
        provider.getDalEventBus().register(this);
        provider.getUiEventBus().register(this);
    }

    public void onEventAsync(SaveKitchenCommand msg) {
        SaveObjectCommand cmd = new SaveObjectCommand();
        cmd.setData(msg.getKitchen());
        cmd.setDocumentId(msg.getKitchen().getId());
        provider.getDalEventBus().post(cmd);

        //Temporary message to trigger the Bluetooth Module. Later there will be proper messages from BL to UI concerning Bluetooth.
        provider.getDalEventBus().post(new BluetoothDiscoveryRequest());
    }

    public void onEventAsync(FindKitchenCommand msg) {
        ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand cmd = new ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand();
        cmd.setQuery(msg.getQuery());

        provider.getDalEventBus().post(cmd);
    }

    public void onEventAsync(ch.fluxron.fluxronapp.events.modelDal.KitchenLoaded msg) {
        ch.fluxron.fluxronapp.events.modelUi.KitchenLoaded uiMsg = new ch.fluxron.fluxronapp.events.modelUi.KitchenLoaded(msg.getKitchen());
        provider.getUiEventBus().post(uiMsg);
    }
}
