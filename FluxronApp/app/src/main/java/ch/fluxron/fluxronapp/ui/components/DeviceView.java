package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.objectBase.DevicePosition;

/**
 * Renders a device
 */
public class DeviceView extends RelativeLayout {

    private DevicePosition position;
    private IDeviceViewListener listener;

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

        this.findViewById(R.id.theStatusOrb).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fireOpenRequested();
            }
        });
    }

    /**
     * Notify the listener of an opening request if it is existing
     */
    private void fireOpenRequested() {
        if(listener!=null){
            listener.openRequested(this);
        }
    }
}