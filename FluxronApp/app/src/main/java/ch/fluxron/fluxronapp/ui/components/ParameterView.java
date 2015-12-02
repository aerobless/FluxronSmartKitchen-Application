package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceNotChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Displays a non-editable parameter to the user with name and value.
 */
public class ParameterView extends RelativeLayout {
    ParamManager manager;
    TypedArray arguments;
    String parameter;
    String measuringUnit;
    int decPointPos;
    TextView paramName;
    TextView paramValue;
    IEventBusProvider provider;

    /**
     * Instantiates a new ParameterView.
     *
     * @param context
     * @param attrs
     */
    public ParameterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.component_parameter_view, this, true);
        manager = new ParamManager();

        arguments = context.obtainStyledAttributes(attrs, R.styleable.ParameterView);
        parameter = arguments.getString(R.styleable.ParameterView_paramName);
        measuringUnit = arguments.getString(R.styleable.ParameterView_paramMeasuringUnit);
        decPointPos = arguments.getInteger(R.styleable.ParameterView_paramDecPointPosRight, 0);

        paramName = (TextView) this.findViewById(R.id.paramName);
        paramValue = (TextView) this.findViewById(R.id.paramValue);

        setDisplayText();

        if (!isInEditMode()) {
            provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider) getContext().getApplicationContext();
            provider.getUiEventBus().post(new RegisterParameterCommand(parameter));
        }
    }


    /**
     * If no displayText is supplied the default param Name is used.
     */
    private void setDisplayText() {
        String displayText = arguments.getString(R.styleable.ParameterView_paramDisplayText);
        if (displayText != null) {
            paramName.setText(displayText);
        } else {
            paramName.setText(manager.getParamMap().get(parameter).getName());
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

    /**
     * Sets the value of this control.
     *
     * @param value
     */
    public void setValue(String value) {
        String formattedValue = value;

        //Format decimal point
        if (decPointPos != 0) {
            formattedValue = value.substring(0, value.length() - decPointPos) + "." + value.substring(value.length() - decPointPos, value.length());
            if (formattedValue.length() == 2) {
                formattedValue = "0" + formattedValue;
            }
        }

        //Format measuring unit
        if (measuringUnit != null) {
            formattedValue += " " + measuringUnit;
        }

        paramValue.setText(formattedValue);
    }

    /**
     * Checks if the DeviceChanged message contains this parameter and sets its value if that's the case.
     *
     * @param msg
     * @return
     */
    public String handleDeviceChanged(DeviceChanged msg) {
        ParameterValue dp = msg.getDevice().getDeviceParameter(getParameter());
        if (dp != null) {
            String val = dp.getValue();
            setValue(val);
            return val;
        }
        return null;
    }

    /**
     * Checks if the DeviceNotChanged message contains this parameter and deactivates the control if that's the case.
     * This happens when the parameter doesn't exist on the device (e.g. old software version).
     *
     * @param msg
     */
    public void handleDeviceNotChanged(DeviceNotChanged msg) {
        if (getParameter().contains(msg.getField())) {
            paramValue.setText(getResources().getString(R.string.fieldDoesNotExist));
            paramValue.setEnabled(false);
            paramValue.setFocusable(false);
        }
    }
}
