package ch.fluxron.fluxronapp.ui.components;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Displays a non-editable error code to the user with name and value.
 */
public class ErrorView extends LinearLayout {
    ParamManager manager;
    TypedArray arguments;
    String parameter;
    TextView errorDescription;
    TextView errorSince;
    TextView errorCode;
    IEventBusProvider provider;

    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.component_error_view, this, true);
        setOrientation(LinearLayout.HORIZONTAL);

        manager = new ParamManager();

        arguments = context.obtainStyledAttributes(attrs, R.styleable.ErrorView);
        parameter = arguments.getString(R.styleable.ErrorView_errorParamName);

        errorCode = (TextView) this.findViewById(R.id.errorCode);
        errorDescription = (TextView) this.findViewById(R.id.errorDescription);
        errorSince = (TextView) this.findViewById(R.id.errorSince);

        if (!isInEditMode()) {
            provider = (IEventBusProvider) getContext().getApplicationContext();
            provider.getUiEventBus().post(new RegisterParameterCommand(parameter));
        }
    }

    /**
     * Returns the id of the parameter thats registered for this view.
     * @return
     */
    public String getParameter(){
        return parameter;
    }

    public void handleDeviceChanged(DeviceChanged msg){
        ParameterValue dp = msg.getDevice().getDeviceParameter(getParameter());
        if(dp != null){
            setValue(dp.getValue());
        }
    }

    public void setValue(String value) {
        String code = getErrorCodeFromValue(value);
        String counter = getCounterFromValue(value);
        this.errorCode.setText(code);
        this.errorDescription.setText(getErrorDescriptionFromCode(code));
        this.errorSince.setText(counter);
    }

    /**
     * Gets the error code part from a parameter value
     * @param value Value
     * @return Error code
     */
    private String getErrorCodeFromValue(String value) {
        // TODO: do it properly
        return value;
    }

    /**
     * Gets the counter from an error parameter value
     * @param value Value
     * @return Usage counter
     */
    private String getCounterFromValue(String value) {
        // TODO: do it properly
        return value;
    }

    /**
     * Maps an error code to a resource id
     * @param code Error Code
     * @return Resource id
     */
    private int getErrorDescriptionFromCode(String code) {
        String prefix = getResources().getString(R.string.error_description_prefix);
        return getResources().getIdentifier(prefix + code, "string", getContext().getPackageName());
    }
}
