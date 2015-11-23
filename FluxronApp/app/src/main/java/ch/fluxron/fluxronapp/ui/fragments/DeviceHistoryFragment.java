package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceNotChanged;
import ch.fluxron.fluxronapp.ui.components.ParameterView;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;
import ch.fluxron.fluxronapp.ui.util.PercentageGroup;

public class DeviceHistoryFragment extends Fragment {
    IEventBusProvider provider;
    String deviceAddress;

    // General usage timers
    PercentageGroup uptimeGroup = new PercentageGroup();

    // Heatsink temperature level timers
    PercentageGroup heatsinkGroup = new PercentageGroup();

    // Glass temperature level timers
    PercentageGroup glassGroup = new PercentageGroup();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        provider.getUiEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        provider.getUiEventBus().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_device_history, container, false);
        provider = (IEventBusProvider) getContext().getApplicationContext();

        return deviceView;
    }

    public void setDeviceAddress(String address) {
        this.deviceAddress = address;
    }

    public void onEventMainThread(DeviceChanged inputMsg) {
        if (inputMsg.getDevice().getAddress().equals(deviceAddress)) {

            // Uptime
            updatePercentageGroupedControls(inputMsg, uptimeGroup
                    , new int[]{R.id.uptimePowerOnTime, R.id.uptimeWorkingTime}
                    , new int[]{R.id.powerOnTimeP, R.id.workingTimeP}
                    , new boolean[]{true, false});

            // Heatsink level
            updatePercentageGroupedControls(inputMsg, heatsinkGroup
                    , new int[]{R.id.heatsinkLevel1, R.id.heatsinkLevel2, R.id.heatsinkLevel3, R.id.heatsinkLevel4}
                    , new int[]{R.id.heatsinkLevel1P, R.id.heatsinkLevel2P, R.id.heatsinkLevel3P, R.id.heatsinkLevel4P}
                    , new boolean[]{true, true, true, true});

            // Glass level
            updatePercentageGroupedControls(inputMsg, glassGroup
                    , new int[]{R.id.glassLevel1, R.id.glassLevel2, R.id.glassLevel3, R.id.glassLevel4}
                    , new int[]{R.id.glassLevel1P, R.id.glassLevel2P, R.id.glassLevel3P, R.id.glassLevel4P}
                    , new boolean[]{true, true, true, true});
        }
    }

    public void onEventMainThread(DeviceNotChanged inputMsg) {
        if (inputMsg.getAddress().equals(deviceAddress)) {
            int[] parameterViewIds = new int[]{
                    R.id.uptimePowerOnTime, R.id.uptimeWorkingTime,
                    R.id.heatsinkLevel1, R.id.heatsinkLevel2, R.id.heatsinkLevel3, R.id.heatsinkLevel4,
                    R.id.glassLevel1, R.id.glassLevel2, R.id.glassLevel3, R.id.glassLevel4
            };
            for (int i = 0; i < parameterViewIds.length; i++) {
                ParameterView current = (ParameterView) getView().findViewById(parameterViewIds[i]);
                current.handleDeviceNotChanged(inputMsg);

            }
        }
    }

    private void updatePercentageGroupedControls(DeviceChanged updatedValues, PercentageGroup group, int[] parameterViewIds, int[] labelIds, boolean[] addToGroup) {
        if (getView() == null) return;

        // Mapping of parameterViewId to new value from updatedValues
        Map<Integer, Integer> values = new HashMap<>(parameterViewIds.length);

        // Add all the specified parameter values to the group
        // and update the displays
        for (int i = 0; i < parameterViewIds.length; i++) {
            ParameterView current = (ParameterView) getView().findViewById(parameterViewIds[i]);
            String newValue = current.handleDeviceChanged(updatedValues);

            if (newValue != null) {
                int timer = Integer.parseInt(newValue);
                if (addToGroup[i]) {
                    group.put(current.getParameter(), timer);
                }
                values.put(parameterViewIds[i], timer);
            }
        }

        // Update the labels
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(1);
        for (int l = 0; l < labelIds.length; l++) {
            TextView label = (TextView) getView().findViewById(labelIds[l]);
            if (values.containsKey(parameterViewIds[l])) {
                int value = values.get(parameterViewIds[l]);
                float percentage = group.getPercentageOfTotal(value);
                label.setText(format.format(percentage));
            } else {
                label.setText(format.format(0));
            }
        }
    }
}
