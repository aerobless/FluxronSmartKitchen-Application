package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * {@link ScrollView} extension that allows to configure scroll offset.
 */
public class ConfigurableScrollView extends ScrollView {

    private int scrollOffset = 0;

    public ConfigurableScrollView (final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Set the offset of this scrollview.
     * @param scrollOffset
     */
    public void setScrollOffset (final int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen (final Rect rect) {
        // adjust by scroll offset
        int scrollDelta = super.computeScrollDeltaToGetChildRectOnScreen(rect);
        int newScrollDelta = (int) Math.signum(scrollDelta) * (scrollDelta + this.scrollOffset);
        return newScrollDelta;
    }
}
