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
     * New resize animation
     * @param view View to animate
     * @param targetWeight Target layout weight
     * @param duration Duration
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
     * Applies the animation at the current time
     * @param interpolatedTime Interpolated time from 0 to 1
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
     * Initializes this animation
     * @param width Width
     * @param height height
     * @param parentWidth Parent Width
     * @param parentHeight Parent Height
     */
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight)
    {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    /**
     * Returns true
     * @return true
     */
    @Override
    public boolean willChangeBounds()
    {
        return true;
    }
}
