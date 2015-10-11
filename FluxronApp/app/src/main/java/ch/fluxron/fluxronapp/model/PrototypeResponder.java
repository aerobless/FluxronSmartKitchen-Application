package ch.fluxron.fluxronapp.model;

import ch.fluxron.fluxronapp.events.modelDal.BluetoothDiscoveryRequest;
import ch.fluxron.fluxronapp.events.modelDal.DeleteObjectById;
import ch.fluxron.fluxronapp.events.modelDal.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.BluetoothTestCommand;
import ch.fluxron.fluxronapp.events.modelUi.DeleteKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.LoadKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.SaveKitchenCommand;
import ch.fluxron.fluxronapp.objectBase.Kitchen;

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
    }

    public void onEventAsync(BluetoothTestCommand msg){
        provider.getDalEventBus().post(new BluetoothDiscoveryRequest());
    }

    public void onEventAsync(FindKitchenCommand msg) {
        ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand cmd = new ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand();
        cmd.setQuery(msg.getQuery());

        provider.getDalEventBus().post(cmd);
    }

    public void onEventAsync(ObjectLoaded msg) {
        if (msg.getData() instanceof Kitchen) {
            provider.getUiEventBus().post(new KitchenLoaded((Kitchen) msg.getData()));
        }
    }

    public void onEventAsync(LoadKitchenCommand cmd){
        LoadObjectByIdCommand dbCommand = new LoadObjectByIdCommand();
        dbCommand.setId(cmd.getId());
        provider.getDalEventBus().post(dbCommand);
    }

    public void onEventAsync(ch.fluxron.fluxronapp.events.modelDal.KitchenLoaded msg) {
        ch.fluxron.fluxronapp.events.modelUi.KitchenLoaded uiMsg = new ch.fluxron.fluxronapp.events.modelUi.KitchenLoaded(msg.getKitchen());
        provider.getUiEventBus().post(uiMsg);
    }

    public void onEventAsync(DeleteKitchenCommand msg) {
        provider.getDalEventBus().post(new DeleteObjectById(msg.getId()));
    }
}
