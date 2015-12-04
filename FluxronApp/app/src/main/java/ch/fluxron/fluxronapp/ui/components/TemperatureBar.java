package ch.fluxron.fluxronapp.ui.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.data.generated.ParamManager;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.RegisterParameterCommand;
import ch.fluxron.fluxronapp.objectBase.ParameterValue;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;
import ch.fluxron.fluxronapp.ui.util.ResizeWeightAnimation;

/**
 * Represents a bar with a target temperature and an actual temperature
 */
public class TemperatureBar extends LinearLayout implements ValueAnimator.AnimatorUpdateListener {
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

    private ResizeWeightAnimation frontAnim;
    private ResizeWeightAnimation middleAnim;
    private ResizeWeightAnimation backAnim;

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

        setMax(100);
        updateCurrentTemperature(50);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMax(420);
                updateCurrentTemperature(120);
            }
        });
    }

    /**
     * Updates the positions of the label
     */
    private void updateCurrentTempPos() {
        int halfText = getTextWidth(currentTemperature) / 2;
        int textOffset = frontSegment.getWidth() - halfText + (space1.getWidth() / 2);
        int paddingLimit = getWidth() - halfText*2-10;
        if (textOffset < paddingLimit) animatePaddingLeft(currentTemperature, textOffset);
    }

    /**
     * Updates the positions of the label
     */
    private void updateMaxTempPos() {
        int halfText = getTextWidth(maxTemperature) / 2;
        int textOffset = frontSegment.getWidth() + middleSegment.getWidth() - halfText + 3 * (space1.getWidth() / 2);
        int paddingLimit = getWidth() - halfText*2 -10;
        if (textOffset < paddingLimit) animatePaddingLeft(maxTemperature, textOffset);
    }

    /**
     * Gets the width of the text
     * @param v Inside this view
     * @return Width of the text
     */
    private int getTextWidth(TextView v) {
        Rect textBounds = new Rect();
        v.getPaint().getTextBounds((String)v.getText(), 0, v.getText().length(), textBounds);
        return textBounds.width();
    }

    /**
     * Sets the left padding
     * @param v View
     * @param textOffset Left padding
     */
    private void animatePaddingLeft(final TextView v, int textOffset) {
        v.setPadding(textOffset, 0, 0, 0);
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

        if (frontAnim != null) frontAnim.cancel();
        if (middleAnim != null) middleAnim.cancel();
        if (backAnim != null) backAnim.cancel();

        frontAnim = new ResizeWeightAnimation(frontSegment, currentTempWeight, 250, null);
        middleAnim = new ResizeWeightAnimation(middleSegment, maxTempWeight, 250, null);
        backAnim = new ResizeWeightAnimation(lastSegment, limitTempWeight, 250, this);

        frontSegment.startAnimation(frontAnim);
        middleSegment.startAnimation(middleAnim);
        lastSegment.startAnimation(backAnim);

        String currentTempText = temperature + " °C";
        String maxTempText = maxTemp + " °C";
        if(!currentTempText.equals(currentTemperature.getText())) currentTemperature.setText(currentTempText);
        if(!maxTempText.equals(maxTemperature.getText())) maxTemperature.setText(maxTempText);
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
     * Returns the id of the parameter that's registered for this view.
     *
     * @return Id of the parameter
     */
    public String getParameter() {
        return parameter;
    }

    public void handleDeviceChanged(DeviceChanged msg) {
        ParameterValue dp = msg.getDevice().getDeviceParameter(getParameter());
        if (dp != null) {
            Float value = Float.parseFloat(dp.getValue());
            updateCurrentTemperature(value);
            setScale(value);
        }
    }

    /**
     * Sets the scale
     * @param value Scale
     */
    private void setScale(Float value) {
        if (value > 500) {
            setMax(100000);
            maxOffsetTemp = 30000; //Fix so that large values don't mess up the UI
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

    /**
     * Animation was updated, update label positions accordingly
     * @param animation Animation
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        updateCurrentTempPos();
        updateMaxTempPos();
    }
}
