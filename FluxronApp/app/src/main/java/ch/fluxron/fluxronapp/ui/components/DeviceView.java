package ch.fluxron.fluxronapp.ui.components;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceChanged;
import ch.fluxron.fluxronapp.events.modelUi.deviceOperations.DeviceFailed;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.objectBase.DevicePosition;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Renders a device
 */
public class DeviceView extends RelativeLayout implements View.OnTouchListener, ObjectAnimator.AnimatorUpdateListener {

    private DevicePosition position;
    private IDeviceViewListener listener;
    IEventBusProvider provider;

    private String deviceType;
    private String deviceName;
    private String deviceAddress;

    private float lastTouchX;
    private float lastTouchY;

    private float draggingX;
    private float draggingY;

    private boolean deleted = false;
    private boolean cancelAnimation = false;

    private HashMap<Integer, Animator> animators;

    public final static int DEVICE_STATUS_OK = 0;
    public final static int DEVICE_STATUS_FAILURE = 1;
    public final static int DEVICE_STATUS_UNKNOWN = 2;

    /**
     * Creates a new device view
     *
     * @param context Context
     */
    public DeviceView(Context context) {
        super(context);
        setUp();
        provider = (IEventBusProvider) getContext().getApplicationContext();
        provider.getUiEventBus().register(this);
        setDeviceStatus(DEVICE_STATUS_UNKNOWN);
    }

