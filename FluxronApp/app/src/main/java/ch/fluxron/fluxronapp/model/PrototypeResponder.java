package ch.fluxron.fluxronapp.model;

import android.util.Log;

import ch.fluxron.fluxronapp.events.modelDal.BluetoothConnectCommand;
import ch.fluxron.fluxronapp.events.modelDal.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.DeleteObjectById;
import ch.fluxron.fluxronapp.events.modelDal.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.BluetoothTestCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.SaveKitchenCommand;
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
        //provider.getDalEventBus().post(new BluetoothDiscoveryCommand(true));
        byte[] message = new byte[]{
                (byte) 0x40, (byte) 0x18, (byte) 0x10, (byte) 0x04,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        provider.getDalEventBus().post(new BluetoothConnectCommand("00:13:04:12:06:20", message));
    }

    public void onEventAsync(BluetoothDeviceFound msg){
        //TODO: send to GUI
        Log.d("FLUXRON", "Got BluetoothDeviceFound Message: "+msg.getName()+" "+msg.getAddress());
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

    public void onEventAsync(DeleteKitchenCommand msg) {
        provider.getDalEventBus().post(new DeleteObjectById(msg.getId()));
    }
}
