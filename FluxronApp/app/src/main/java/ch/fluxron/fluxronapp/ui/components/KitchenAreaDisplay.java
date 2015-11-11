package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ch.fluxron.fluxronapp.objectBase.DevicePosition;
import ch.fluxron.fluxronapp.objectBase.KitchenArea;
import ch.fluxron.fluxronapp.ui.activities.DeviceActivity;
import ch.fluxron.fluxronapp.ui.util.Camera;

/**
 * Displays a big image or parts of it
 */
public class KitchenAreaDisplay extends View implements IDeviceViewListener {
    public interface IKitchenAreaListener {
        void devicePositionChanged(KitchenArea area, String deviceId, int x, int y);
        void deviceDeleted(KitchenArea area, String deviceId);
    }

    // States
    private boolean editMode = false;

    // Zoom variables
    private float maxZoom = 1;
    private float minZoom = 0.5f;
    private Camera cam;
    private PointF currentDragStart;
    private PointF cameraDragStartTranslation;
    private ScaleGestureDetector detector;
    private float bmpWidth;
    private float bmpHeight;

    // Listener
    private IKitchenAreaListener listener;

    // Bitmaps and device positions
    private Bitmap[] splitMaps = new Bitmap[4];
    private List<DeviceView> views;
    private KitchenArea area;
    private int splitArraySide;
    private int splitHeight;
    private int splitWidth;

    // Last render time to optimize framerate
    private long lastRenderTimeMilliseconds;

    public KitchenAreaDisplay(Context context) {
        super(context);
        setUp();
    }

