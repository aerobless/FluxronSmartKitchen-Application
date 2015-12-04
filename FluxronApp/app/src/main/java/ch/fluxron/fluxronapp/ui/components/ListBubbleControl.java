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
    private int normalColor;
    private int highlightedColor;

    /**
     * New list bubble control
     * @param context Context
     */
    public ListBubbleControl(Context context) {
        super(context);
        setUpLayout();
    }

    /**
     * New list bubble control
     * @param context Context
     * @param attrs Attributes
     */
    public ListBubbleControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpLayout();
    }

    /**
     * New list bubble control
     * @param context Context
     * @param attrs Attrs
     * @param defStyleAttr Styling
     */
    public ListBubbleControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpLayout();
    }

    /**
     * Sets up the layout
     */
    private void setUpLayout() {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);

        this.normalColor = ContextCompat.getColor(getContext(), R.color.primaryColorDark);
        this.highlightedColor = ContextCompat.getColor(getContext(), R.color.accentColor1);
    }

    /**
     * Sets the total number of bubbles
     * @param n Number of bubbles
     */
    public void setNumberOfBubbles(int n){
        numberOfBubbles = n;
        adjustChildren();
    }

    /**
     * Recalculates the number of children needed
     */
    private void adjustChildren() {
        // remove unnecessary bubbles
        while (this.getChildCount() > numberOfBubbles) {
            this.removeViewAt(this.getChildCount() - 1);
        }

        // add needed bubbles
        while(this.getChildCount() < numberOfBubbles) {
            LayoutParams p = new LayoutParams(MARGIN_SPACE, MARGIN_SPACE);
            p.setMargins(MARGIN_SPACE / 2, 0, MARGIN_SPACE / 2, 0);
            p.gravity = Gravity.CENTER_VERTICAL;

            View v = new View(getContext());
            v.setBackgroundColor(normalColor);
            v.setLayoutParams(p);

            this.addView(v);
        }
    }

    /**
     * Sets the index of the selected item
     * @param i Index
     */
    public void setCurrentBubble(int i){
        int oldBubble = currentBubble;
        currentBubble = i;

        if (this.getChildCount() > oldBubble) this.getChildAt(oldBubble).setBackgroundColor(normalColor);
        if (this.getChildCount() > currentBubble) this.getChildAt(currentBubble).setBackgroundColor(highlightedColor);
    }
}
