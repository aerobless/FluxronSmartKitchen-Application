package ch.fluxron.fluxronapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;

import ch.fluxron.fluxronapp.data.Bluetooth;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothConnectCommand;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDeviceFound;
import ch.fluxron.fluxronapp.events.modelDal.bluetoothOperations.BluetoothDiscoveryCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.AttachFileToObjectById;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.DeleteObjectById;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.FileStreamReady;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.GetFileStreamFromAttachment;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.LoadObjectByIdCommand;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectCreated;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.ObjectLoaded;
import ch.fluxron.fluxronapp.events.modelDal.objectOperations.SaveObjectCommand;
import ch.fluxron.fluxronapp.events.modelUi.BluetoothTestCommand;
import ch.fluxron.fluxronapp.events.modelUi.ImageLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.AttachImageToKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DeleteKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.FindKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenCreated;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadImageFromKitchenCommand;
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
        cmd.setConnectionId(msg);
        provider.getDalEventBus().post(cmd);
    }

    public void onEventAsync(AttachImageToKitchenCommand msg) {
        AttachFileToObjectById cmd = new AttachFileToObjectById(msg.getId(), msg.getImagePath(), "mainPicture");
        cmd.setConnectionId(msg);
        provider.getDalEventBus().post(cmd);
    }

    public void onEventAsync(LoadImageFromKitchenCommand msg) {
        GetFileStreamFromAttachment cmd = new GetFileStreamFromAttachment(msg.getKitchenId(), msg.getImageName());
        cmd.setConnectionId(msg);
        provider.getDalEventBus().post(cmd);
    }

    public void onEventAsync(FileStreamReady msg){
        Bitmap bmp = BitmapFactory.decodeStream(msg.getStream());
        ImageLoaded event = new ImageLoaded(bmp);
        event.setConnectionId(msg);
        provider.getUiEventBus().post(event);
    }

    public void onEventAsync(BluetoothTestCommand msg){
        //provider.getDalEventBus().post(new BluetoothDiscoveryCommand(true));
        provider.getDalEventBus().post(new BluetoothConnectCommand(Bluetooth.FLX_GTZ_196_ADDRESS, Bluetooth.DEMO_MESSAGE));
        //provider.getDalEventBus().post(new BluetoothConnectCommand(Bluetooth.FLX_BAX_5206_ADDRESS, Bluetooth.SERIAL_NUMBER));
    }

    public void onEventAsync(BluetoothDeviceFound msg){
        //TODO: send to GUI
        Log.d("FLUXRON", "Got BluetoothDeviceFound Message: " + msg.getName() + " " + msg.getAddress());
    }

    public void onEventAsync(FindKitchenCommand msg) {
        ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand cmd = new ch.fluxron.fluxronapp.events.modelDal.FindKitchenCommand(msg.getQuery());
        cmd.setConnectionId(msg);

        provider.getDalEventBus().post(cmd);
    }

    public void onEventAsync(ObjectLoaded msg) {
        if (msg.getData() instanceof Kitchen) {
            KitchenLoaded event = new KitchenLoaded((Kitchen) msg.getData());
            event.setConnectionId(msg);
            provider.getUiEventBus().post(event);
        }
    }

    public void onEventAsync(ObjectCreated msg) {
        if (msg.getData() instanceof Kitchen) {
            KitchenCreated event = new KitchenCreated((Kitchen) msg.getData());
            event.setConnectionId(msg);
            provider.getUiEventBus().post(event);
        }
    }

    public void onEventAsync(LoadKitchenCommand cmd){
        LoadObjectByIdCommand dbCommand = new LoadObjectByIdCommand(cmd.getId());
        dbCommand.setConnectionId(cmd);
        provider.getDalEventBus().post(dbCommand);
    }

    public void onEventAsync(DeleteKitchenCommand msg) {
        DeleteObjectById delete = new DeleteObjectById(msg.getId());
        delete.setConnectionId(msg);
        provider.getDalEventBus().post(delete);
    }
}
