package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Represents a bar with a target temperature and an actual temperature
 */
public class TemperatureBar extends LinearLayout {
    private ParamManager manager;
    private TypedArray arguments;
    private String parameter;
    private TextView paramName;
    private TextView currentTemperature;
    private TextView maxTemperature;
    private View frontSegment;
    private View middleSegment;
    private View lastSegment;
    private View space1;
    private IEventBusProvider provider;

    private int maxTemp = 100;
    private int maxOffsetTemp = 40;

    /**
     * Create a new temperature bar
     *
     * @param context Context
     * @param attrs   Attributes
     */
    public TemperatureBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.component_temperature_bar, this, true);
        manager = new ParamManager();

        arguments = context.obtainStyledAttributes(attrs, R.styleable.TemperatureBar);
        parameter = arguments.getString(R.styleable.TemperatureBar_temperatureParamName);
        currentTemperature = (TextView) findViewById(R.id.currentTemperatureValue);
        maxTemperature = (TextView) findViewById(R.id.maxTemperatureValue);
        frontSegment = findViewById(R.id.frontSegment);
        middleSegment = findViewById(R.id.middleSegment);
        lastSegment = findViewById(R.id.lastSegment);
        space1 = findViewById(R.id.space1);
        paramName = (TextView) this.findViewById(R.id.paramName);
        setDisplayText();

        if (!isInEditMode()) {
            provider = (ch.fluxron.fluxronapp.ui.util.IEventBusProvider) getContext().getApplicationContext();
            provider.getUiEventBus().post(new RegisterParameterCommand(parameter));
        }
    }

    private void updateCurrentTempPos() {
        int halfText = (currentTemperature.getWidth() - currentTemperature.getPaddingLeft()) / 2;
        int textOffset = frontSegment.getWidth() - halfText + (space1.getWidth() / 2);
        currentTemperature.setPadding(textOffset, 0, 0, 0);
    }

    private void updateMaxTempPos() {
        int halfText = (maxTemperature.getWidth() - maxTemperature.getPaddingLeft()) / 2;
        int textOffset = frontSegment.getWidth() + middleSegment.getWidth() - halfText + 3 * (space1.getWidth() / 2);
        maxTemperature.setPadding(textOffset, 0, 0, 0);
    }

    /**
     * Sets the minimum and the maximum temperature for the display
     *
     * @param max Maximum temperature
     */
    public void setMax(int max) {
        maxTemp = max;
    }

    /**
     * Set the current temperature, updates UI.
     *
     * @param temperature
     */
    public void updateCurrentTemperature(float temperature) {
        int limit = maxTemp + maxOffsetTemp;
        float currentTempPercent = 100f / limit * temperature;
        float maxTempPercent = (100f / limit * maxTemp) - currentTempPercent;
        float restTempPercent = 100f - currentTempPercent - maxTempPercent;

        float currentTempWeight = (100 - currentTempPercent) / 100;
        float maxTempWeight = (100 - maxTempPercent) / 100;
        float limitTempWeight = (100 - restTempPercent) / 100;

        frontSegment.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, currentTempWeight));
        middleSegment.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, maxTempWeight));
        lastSegment.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, limitTempWeight));

        currentTemperature.setText(temperature + " °C");
        maxTemperature.setText(maxTemp + " °C");

        post(new Runnable() {
            @Override
            public void run() {
                updateCurrentTempPos();
                updateMaxTempPos();
            }
        });
    }

    /**
     * If no displayText is supplied the default parameter_name is used.
     */
    private void setDisplayText() {
        String displayText = arguments.getString(R.styleable.TemperatureBar_temperatureDisplayText);
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

    public void handleDeviceChanged(DeviceChanged msg) {
        ParameterValue dp = msg.getDevice().getDeviceParameter(getParameter());
        if (dp != null) {
            Float value = Float.parseFloat(dp.getValue());
            updateCurrentTemperature(value);
            if (value > 500) {
                setMax(100000);
            } else if (value < 500 && value > 400) {
                setMax(500);
            } else if (value < 400 && value > 300) {
                setMax(400);
            } else if (value < 300 && value > 200) {
                setMax(300);
            } else if (value < 200 && value > 100) {
                setMax(200);
            } else if (value < 100) {
                setMax(100);
            }
        }
    }
}