    public KitchenAreaDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
    }

    public KitchenAreaDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long start = System.currentTimeMillis();
        canvas.save();

        canvas.clipRect(0, 0, getWidth(), getHeight());
        canvas.concat(cam.getTransformMatrix());

        // render the image consisting out of the parts
        if(splitMaps.length > 0 && splitMaps[0] != null) {
            for(int x = 0; x < splitArraySide; x++) {
                for (int y = 0; y < splitArraySide; y++) {
                    canvas.drawBitmap(splitMaps[y * splitArraySide + x], (x*splitWidth), (y*splitHeight), null);
                }
            }
        }

        canvas.restore();
        float theZoom = cam.getScale();
        canvas.translate(cam.getTranslation().x*theZoom, cam.getTranslation().y*theZoom);

        // Draw the positions for the devices
        if (views!= null) {
            for (DeviceView p : views){
                canvas.save();
                canvas.translate(p.getPosition().getPosition().x*theZoom, p.getPosition().getPosition().y*theZoom);
                p.draw(canvas);
                canvas.restore();
            }
        }
    }

    private void limitTranslation() {
        PointF f = cam.copyTranslation();

        f.x = Math.min(0, f.x);
        f.x = Math.max(-bmpWidth*cam.getScale()+getWidth(), f.x);

        f.y = Math.min(0, f.y);
        f.y = Math.max(-bmpHeight*cam.getScale()+getHeight(), f.y);

        cam.setTranslation(f.x, f.y);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = getPaddingBottom() + getPaddingTop() + getSuggestedMinimumHeight();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean touchHandled = false;

        // Map from cam space to world space
        float theZoom = cam.getScale();
        float[] p = {event.getX(), event.getY()};
        Matrix camSpaceToWorldSpace = new Matrix();
        camSpaceToWorldSpace.setTranslate(-cam.getTranslation().x*theZoom, -cam.getTranslation().y*theZoom);
        camSpaceToWorldSpace.mapPoints(p);

        // Notify child views of the touch event in reverse order
        // because the last one drawn is the topmost element
        for(int i = views.size()-1; i>= 0 && !touchHandled;i--) {
            DeviceView child = views.get(i);

            // Copy the click event and translate it to the control space
            MotionEvent translatedCopy = MotionEvent.obtain(event);
            translatedCopy.setLocation(p[0]-child.getPosition().getPosition().x*theZoom,p[1]-child.getPosition().getPosition().y*theZoom);

            // Send to the child
            touchHandled = child.dispatchTouchEvent(translatedCopy);
        }

        // We do not want to scroll and touch child controls at the same time
        if (touchHandled){
            invalidate();
            return true;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                currentDragStart = new PointF(event.getX(), event.getY());
                cameraDragStartTranslation = cam.copyTranslation();
                break;

            case MotionEvent.ACTION_MOVE:
                PointF dragTranslation = new PointF();
                dragTranslation.x = event.getX() - currentDragStart.x;
                dragTranslation.y = event.getY() - currentDragStart.y;
                dragTranslation.x /= cam.getScale();
                dragTranslation.y /= cam.getScale();
                cam.setTranslation(cameraDragStartTranslation.x + dragTranslation.x, cameraDragStartTranslation.y + dragTranslation.y);
                break;
        }

        limitTranslation();
        needsRepaint(false);
        detector.onTouchEvent(event);
        return true;
    }

    private void setUp() {
        cam = new Camera();
        cam.setScale(1);

        views = new ArrayList<>();

        detector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                    float oldScaleFactor = cam.getScale();
                    float scaleFactor = oldScaleFactor * detector.getScaleFactor();
                    scaleFactor = Math.max(minZoom, Math.min(scaleFactor, maxZoom));
                    if (scaleFactor != oldScaleFactor) {
                        float scaleDifference = scaleFactor / oldScaleFactor;
                        float zoomTranslationX = (1 - scaleDifference) * detector.getFocusX() / scaleFactor;
                        float zoomTranslationY = (1 - scaleDifference) * detector.getFocusY() / scaleFactor;
                        cam.setScaleAndRelativeTranslate(scaleFactor,
                                zoomTranslationX,
                                zoomTranslationY);
                        invalidate();
                    }
                    return true;

                }
        });
    }


    /**
     * Sets the full sized bitmap to display
     * @param bmp
     */
    public void setBitmap(Bitmap bmp){
        splitArraySide = (int)Math.sqrt(splitMaps.length);
        splitHeight = bmp.getHeight() / splitArraySide;
        splitWidth = bmp.getWidth() / splitArraySide;

        bmpWidth = bmp.getWidth();
        bmpHeight = bmp.getHeight();

        minZoom = bmp.getWidth() > bmp.getHeight() ? (float)getHeight() / (float)bmp.getHeight() : (float)getWidth() / (float)bmp.getWidth();
        cam.setScale(minZoom);

        for(int x = 0; x < splitArraySide; x++){
            for(int y = 0; y < splitArraySide; y++){
                splitMaps[y * splitArraySide + x] = Bitmap.createBitmap(bmp, x * splitWidth, y * splitHeight, splitWidth, splitHeight);
            }
        }

        needsRepaint(true);
    }

    public void setListener(IKitchenAreaListener listener) {
        this.listener = listener;
    }

    public void setDevicePositions(KitchenArea area){
        this.area = area;
        for(DevicePosition d : area.getDevicePositionList()){
            DeviceView deviceRenderer = createDeviceView(d);
            views.add(deviceRenderer);
        }
        needsRepaint(true);
    }

    @NonNull
    private DeviceView createDeviceView(DevicePosition d) {
        DeviceView deviceRenderer = new DeviceView(getContext());
        deviceRenderer.setDeviceAddress(d.getDeviceId());
        deviceRenderer.setDeviceType(d.getCategory());
        deviceRenderer.setPosition(d);
        deviceRenderer.setListener(this);
        return deviceRenderer;
    }

    @Override
    public boolean moveRequested(DeviceView v, int dx, int dy, boolean finalPosition) {
        if (editMode){
            int x = v.getPosition().getPosition().x += dx;
            int y = v.getPosition().getPosition().y += dy;

            if (finalPosition && listener!=null){
                listener.devicePositionChanged(this.area, v.getPosition().getDeviceId(), x, y);
            }
        }

        return editMode;
    }

    @Override
    public void actionRequested(DeviceView v) {
        if(editMode) {
            v.askForDelete();
            return;
        }
        Intent startActivity = new Intent(this.getContext(), DeviceActivity.class);
        startActivity.putExtra("DEVICE_ID", v.getDeviceAddress());
        startActivity.putExtra("DEVICE_TYPE", v.getDeviceType());
        getContext().startActivity(startActivity);
    }

    @Override
    public void needsRepaint(boolean force) {
        long deltaMillis = System.currentTimeMillis() - lastRenderTimeMilliseconds ;

        // it's either a forced redraw, or we need to check if we are over 60 fps
        if (force || deltaMillis > (1000/60)) {
            invalidate();
            lastRenderTimeMilliseconds = System.currentTimeMillis();
        }
    }

    @Override
    public void deleteRequested(DeviceView v) {
        if(listener != null){
            listener.deviceDeleted(area, v.getPosition().getDeviceId());
        }
    }

    /**
     * Sets wether the display should allow editing or not
     * @param edit Editing or not
     */
    public void setEditMode(boolean edit){
        editMode = edit;
    }

    /**
     * Adds or changes the position of a device
     * @param devicePosition Position of a device
     */
    public void setDevicePosition(DevicePosition devicePosition) {
        // Find the view with the changed position
        DevicePosition found = null;
        for(DeviceView deviceView : views) {
            if(deviceView.getPosition().getDeviceId().equals(devicePosition.getDeviceId())) {
                found = deviceView.getPosition();
                break;
            }
        }

        // If we found the view, update it's position,
        // otherwise we'll need to create a new one
        if (found!=null) {
            found.setPosition(devicePosition.getPosition());
            needsRepaint(false);
        }
        else {
            DeviceView deviceRenderer = createDeviceView(devicePosition);
            deviceRenderer.popUp();
            views.add(deviceRenderer);
            needsRepaint(true);
        }
    }

    public void removePosition(String deviceId) {
        for(DeviceView deviceView : views) {
            if(deviceView.getPosition().getDeviceId().equals(deviceId)) {
                deviceView.remove();
                break;
            }
        }
    }
}