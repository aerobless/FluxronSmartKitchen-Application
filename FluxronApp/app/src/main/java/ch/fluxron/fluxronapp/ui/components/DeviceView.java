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

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        if (deviceType.equals(Device.UNKNOWN_DEVICE_TYPE)) {
            ((TextView) this.findViewById(R.id.theDeviceOrb)).setText("?");
        } else {
            ((TextView) this.findViewById(R.id.theDeviceOrb)).setText(getDeviceType());
        }
    }

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
     * @param msg
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
     * @param msg
     */
    public void onEventMainThread(DeviceFailed msg) {
        if (msg.getAddress().equals(deviceAddress)) {
            setDeviceStatus(DEVICE_STATUS_FAILURE);
        }
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

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

    private boolean fireRequestMove(float dx, float dy, boolean lastPosition) {
        if (listener != null) {
            return listener.moveRequested(this, (int) dx, (int) dy, lastPosition);
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

    public void popUp() {
        animateIn(findViewById(R.id.theDeviceOrb));
        animateIn(findViewById(R.id.theStatusOrb));
    }

    private void fireNeedUpdate() {
        if (listener != null) {
            listener.needsRepaint(false);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        fireNeedUpdate();
    }

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

    private void removeDeleteStatusViews() {
        // Hide the delete icon and the cancel icon
        animateOut(findViewById(R.id.theDeleteOrb));
        animateOut(findViewById(R.id.theCancelOrb));

        // show the status orb
        if (!deleted && !cancelAnimation) animateIn(findViewById(R.id.theStatusOrb));
        cancelAnimation = false;
    }

    public void remove() {
        deleted = true;

        // animate everything out, we are not needed anymore
        animateOut(findViewById(R.id.theDeleteOrb));
        animateOut(findViewById(R.id.theCancelOrb));
        animateOut(findViewById(R.id.theStatusOrb));
        animateOut(findViewById(R.id.theDeviceOrb));
    }

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
}