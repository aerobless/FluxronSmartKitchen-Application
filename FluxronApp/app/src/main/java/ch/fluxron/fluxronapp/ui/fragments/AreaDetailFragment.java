package ch.fluxron.fluxronapp.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ch.fluxron.fluxronapp.events.modelUi.ImageLoaded;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.DevicePositionChanged;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.KitchenAreaLoaded;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadImageFromKitchenCommand;
import ch.fluxron.fluxronapp.events.modelUi.kitchenOperations.LoadKitchenAreaCommand;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.objectBase.DevicePosition;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;
import ch.fluxron.fluxronapp.ui.activities.KitchenActivity;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.components.DeviceView;
import ch.fluxron.fluxronapp.ui.components.KitchenAreaDisplay;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Implements a fragment that displays a single kitchen area
 */
public class AreaDetailFragment extends Fragment {
    public static final String KITCHEN_ID = "kitchen_id";
    public static final String AREA_RELATIVE_ID = "area_id";

    private String kitchenId;
    private int areaId;
    private IEventBusProvider provider;
    private KitchenAreaDisplay display;
    private KitchenArea kitchenArea;
    private String imageLoadConnection;
    private KitchenActivity areaListener;
    private String areaLoadConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            kitchenId = savedInstanceState.getString(KITCHEN_ID);
            areaId = savedInstanceState.getInt(AREA_RELATIVE_ID);
        } else if (getArguments() != null) {
            kitchenId = getArguments().getString(KITCHEN_ID);
            areaId = getArguments().getInt(AREA_RELATIVE_ID);
        }
    }

    public void setEventBusProvider(IEventBusProvider provider) {
        this.provider = provider;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (provider == null) {
            provider = ((FluxronBaseActivity) getActivity()).getBusProvider();
        }

        provider.getUiEventBus().register(this);

        // Request the load of a kitchen area
        LoadKitchenAreaCommand cmd = new LoadKitchenAreaCommand(kitchenId, areaId);
        areaLoadConnection = cmd.getConnectionId();
        provider.getUiEventBus().post(cmd);
    }

    public void onEventMainThread(KitchenAreaLoaded msg) {
        if (msg.getConnectionId().equals(areaLoadConnection)) {
            this.kitchenArea = msg.getArea();

            // request the full size image
            LoadImageFromKitchenCommand cmd = new LoadImageFromKitchenCommand(kitchenArea.getKitchenId(), kitchenArea.getImageName());
            cmd.setImageSize(null); // no size limit
            this.imageLoadConnection = cmd.getConnectionId();
            provider.getUiEventBus().post(cmd);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        provider.getUiEventBus().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KITCHEN_ID, kitchenId);
        outState.putInt(AREA_RELATIVE_ID, areaId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        display = new KitchenAreaDisplay(getActivity());
        display.setListener(this.areaListener);
        return display;
    }

    public void onEventMainThread(ImageLoaded msg){
        if (msg.getConnectionId().equals(imageLoadConnection)){
            // Set the bitmap to the display
            display.setBitmap(msg.getBmp());

            // Set the device positions to the display
            display.setDevicePositions(kitchenArea);
        }
    }

    public void onEventMainThread(DevicePositionChanged msg) {
        // Check if we handle this kitchen and this area at the moment
        if (msg.getKitchenId().equals(kitchenId) && kitchenArea!= null && kitchenArea.getRelativeId() == msg.getAreaId()) {
            display.setDevicePosition(msg.getPosition());
        }
    }

    public void onEventMainThread(DeviceChanged msg){
        Device device = msg.getDevice();
        List<DevicePosition> positionList = kitchenArea.getDevicePositionList();
        for(DevicePosition pos:positionList){
            if(device.getAddress().equals(pos.getDeviceId())){
                pos.setCategory(device.getDeviceType());
                display.setDevicePosition(pos, DeviceView.DEVICE_STATUS_OK);
            }
        }
    }

    //TODO: update when failure

    public void setKitchenArea(KitchenArea kitchenArea) {
        this.kitchenArea = kitchenArea;
    }

    /**
     * Sets whether this area is in edit mode or not
     * @param edit Edit mode
     */
    public void setEditMode(boolean edit){
        display.setEditMode(edit);
    }

    public void setAreaListener(KitchenActivity areaListener) {
        this.areaListener = areaListener;
    }

    /**
     * Removes the device
     * @param deviceId Id of the device
     */
    public void removeDevice(String deviceId) {
        display.removePosition(deviceId);
    }
}
