package ch.fluxron.fluxronapp.ui.components;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.objectBase.DeviceParameter;
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

        arguments = context.obtainStyledAttributes(attrs, R.styleable.ParameterView);
        parameter = arguments.getString(R.styleable.ErrorViewView_errorParamName);

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
        DeviceParameter dp = msg.getDevice().getDeviceParameter(getParameter());
        if(dp != null){
            setValue(dp.getValue());
        }
    }

    public void setValue(String value) {
        this.errorCode.setText(value);
    }
}