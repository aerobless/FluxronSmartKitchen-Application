package ch.fluxron.fluxronapp.ui.components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.objectBase.DevicePosition;

/**
 * Renders a device
 */
public class DeviceView extends RelativeLayout implements View.OnTouchListener {

    private DevicePosition position;
    private IDeviceViewListener listener;

    private float lastTouchX;
    private float lastTouchY;

    private float draggingX;
    private float draggingY;

    /**
     * Creates a new device view
     * @param context Context
     */
    public DeviceView(Context context) {
        super(context);
        setUp();
    }

    /**
     * Creates a new device view
     * @param context Context
     * @param attrs Attributes
     */
    public DeviceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
    }

    /**
     * Creates a new device view
     * @param context Context
     * @param attrs Attributes
     * @param defStyleAttr Defined Attributes
     */
    public DeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp();
    }

    /**
     * Sets a listener to listen for events
     * @param l Event listener
     */
    public void setListener(IDeviceViewListener l){
        this.listener = l;
    }

    /**
     * Gets the position of the device
     * @return Position
     */
    public DevicePosition getPosition() {
        return position;
    }

    /**
     * Gets the position of the device
     * @param pos Position
     */
    public void setPosition(DevicePosition pos) {
        this.position = pos;
    }

    /**
     * Set up the state
     */
    private void setUp() {
        LayoutInflater.from(getContext()).inflate(R.layout.component_device_view, this, true);
        this.setWillNotDraw(false);
        this.setLayoutParams(new ViewGroup.LayoutParams(145, 100));

        this.measure(145, 100);
        this.layout(0, 0, 145, 100);

        this.findViewById(R.id.theStatusOrb).setOnTouchListener(this);
    }

    /**
     * Notify the listener of an opening request if it is existing
     */
    private void fireActionRequested() {
        if(listener!=null){
            listener.actionRequested(this);
        }
    }

    private boolean fireRequestMove(float dx, float dy, boolean lastPosition) {
        if(listener!=null){
            return listener.moveRequested(this, (int)dx, (int)dy, lastPosition);
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                draggingX = 0;
                draggingY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                draggingX += event.getX() - lastTouchX;
                draggingY += event.getY() - lastTouchY;
                fireRequestMove(event.getX() - lastTouchX, event.getY() - lastTouchY, false);
                break;
            case MotionEvent.ACTION_UP:
                float dTotalDrag = draggingX*draggingX+draggingY*draggingY;

                if (dTotalDrag  < 10) {
                    fireActionRequested();
                }
                else {
                    fireRequestMove(0, 0, true); // Do not move further, this is the final position
                }
                break;
        }
        return true;
    }

    public void popUp(){
        findViewById(R.id.theStatusOrb).setAlpha(0);
        ObjectAnimator oa = ObjectAnimator.ofFloat(findViewById(R.id.theStatusOrb), View.ALPHA, 1).setDuration(4000);

        oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fireNeedUpdate();
            }
        });
        oa.start();
    }

    private void fireNeedUpdate() {
        if(listener!=null){
            listener.needsRepaint(false);
        }
    }
}