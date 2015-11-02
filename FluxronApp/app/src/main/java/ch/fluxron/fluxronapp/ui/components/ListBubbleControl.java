package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import ch.fluxron.fluxronapp.R;

/**
 * Represents a control that draws dots for every element in the list.
 */
public class ListBubbleControl extends LinearLayout{
    private int numberOfBubbles = 0;
    private int currentBubble = 0;
    private final int MARGIN_SPACE = 15;

    public ListBubbleControl(Context context) {
        super(context);
        setUpLayout();
    }

    public ListBubbleControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpLayout();
    }

    public ListBubbleControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpLayout();
    }


    private void setUpLayout() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
    }


    public void setNumberOfBubbles(int n){
        numberOfBubbles = n;
        adjustChildren();
    }

    private void adjustChildren() {
        // remove unnecessary bubbles
        while (this.getChildCount() > numberOfBubbles) {
            this.removeViewAt(this.getChildCount() - 1);
        }

        // add needed bubbles
        while(this.getChildCount() < numberOfBubbles) {
            LayoutParams p = new LayoutParams(MARGIN_SPACE, MARGIN_SPACE);
            p.setMargins(MARGIN_SPACE / 2, 0, MARGIN_SPACE / 2, 0);

            View v = new View(getContext());
            v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primaryColorDark));
            v.setLayoutParams(p);

            this.addView(v);
        }
    }

    public void setCurrentBubble(int i){
        currentBubble = i;
    }
}