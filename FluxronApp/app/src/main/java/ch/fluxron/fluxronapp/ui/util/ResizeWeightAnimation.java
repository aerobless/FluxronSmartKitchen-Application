package ch.fluxron.fluxronapp.ui.util;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Animates the width of a view
 */
public class ResizeWeightAnimation extends Animation
{
    private float mWeight;
    private float mStartWeight;
    private View mView;
    private ValueAnimator.AnimatorUpdateListener listener;

    public ResizeWeightAnimation(View view, float targetWeight, long duration, ValueAnimator.AnimatorUpdateListener listener)
    {
        mView = view;
        mWeight = targetWeight;
        mStartWeight = ((LinearLayout.LayoutParams)view.getLayoutParams()).weight;
        this.listener = listener;
        setDuration(duration);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        ((LinearLayout.LayoutParams)mView.getLayoutParams()).weight = (mStartWeight + (mWeight - mStartWeight) * interpolatedTime);
        mView.requestLayout();
        if (listener != null) {
            listener.onAnimationUpdate(null);
        }
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight)
    {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds()
    {
        return true;
    }
}
