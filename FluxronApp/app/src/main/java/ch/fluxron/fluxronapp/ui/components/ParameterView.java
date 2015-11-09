package ch.fluxron.fluxronapp.ui.components;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.data.generated.ParamManager;

public class ParameterView extends RelativeLayout {
    TypedArray arguments;
    ParamManager manager;
    String parameter;
    TextView paramName;
    TextView paramValue;

    public ParameterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.component_parameter_view, this, true);
        manager = new ParamManager();

        arguments = context.obtainStyledAttributes(attrs, R.styleable.ParameterView);
        parameter = arguments.getString(R.styleable.ParameterView_paramName);

        paramName = (TextView) this.findViewById(R.id.paramName);
        paramValue = (TextView) this.findViewById(R.id.paramValue);

        setDisplayText();
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

    public ParameterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.component_parameter_view, this, true);
    }
}
