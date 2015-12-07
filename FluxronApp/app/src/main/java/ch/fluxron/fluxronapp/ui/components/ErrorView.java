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
import ch.fluxron.fluxronapp.ui.util.ErrorCodeConverter;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Displays a non-editable error code to the user with name and value.
 */
public class ErrorView extends LinearLayout {
    ParamManager manager;
    LinearLayout errorControl;
    TypedArray arguments;
    String parameter;
    TextView errorDescription;
    TextView errorSince;
    TextView errorCode;
    IEventBusProvider provider;

    /**
     * Creates a new error view
     *
     * @param context Context
     * @param attrs   Attributes
     */
    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.component_error_view, this, true);
        setOrientation(LinearLayout.HORIZONTAL);

        manager = new ParamManager();

        arguments = context.obtainStyledAttributes(attrs, R.styleable.ErrorView);
        parameter = arguments.getString(R.styleable.ErrorView_errorParamName);
        errorControl = (LinearLayout) this.findViewById(R.id.errorControl);
        errorControl.setVisibility(GONE);

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
     *
     * @return Parameter
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * Handles DeviceChanged events. If the event had an effect it returns true, otherwise false.
     *
     * @param msg Message
     * @return Parameter found
     */
    public boolean handleDeviceChanged(DeviceChanged msg) {
        ParameterValue dp = msg.getDevice().getDeviceParameter(getParameter());
        if (dp != null) {
            return setValue(dp.getValue());
        }
        return false;
    }

    /**
     * Converts a combined Fluxron error code to a legible error code and time. Then sets these
     * values in the UI.
     *
     * @param value input ErrorCode
     * @return success
     */
    public boolean setValue(String value) {
        errorControl.setVisibility(VISIBLE);
        String code = ErrorCodeConverter.convertToErrorCode(Integer.parseInt(value));
        String counter = ErrorCodeConverter.convertToTime(Integer.parseInt(value)) + " " + getResources().getString(R.string.hoursAgo);
        this.errorCode.setText(code);
        this.errorDescription.setText(getErrorDescriptionFromCode(code));
        this.errorSince.setText(counter);
        if(code.equals("e00")){
            this.setVisibility(GONE);
            return false;
        }
        return true;
    }

    /**
     * Maps an error code to a resource id
     *
     * @param code Error Code
     * @return Resource id
     */
    private int getErrorDescriptionFromCode(String code) {
        String prefix = getResources().getString(R.string.error_description_prefix);
        return getResources().getIdentifier(prefix + code, "string", getContext().getPackageName());
    }
}
