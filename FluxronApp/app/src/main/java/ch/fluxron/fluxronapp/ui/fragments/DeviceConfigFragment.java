package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AccessGranted;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChangeCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceNotChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;
import ch.fluxron.fluxronapp.ui.components.ConfigurableScrollView;
import ch.fluxron.fluxronapp.ui.components.ParameterEditable;
import ch.fluxron.fluxronapp.ui.fragments.common.DeviceBaseFragment;
import ch.fluxron.fluxronapp.ui.util.CoilSetupConfigurator;
import ch.fluxron.fluxronapp.ui.util.DeviceTypeConverter;

/**
 * Fragment displaying the device configuration. Contains mostly ParameterEditables.
 */
public class DeviceConfigFragment extends DeviceBaseFragment {
    private List<ParameterEditable> parameters;
    private List<View> profiles;
    private boolean hasAdvancedConfiguration = false;
    private boolean hasBasicConfiguration = false;
    private CoilSetupConfigurator coilSetupConfigurator;
    private Spinner coilSetupSinner;
    CompoundButton.OnCheckedChangeListener checkedChangeListener;
    private Switch keepWarm;

    /**
     * Creates the view
     *
     * @param inflater           Inflater
     * @param container          Container
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
        parameters = new ArrayList<>();
        if (getDeviceClass().equals(DeviceTypeConverter.CCLASS)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_cclass_device_config, container, false);
            initAdvancecConfig(deviceView);
            initBasicConfig(deviceView);
        } else if (getDeviceClass().equals(DeviceTypeConverter.SCLASS)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_sclass_device_config, container, false);
            initAdvancecConfig(deviceView);
            initBasicConfig(deviceView);
        } else if (getDeviceClass().equals(DeviceTypeConverter.ETX)) {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_etx_device_config, container, false);
            initProfiles(deviceView);
        } else {
            deviceView = getActivity().getLayoutInflater().inflate(R.layout.fragment_unsupported_device, container, false);
        }
        return deviceView;
    }

    /**
     * Initializes the base views
     *
     * @param deviceView Device view
     */
    private void initBasicConfig(View deviceView) {
        parameters.add((ParameterEditable) deviceView.findViewById(R.id.powerMax));
        keepWarm = (Switch) deviceView.findViewById(R.id.keepWarmSwitch);
        String switchParam = keepWarm.getTag().toString();
        ((ch.fluxron.fluxronapp.ui.util.IEventBusProvider) getContext().getApplicationContext()).getUiEventBus().post(new RegisterParameterCommand(switchParam));
        checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    provider.getUiEventBus().post(new DeviceChangeCommand(getDeviceAddress(), new ParameterValue(keepWarm.getTag().toString(), "1")));
                } else {
                    provider.getUiEventBus().post(new DeviceChangeCommand(getDeviceAddress(), new ParameterValue(keepWarm.getTag().toString(), "0")));
                }
            }
        };
        keepWarm.setEnabled(false);
        keepWarm.setOnCheckedChangeListener(checkedChangeListener);

        coilSetupConfigurator = new CoilSetupConfigurator();
        coilSetupSinner = ((Spinner) deviceView.findViewById(R.id.coilSetupSpinner));
        coilSetupSinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selecteditem = coilSetupSinner.getItemAtPosition(position).toString();
                Log.d("Fluxron", selecteditem);
                coilSetupConfigurator.postConfiguration(selecteditem, getDeviceAddress(), getDeviceClass(), provider);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        hasBasicConfiguration = true;
    }

    /**
     * Initializes the profile views for thermostats (ETX)
     *
     * @param deviceView Root view
     */
    private void initProfiles(final View deviceView) {
        profiles = new ArrayList<>();
        LinearLayout layout = (LinearLayout) deviceView.findViewById(R.id.profiles);
        profiles.add(deviceView.findViewById(R.id.profile));
        profiles.add(cloneProfile(2));
        profiles.add(cloneProfile(3));
        profiles.add(cloneProfile(4));
        layout.addView(profiles.get(1));
        layout.addView(profiles.get(2));
        layout.addView(profiles.get(3));

        for (View profile : profiles) {
            profile.findViewById(R.id.profileHeader).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = ((TextView) v).getText().toString();
                    int profileNr = Integer.parseInt(id.substring(8, 9)) - 1;
                    closeAllProfiles();
                    profiles.get(profileNr).findViewById(R.id.editableViewList).setVisibility(View.VISIBLE);
                }
            });
        }

        closeAllProfiles();
        //TODO: open the currently active profile instead of profile 0
        profiles.get(0).findViewById(R.id.editableViewList).setVisibility(View.VISIBLE);

        ((ConfigurableScrollView) deviceView.findViewById(R.id.scrollView)).setScrollOffset(100);
    }

    /**
     * Closes all profile views
     */
    private void closeAllProfiles() {
        for (View profile : profiles) {
            profile.findViewById(R.id.editableViewList).setVisibility(View.GONE);
        }
    }

    /**
     * Clones a profile
     *
     * @param profileNumber Number of the profile
     * @return Cloned profile
     */
    @NonNull
    private View cloneProfile(int profileNumber) {
        View child = getActivity().getLayoutInflater().inflate(R.layout.fragment_etx_device_config, null);
        View clonedProfile = child.findViewById(R.id.profile);
        ((TextView) clonedProfile.findViewById(R.id.profileHeader)).setText(getResources().getText(R.string.etx_config_profile) + " " + profileNumber);
        ((ViewGroup) clonedProfile.getParent()).removeView(clonedProfile);

        parameters = new ArrayList<>();
        ViewGroup list = (ViewGroup) clonedProfile.findViewById(R.id.editableViewList);
        for (int i = 0; i < list.getChildCount(); i++) {
            ParameterEditable editable = (ParameterEditable) list.getChildAt(i);
            editable.setProfile(profileNumber);
            parameters.add(editable);
        }
        return clonedProfile;
    }

    /**
     * Initializes the advanced config view
     *
     * @param deviceView Root view
     */
    private void initAdvancecConfig(View deviceView) {
        ViewGroup list = (ViewGroup) deviceView.findViewById(R.id.editableViewList);
        for (int i = 0; i < list.getChildCount(); i++) {
            parameters.add((ParameterEditable) list.getChildAt(i));
        }
        ((ConfigurableScrollView) deviceView.findViewById(R.id.scrollView)).setScrollOffset(100);
        hasAdvancedConfiguration = true;
    }

    /**
     * Device was changed, update values
     *
     * @param inputMsg Message
     */
    public void onEventMainThread(DeviceChanged inputMsg) {
        if (inputMsg.getDevice().getAddress().equals(getDeviceAddress()) && hasAdvancedConfiguration) {
            for (ParameterEditable p : parameters) {
                p.setDeviceAddress(getDeviceAddress());
                p.handleDeviceChanged(inputMsg);
            }
        }
        if (inputMsg.getDevice().getAddress().equals(getDeviceAddress()) && hasBasicConfiguration) {
            handleBasicConfig(inputMsg);
        }
    }

    /**
     * Update the basic config values
     *
     * @param inputMsg Message
     */
    private void handleBasicConfig(DeviceChanged inputMsg) {
        ParameterValue dp = inputMsg.getDevice().getDeviceParameter(keepWarm.getTag().toString());
        if (dp != null) {
            keepWarm.setEnabled(true);
            keepWarm.setOnCheckedChangeListener(null);
            keepWarm.setChecked(Integer.parseInt(dp.getValue()) == 1);
            keepWarm.setOnCheckedChangeListener(checkedChangeListener);
        }
    }

    /**
     * Device value not available
     *
     * @param inputMsg Message
     */
    public void onEventMainThread(DeviceNotChanged inputMsg) {
        if (inputMsg.getAddress().equals(getDeviceAddress()) && hasAdvancedConfiguration) {
            for (ParameterEditable p : parameters) {
                p.setDeviceAddress(getDeviceAddress());
                p.handleDeviceNotChanged(inputMsg);
            }
        }
    }

    /**
     * User Access Control Level has changed
     *
     * @param inputMsg Message
     */
    public void onEventMainThread(AccessGranted inputMsg) {
        if (hasAdvancedConfiguration) {
            for (ParameterEditable p : parameters) {
                p.handleAccessLevel(inputMsg.getAccessLevel());
            }
        }
    }
}