    /**
     * Creates a new device view
     *
     * @param context Context
     * @param attrs   Attributes
     */
    public DeviceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
        provider = (IEventBusProvider) getContext().getApplicationContext();
        provider.getUiEventBus().register(this);
        setDeviceStatus(DEVICE_STATUS_UNKNOWN);
    }

    /**
     * Creates a new device view
     *
     * @param context      Context
     * @param attrs        Attributes
     * @param defStyleAttr Defined Attributes
     */
    public DeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp();
        provider = (IEventBusProvider) getContext().getApplicationContext();
        provider.getUiEventBus().register(this);
        setDeviceStatus(DEVICE_STATUS_UNKNOWN);
    }

    /**
     * Sets a listener to listen for events
     *
     * @param l Event listener
     */
    public void setListener(IDeviceViewListener l) {
        this.listener = l;
    }

    /**
     * Gets the position of the device
     *
     * @return Position
     */
    public DevicePosition getPosition() {
        return position;
    }

    /**
     * Gets the position of the device
     *
     * @param pos Position
     */
    public void setPosition(DevicePosition pos) {
        this.position = pos;
    }

    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Set the device name. If the device type is unknown, the device name is set as text for the deviceOrb.
     *
     * @param deviceName Name of the devices
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        TextView deviceOrbText = ((TextView) this.findViewById(R.id.theDeviceOrb));
        //If we don't know the device type we use the device name instead.
        if (deviceType.equals(Device.UNKNOWN_DEVICE_TYPE) || deviceOrbText.toString().equals("")) {
            deviceOrbText.setText(deviceName);
        }
    }

    /**
     * Get the device type.
     *
     * @return Type of the device
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Set the device type. If the device type is unknown, the device name is set as text for the deviceOrb.
     *
     * @param deviceType Type of the device
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        TextView deviceOrbText = ((TextView) this.findViewById(R.id.theDeviceOrb));
        if (deviceType.equals(Device.UNKNOWN_DEVICE_TYPE)) {
            //If we don't know the device type we use the device name instead.
            deviceOrbText.setText(deviceName);
        } else {
            deviceOrbText.setText(getDeviceType());
        }
    }

    /**
     * Set the device status. Status can be OK, FAILURE or UNKNOWN.
     *
     * @param deviceStatus Status of the device
     */
    private void setDeviceStatus(int deviceStatus) {
        TextView statusOrb = (TextView) findViewById(R.id.theStatusOrb);
        switch (deviceStatus) {
            case DEVICE_STATUS_OK:
                statusOrb.setText(getResources().getText(R.string.ok_check));
                statusOrb.setBackground(getResources().getDrawable(R.drawable.status_ok_background));
                break;
            case DEVICE_STATUS_FAILURE:
                statusOrb.setText(getResources().getText(R.string.fail_check));
                statusOrb.setBackground(getResources().getDrawable(R.drawable.status_failure_background));
                break;
            case DEVICE_STATUS_UNKNOWN:
                statusOrb.setText(getResources().getText(R.string.unkown_check));
                statusOrb.setBackground(getResources().getDrawable(R.drawable.status_unkown_background));
                break;
            default:
                statusOrb.setText(getResources().getText(R.string.unkown_check));
                statusOrb.setBackground(getResources().getDrawable(R.drawable.status_unkown_background));
                break;
        }
        fireNeedUpdate();
    }

    /**
     * Updates the status orb to OK when we get a successful DeviceChanged event
     *
     * @param msg Message
     */
    public void onEventMainThread(DeviceChanged msg) {
        if (msg.getDevice().getAddress().equals(deviceAddress)) {
            if (!msg.getDevice().getDeviceType().equals(Device.UNKNOWN_DEVICE_TYPE)) {
                setDeviceType(msg.getDevice().getDeviceType());
            }
            setDeviceStatus(DEVICE_STATUS_OK);
        }
    }

    /**
     * Updates the status orb to FAILURE when we get a DeviceFailed event
     *
     * @param msg Message
     */
    public void onEventMainThread(DeviceFailed msg) {
        if (msg.getAddress().equals(deviceAddress)) {
            setDeviceStatus(DEVICE_STATUS_FAILURE);
        }
    }

    /**
     * Gets the device address
     * @return Address of the device
     */
    public String getDeviceAddress() {
        return deviceAddress;
    }

    /**
     * Sets the address of the device
     * @param deviceAddress Address of the device
     */
    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    /**
     * Set up the state
     */
    private void setUp() {
        animators = new HashMap<>();

        LayoutInflater.from(getContext()).inflate(R.layout.component_device_view, this, true);
        this.setWillNotDraw(false);
        this.setLayoutParams(new ViewGroup.LayoutParams(145, 100));

        this.measure(145, 100);
        this.layout(0, 0, 145, 100);

        this.findViewById(R.id.theDeviceOrb).setOnTouchListener(this);
        this.findViewById(R.id.theDeleteOrb).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClicked();
            }
        });
        this.findViewById(R.id.theCancelOrb).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDeleteStatusViews();
                cancelAnimation = true;
            }
        });

        // hide controls that are only situational
        findViewById(R.id.theDeleteOrb).setVisibility(ViewGroup.GONE);
        findViewById(R.id.theCancelOrb).setVisibility(ViewGroup.GONE);
    }

    /**
     * Delete was clicked
     */
    private void deleteClicked() {
        if (listener != null) {
            listener.deleteRequested(this);
        }
    }

    /**
     * Notify the listener of an opening request if it is existing
     */
    private void fireActionRequested() {
        if (listener != null) {
            listener.actionRequested(this);
        }
    }

    /**
     * Request a move
     * @param dx x-direction
     * @param dy y-direction
     * @param lastPosition No more changes after this move?
     * @return Accepted
     */
    private boolean fireRequestMove(float dx, float dy, boolean lastPosition) {
        if (listener != null) {
            return listener.moveRequested(this, (int) dx, (int) dy, lastPosition);
        }
        return false;
    }

    /**
     * Touch event
     * @param v View
     * @param event Touch event
     * @return Handled
     */
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
                float dTotalDrag = draggingX * draggingX + draggingY * draggingY;

                if (dTotalDrag < 15) {
                    fireActionRequested();
                } else {
                    fireRequestMove(0, 0, true); // Do not move further, this is the final position
                }
                break;
        }
        return true;
    }

    /**
     * Starts the popup animation
     */
    public void popUp() {
        animateIn(findViewById(R.id.theDeviceOrb));
        animateIn(findViewById(R.id.theStatusOrb));
    }

    /**
     * Needs an update
     */
    private void fireNeedUpdate() {
        if (listener != null) {
            listener.needsRepaint(false);
        }
    }

    /**
     * Animation updated, need a repaint
     * @param animation Animation
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        fireNeedUpdate();
    }

    /**
     * Deletion should be reassured by the users
     */
    public void askForDelete() {
        // Pop up the delete and cancel icon
        animateIn(findViewById(R.id.theDeleteOrb));
        animateIn(findViewById(R.id.theCancelOrb));

        // hide the status orb
        animateOut(findViewById(R.id.theStatusOrb));

        // Remove the delete icon after 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeDeleteStatusViews();
            }
        }, 2000);
    }

    /**
     * Delete cancelled
     */
    private void removeDeleteStatusViews() {
        // Hide the delete icon and the cancel icon
        animateOut(findViewById(R.id.theDeleteOrb));
        animateOut(findViewById(R.id.theCancelOrb));

        // show the status orb
        if (!deleted && !cancelAnimation) animateIn(findViewById(R.id.theStatusOrb));
        cancelAnimation = false;
    }

    /**
     * Start the deletion animation
     */
    public void remove() {
        deleted = true;

        // animate everything out, we are not needed anymore
        animateOut(findViewById(R.id.theDeleteOrb));
        animateOut(findViewById(R.id.theCancelOrb));
        animateOut(findViewById(R.id.theStatusOrb));
        animateOut(findViewById(R.id.theDeviceOrb));
    }

    /**
     * Animates a views popup effect
     * @param target View
     */
    private void animateIn(View target) {
        if (animators.containsKey(target.getId())) {
            animators.get(target.getId()).end();
        }

        target.setVisibility(ViewGroup.VISIBLE);
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.device_flash);
        set.setTarget(target);
        ((ObjectAnimator) set.getChildAnimations().get(0)).addUpdateListener(this);
        set.start();

        animators.put(target.getId(), set);
    }

    /**
     * Animates a views fadeout effect
     * @param target View
     */
    private void animateOut(final View target) {
        if (animators.containsKey(target.getId())) {
            animators.get(target.getId()).end();
        }

        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.device_hide);
        set.setTarget(target);
        ((ObjectAnimator) set.getChildAnimations().get(0)).addUpdateListener(this);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                target.setVisibility(ViewGroup.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.start();

        animators.put(target.getId(), set);
    }

    /**
     * Gets the deletion flag from this view
     * @return Deletion flag
     */
    public boolean isDeleted() {
        return deleted && allAnimationsDone();
    }

    /**
     * Returns wether all animations are done or not
     * @return All animations done
     */
    private boolean allAnimationsDone() {
        boolean animationsRunning = false;
        for (Animator a : animators.values()) {
            animationsRunning = animationsRunning || a.isRunning();
        }

        return !animationsRunning;
    }

    /**
     * Cleans upd any references
     */
    public void cleanUp() {
        if(this.provider!=null && this.provider.getUiEventBus().isRegistered(this)) {
            provider.getUiEventBus().unregister(this);
            provider = null;
        }
    }
}