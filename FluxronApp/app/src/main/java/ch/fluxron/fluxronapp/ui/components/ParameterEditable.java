package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.objectBase.DeviceParameter;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Displays a editable parameter to the user with name and value.
 */
public class ParameterEditable extends LinearLayout {
    ParamManager manager;
    TypedArray arguments;
    String parameter;
    String measuringUnit;
    TextView paramNameSmall;
    TextView paramNameBig;
    TextView infoText;
    EditText paramValue;
    LinearLayout buttonPanel;
    LinearLayout paramPanel;
    IEventBusProvider provider;

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

        setDisplayText();
        setInfoText();

        if (!isInEditMode()) {
            provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider) getContext().getApplicationContext();
            provider.getUiEventBus().post(new RegisterParameterCommand(parameter));
        }

        paramValue.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    paramNameSmall.setVisibility(GONE);
                    paramNameBig.setVisibility(VISIBLE);
                    if(infoText.getText()!=""){
                        infoText.setVisibility(VISIBLE);
                    }
                    buttonPanel.setVisibility(VISIBLE);
                    paramPanel.setBackgroundColor(getResources().getColor(R.color.primaryColorLight));
                } else {
                    paramNameBig.setVisibility(GONE);
                    infoText.setVisibility(GONE);
                    buttonPanel.setVisibility(GONE);
                    paramNameSmall.setVisibility(VISIBLE);
                    paramPanel.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                }
            }
        });
    }

    /**
     * If no displayText is supplied the default param Name is used.
     */
    private void setDisplayText() {
        String displayText = arguments.getString(R.styleable.ParameterEditable_editableDisplayText);
        if(displayText != null){
            paramNameSmall.setText(displayText);
            paramNameBig.setText(displayText);
        } else {
            paramNameSmall.setText(manager.getParamMap().get(parameter).getName());
            paramNameBig.setText(manager.getParamMap().get(parameter).getName());
        }
    }

    private void setInfoText() {
        String text = arguments.getString(R.styleable.ParameterEditable_editableInfoText);
        if(text != null){
            infoText.setText(text);
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
        paramValue.setText(value+" "+measuringUnit);
    }

    public void handleDeviceChanged(DeviceChanged msg){
        DeviceParameter dp = msg.getDevice().getDeviceParameter(getParameter());
        if(dp != null){
            setValue(dp.getValue()+" "+measuringUnit);
        }
    }
}
