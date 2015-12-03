package ch.fluxron.fluxronapp.ui.util;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Animates the layout weight of a view
 */
public class ResizeWeightAnimation extends Animation
{
    private float mWeight;
    private float mStartWeight;
    private View mView;
    private ValueAnimator.AnimatorUpdateListener listener;

    /**
     * Creates a new resize animation
     * @param view View to animate
     * @param targetWeight Weight to animate to
     * @param duration Duration of the animation
     * @param listener Listener
     */
    public ResizeWeightAnimation(View view, float targetWeight, long duration, ValueAnimator.AnimatorUpdateListener listener)
    {
        mView = view;
        mWeight = targetWeight;
        mStartWeight = ((LinearLayout.LayoutParams)view.getLayoutParams()).weight;
        this.listener = listener;
        setDuration(duration);
    }

    /**
     * Applies the animated value
     * @param interpolatedTime Timing
     * @param t Transformation
     */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        ((LinearLayout.LayoutParams)mView.getLayoutParams()).weight = (mStartWeight + (mWeight - mStartWeight) * interpolatedTime);
        mView.requestLayout();
        if (listener != null) {
            listener.onAnimationUpdate(null);
        }
    }

    /**
     * Initializes the animation
     * @param width Width
     * @param height Height
     * @param parentWidth Parent width
     * @param parentHeight Parent height
     */
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight)
    {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    /**
     * Gets a value that specifies wether this animation changes the bounds of the control. Always true.
     * @return True
     */
    @Override
    public boolean willChangeBounds()
    {
        return true;
    }
}
