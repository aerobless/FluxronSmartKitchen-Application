package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import ch.fluxron.fluxronapp.ui.fragments.common.DeviceBaseFragment;
import ch.fluxron.fluxronapp.ui.util.DeviceTypeConverter;
import ch.fluxron.fluxronapp.ui.util.PercentageGroup;

/**
 * Displays a devices usage counters
 */
public class DeviceHistoryFragment extends DeviceBaseFragment {
    private boolean ready = false;

    // General usage timers
    private PercentageGroup uptimeGroup = new PercentageGroup();

    // Heatsink temperature level timers
    private PercentageGroup heatsinkGroup = new PercentageGroup();

    // Glass temperature level timers
    private PercentageGroup glassGroup = new PercentageGroup();

    /**
     * Creates the view
     * @param inflater Inflater
     * @param container Container
     * @param savedInstanceState State
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View deviceView;
        if (savedInstanceState != null) {
            setDeviceAddress(savedInstanceState.getString(STATE_ADDRESS));
            setDeviceClass(savedInstanceState.getString(STATE_DEVICE_CLASS));
        }
        if (getDeviceClass().equals(DeviceTypeConverter.CCLASS)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_cclass_device_history, container, false);
            ready = true;
        } else if (getDeviceClass().equals(DeviceTypeConverter.SCLASS)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_sclass_device_history, container, false);
            ready = true;
        } else {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_unsupported_device, container, false);
        }
        return deviceView;
    }

    /**
     * Device was changed, update the display
     * @param inputMsg Message
     */
    public void onEventMainThread(DeviceChanged inputMsg) {
        if (inputMsg.getDevice().getAddress().equals(getDeviceAddress()) && ready) {

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

    /**
     * Device change failed, update all views
     * @param inputMsg Message
     */
    public void onEventMainThread(DeviceNotChanged inputMsg) {
        if (inputMsg.getAddress().equals(getDeviceAddress()) && ready) {
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

    /**
     * Updates a percentage group
     * @param updatedValues Device with the parameters
     * @param group Group to update
     * @param parameterViewIds Views that contain the parameter values
     * @param labelIds Labels with the percentages (in the same order as the parameter value views)
     * @param addToGroup Defines which values should be added to the groups total (in the same order as the parameter value views)
     */
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
