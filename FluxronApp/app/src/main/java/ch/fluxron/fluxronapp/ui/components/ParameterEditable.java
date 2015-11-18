package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
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
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChangeCommand;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
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
    private TextView paramNameSmall;
    private TextView paramNameBig;
    private TextView infoText;
    private boolean editMode;
    private EditText paramValue;
    private String lastParamValue = "";
    private String deviceAddress;
    private LinearLayout buttonPanel;
    private LinearLayout paramPanel;
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

        paramNameSmall = (TextView) this.findViewById(R.id.paramNameSmall);
        paramNameBig = (TextView) this.findViewById(R.id.paramNameBig);
        infoText = (TextView) this.findViewById(R.id.infoText);
        paramValue = (EditText) this.findViewById(R.id.paramValue);
        buttonPanel = (LinearLayout) this.findViewById(R.id.buttonPanel);
        paramPanel = (LinearLayout) this.findViewById(R.id.paramPanel);
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
    }

    private void initButtonListeners() {
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = paramValue.getText().toString();
                //TODO: possible problem if we have to enter hex data..?
                value = value.replaceAll("[^\\d]", ""); //TODO: better way to format the values instead of removing non-numerics here.
                if (value != "" && deviceAddress != null) {
                    provider.getUiEventBus().post(new DeviceChangeCommand(deviceAddress, new ParameterValue(parameter, value)));
                } else {
                    Log.d("FLUXRON", "Trying to save an empty value in ParameterEditable OR ParameterEditable.deviceAddress hasn't been set.");
                    //TODO: message/toast/info for user
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
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
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
        });
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
            result += " " + measuringUnit;
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
        if (dp != null & !editMode) {
            setValue(dp.getValue());
        }
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
}
