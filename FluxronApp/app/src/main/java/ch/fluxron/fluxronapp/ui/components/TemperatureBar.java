package ch.fluxron.fluxronapp.ui.components;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ch.fluxron.fluxronapp.R;

/**
 * Represents a bar with a target temperature and an actual temperature
 */
public class TemperatureBar extends LinearLayout{
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
        LayoutInflater.from(context).inflate(R.layout.compound_temperature_bar, this, true);

        middleSegment = findViewById(R.id.middleSegment);

        // TODO: Allow styling via the colors XML (obtainStyledAttributes etc)
        // TODO: Animate changes
        // TODO: Use actual values
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
}
