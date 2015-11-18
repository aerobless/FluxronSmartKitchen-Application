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
    TextView paramName;
    TextView paramValue;
    IEventBusProvider provider;

    public ParameterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.component_parameter_view, this, true);
        manager = new ParamManager();

        arguments = context.obtainStyledAttributes(attrs, R.styleable.ParameterView);
        parameter = arguments.getString(R.styleable.ParameterView_paramName);

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
        if(displayText != null){
            paramName.setText(displayText);
        } else {
            paramName.setText(manager.getParamMap().get(parameter).getName());
        }
    }

    /**
     * Returns the id of the parameter thats registered for this view.
     * @return
     */
    public String getParameter(){
        return parameter;
    }

    public void setValue(String value){
        paramValue.setText(value);
    }

    public String handleDeviceChanged(DeviceChanged msg){
        ParameterValue dp = msg.getDevice().getDeviceParameter(getParameter());
        if(dp != null){
            String val = dp.getValue();
            setValue(val);
            return val;
        }
        return null;
    }
}
