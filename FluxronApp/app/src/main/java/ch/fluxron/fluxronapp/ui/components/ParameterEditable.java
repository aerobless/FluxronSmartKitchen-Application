package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelUi.authenticationOperations.AccessCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChangeCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceNotChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.objectBase.AccessLevel;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Displays a editable parameter to the user with name and value.
 * Automatically notifies business layer to keep it updated with information about this parameter.
 * It doesn't however listen to changes itself, so the underlying fragment has to call setValue()
 * or use handleDeviceChanged().
 */
public class ParameterEditable extends LinearLayout {
    private ParamManager manager;
    private TypedArray arguments;
    private String parameter;
    private String measuringUnit;
    private TextView paramMeasurementUnit;
    private TextView paramNameSmall;
    private TextView paramNameBig;
    private TextView infoText;
    private boolean editMode;
    private EditText paramValue;
    private String lastParamValue = "";
    private String deviceAddress;
    private LinearLayout buttonPanel;
    private int requiredAccessLevel;

    private Button saveButton;
    private Button resetButton;
    private IEventBusProvider provider;

    public ParameterEditable(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.component_parameter_editable, this, true);
        manager = new ParamManager();

        arguments = context.obtainStyledAttributes(attrs, R.styleable.ParameterEditable);
        parameter = arguments.getString(R.styleable.ParameterEditable_editableParamName);
        measuringUnit = arguments.getString(R.styleable.ParameterEditable_editableMeasuringUnit);
        requiredAccessLevel = arguments.getInt(R.styleable.ParameterEditable_editableAccessLevel, 0); //If not specified, default is DEMO_USER = 0

        paramMeasurementUnit = (TextView) this.findViewById(R.id.paramMeasurementUnit);
        paramNameSmall = (TextView) this.findViewById(R.id.paramNameSmall);
        paramNameBig = (TextView) this.findViewById(R.id.paramNameBig);
        infoText = (TextView) this.findViewById(R.id.infoText);
        paramValue = (EditText) this.findViewById(R.id.paramValue);
        buttonPanel = (LinearLayout) this.findViewById(R.id.buttonPanel);
        saveButton = (Button) this.findViewById(R.id.saveButton);
        resetButton = (Button) this.findViewById(R.id.resetButton);

        setDisplayText();
        setInfoText();

        if (!isInEditMode()) {
            provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider) getContext().getApplicationContext();
            provider.getUiEventBus().post(new RegisterParameterCommand(parameter));
        }

        initOnFocusListener();

        initButtonListeners();

        if (requiredAccessLevel > 0) {
            /**
             * If the access level needed to display this control is greater then DEMO_USER (0) we
             * hide it until we get confirmation that the user is authenticated and has the required
             * access level.
             */
            setVisibility(GONE);
            post(new Runnable() {
                @Override
                public void run() {
                    provider.getUiEventBus().post(new AccessCommand());
                }
            });
        }
    }

    public void handleAccessLevel(AccessLevel accessLevel) {
        if (accessLevel.ordinal() >= requiredAccessLevel) {
            setVisibility(VISIBLE);
        }
    }

    private void initButtonListeners() {
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = paramValue.getText().toString();
                if (deviceAddress != null) {
                    provider.getUiEventBus().post(new DeviceChangeCommand(deviceAddress, new ParameterValue(parameter, value)));
                } else {
                    Log.d("FLUXRON", "ParameterEditable.deviceAddress hasn't been set. Are you attempting to save on a Fake Device?");
                }
            }
        });

        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paramValue.setText(lastParamValue);
            }
        });
    }

    /**
     * Initialize the onFocusListener used to display editing options when the parameter has
     * the user focus.
     */
    private void initOnFocusListener() {
        paramValue.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, final boolean hasFocus) {
                setFocus(hasFocus);
            }
        });
    }

    private void setFocus(boolean focus) {
        if (focus) {
            paramValue.setSelection(paramValue.getText().length());
            paramNameSmall.setVisibility(GONE);
            paramNameBig.setVisibility(VISIBLE);
            if (infoText.getText() != "") {
                infoText.setVisibility(VISIBLE);
            }
            buttonPanel.setVisibility(VISIBLE);
            editMode = true;
        } else {
            paramNameBig.setVisibility(GONE);
            infoText.setVisibility(GONE);
            buttonPanel.setVisibility(GONE);
            paramNameSmall.setVisibility(VISIBLE);
            editMode = false;
        }
    }

    /**
     * Used to change the subindex of this parameter according to the profile number of ETX devices.
     * This method is not intended to be used with devices != ETX.
     *
     * @param profileNumber
     */
    public void setProfile(int profileNumber) {
        parameter = parameter.substring(0, parameter.length() - 1) + profileNumber;
        setDisplayText();
        provider.getUiEventBus().post(new RegisterParameterCommand(parameter));
    }

    /**
     * If no displayText is supplied the default param Name is used.
     */
    private void setDisplayText() {
        String displayText = arguments.getString(R.styleable.ParameterEditable_editableDisplayText);
        if (displayText != null) {
            paramNameSmall.setText(displayText);
            paramNameBig.setText(displayText);
        } else {
            paramNameSmall.setText(manager.getParamMap().get(parameter).getName());
            paramNameBig.setText(manager.getParamMap().get(parameter).getName());
        }
    }

    /**
     * Set the information text if it has been defined.
     */
    private void setInfoText() {
        String text = arguments.getString(R.styleable.ParameterEditable_editableInfoText);
        if (text != null) {
            infoText.setText(text);
        }
    }

    /**
     * Returns the id of the parameter thats registered for this view.
     *
     * @return
     */
    public String getParameter() {
        return parameter;
    }

    public void setValue(String value) {
        String result = value;
        if (measuringUnit != null) {
            paramMeasurementUnit.setText(measuringUnit);
        }
        lastParamValue = result;
        paramValue.setText(result);
    }

    /**
     * Check if the deviceChanged message contains this parameter and update it if that's the case.
     *
     * @param msg
     */
    public void handleDeviceChanged(DeviceChanged msg) {
        ParameterValue dp = msg.getDevice().getDeviceParameter(getParameter());
        if (dp != null && !editMode) {
            setValue(dp.getValue());
        }
    }

    public void handleDeviceNotChanged(DeviceNotChanged msg) {
        if (getParameter().contains(msg.getField())) {
            paramValue.setText(getResources().getString(R.string.fieldDoesNotExist));
            paramValue.setEnabled(false);
            paramValue.setFocusable(false);
            paramValue.setInputType(InputType.TYPE_NULL);
        }
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}
