package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Represents a bar with a target temperature and an actual temperature
 */
public class TemperatureBar extends LinearLayout{
    ParamManager manager;
    TypedArray arguments;
    String parameter;
    TextView paramName;
    //TextView paramValue;
    IEventBusProvider provider;


    private int minTemp = 0;
    private int maxTemp = 200;
    private int curTemp = 150;
    private View middleSegment;

    /**
     * Create a new temperature bar
     * @param context Context
     * @param attrs Attributes
     */
    public TemperatureBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.component_temperature_bar, this, true);

        middleSegment = findViewById(R.id.middleSegment);

        manager = new ParamManager();

        arguments = context.obtainStyledAttributes(attrs, R.styleable.TemperatureBar);
        parameter = arguments.getString(R.styleable.TemperatureBar_temperatureParamName);

        paramName = (TextView) this.findViewById(R.id.paramName);
        //paramValue = (TextView) this.findViewById(R.id.paramValue);

        setDisplayText();

        // TODO: Allow styling via the colors XML (obtainStyledAttributes etc)
        // TODO: Animate changes
        // TODO: Use actual values

        if (!isInEditMode()) {
            provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider) getContext().getApplicationContext();
            provider.getUiEventBus().post(new RegisterParameterCommand(parameter));
        }
    }

    /**
     * Sets the minimum and the maximum temperature for the display
     * @param min Minimum temperature
     * @param max Maximum temperature
     */
    public void setMinMax(int min, int max) {
        minTemp = min;
        maxTemp = max;
        ViewGroup.LayoutParams params = middleSegment.getLayoutParams();
        params.width = 290;
        middleSegment.setLayoutParams(params);
    }

    /**
     * If no displayText is supplied the default param Name is used.
     */
    private void setDisplayText() {
        String displayText = arguments.getString(R.styleable.TemperatureBar_temperatureDisplayText);
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
}
