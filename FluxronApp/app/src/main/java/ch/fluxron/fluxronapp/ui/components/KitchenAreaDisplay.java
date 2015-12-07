package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    /**
     * Listener
     */
    public interface IKitchenAreaListener {
        void devicePositionChanged(KitchenArea area, String deviceId, int x, int y);
        void deviceDeleted(KitchenArea area, String deviceId);
    }

    // States
    private boolean editMode = false;

    // Zoom variables
    private float maxZoom = 1;
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

    /**
     * New display view
     * @param context Context
     */
    public KitchenAreaDisplay(Context context) {
        super(context);
        setUp();
    }

    /**
     * New display view
     * @param context Context
     * @param attrs Attributes
     */
    public KitchenAreaDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
    }

    /**
     * New display view
     * @param context Context
     * @param attrs Attributes
     */
    public KitchenAreaDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp();
    }

    public void initDeviceViews(){
        for(DeviceView dv:views){
            dv.initProvider();
        }
    }

    /**
     * Draws all the elements
     * @param canvas Canvas to draw on
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        canvas.clipRect(0, 0, getWidth(), getHeight());
        canvas.concat(cam.getTransformMatrix());

        // render the image consisting out of the parts
        if(splitMaps.length > 0 && splitMaps[0] != null) {
            for(int x = 0; x < splitArraySide; x++) {
                for (int y = 0; y < splitArraySide; y++) {
                    if (splitMaps[y * splitArraySide + x] != null)
                        canvas.drawBitmap(splitMaps[y * splitArraySide + x], (x*splitWidth), (y*splitHeight), null);
                }
            }
        }

        canvas.restore();
        float theZoom = cam.getScale();
        canvas.translate(cam.getTranslation().x * theZoom, cam.getTranslation().y * theZoom);

        // Draw the positions for the devices
        List<DeviceView> toDelete = new ArrayList<>(1);
        if (views!= null) {
            for (DeviceView p : views){
                canvas.save();
                canvas.translate(p.getPosition().getPosition().x*theZoom, p.getPosition().getPosition().y*theZoom);
                p.draw(canvas);
                canvas.restore();

                // Device views that finished all animations and are in a deleted state
                // should be removed
                if (p.isDeleted()) {
                    toDelete.add(p);
                }
            }

            // Remove after the render loop to avoid inconsistencies in the render order
            for (DeviceView view : toDelete) {
                view.cleanUp();
                views.remove(view);
            }
        }
    }

    /**
     * Limits the scrolling so you can't scroll away
     */
    private void limitTranslation() {
        PointF f = cam.copyTranslation();

        f.x = Math.min(0, f.x);
        f.x = Math.max(-bmpWidth*cam.getScale()+getWidth(), f.x);

        f.y = Math.min(0, f.y);
        f.y = Math.max(-bmpHeight*cam.getScale()+getHeight(), f.y);

        cam.setTranslation(f.x, f.y);
    }

    /**
     * Measures the controls size
     * @param widthMeasureSpec Spec
     * @param heightMeasureSpec Spec
     */
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

    /**
     * Touch event registered
     * @param event Event
     * @return Handled or not
     */
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

    /**
     * Sets up all the listeners this control needs
     */
    private void setUp() {
        cam = new Camera();
        cam.setScale(1);

        views = new ArrayList<>();

        detector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                    float oldScaleFactor = cam.getScale();
                    float scaleFactor = oldScaleFactor * detector.getScaleFactor();
                    scaleFactor = Math.max(getMinZoom(), Math.min(scaleFactor, maxZoom));
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
     * Background task for image splitting
     */
    private class SplitImageTask extends AsyncTask<Bitmap, Object, Object> {
        /**
         * Splits the image in the background
         * @param params Exactly one Bitmap
         * @return null
         */
        @Override
        protected Object doInBackground(Bitmap... params) {
            Bitmap bmp = params[0];
            for(int x = 0; x < splitArraySide; x++){
                for(int y = 0; y < splitArraySide; y++){
                    splitMaps[y * splitArraySide + x] = Bitmap.createBitmap(bmp, x * splitWidth, y * splitHeight, splitWidth, splitHeight);
                }
            }
            return null;
        }

        /**
         * Requests a repaint after the splitting is done
         * @param o Object
         */
        @Override
        protected void onPostExecute(Object o) {
            needsRepaint(true);
        }
    }


    /**
     * Sets the full sized bitmap to display
     * @param bmp Bitmap
     */
    public void setBitmap(Bitmap bmp){
        splitArraySide = (int)Math.sqrt(splitMaps.length);
        splitHeight = bmp.getHeight() / splitArraySide;
        splitWidth = bmp.getWidth() / splitArraySide;

        bmpWidth = bmp.getWidth();
        bmpHeight = bmp.getHeight();

        cam.setScale(getMinZoom());

        new SplitImageTask().execute(bmp);
    }

    /**
     * Sets the listener for area events
     * @param listener Listener
     */
    public void setListener(IKitchenAreaListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the positions of all devices
     * @param area Area
     */
    public void setDevicePositions(KitchenArea area){
        this.area = area;
        for(DevicePosition d : area.getDevicePositionList()){
            DeviceView found = findDeviceView(d);
            if (found==null) {
                DeviceView deviceRenderer = createDeviceView(d);
                views.add(deviceRenderer);
            }
        }
        needsRepaint(true);
    }

    /**
     * Creates a new device view
     * @param d Position
     * @return View
     */
    @NonNull
    private DeviceView createDeviceView(DevicePosition d) {
        DeviceView deviceRenderer = new DeviceView(getContext());
        deviceRenderer.setDeviceAddress(d.getDeviceId());
        deviceRenderer.setDeviceType(d.getCategory());
        deviceRenderer.setDeviceName(d.getName());
        deviceRenderer.setPosition(d);
        deviceRenderer.setListener(this);
        return deviceRenderer;
    }

    /**
     * Move of a view is requested
     * @param v View
     * @param dx Delta x
     * @param dy Delta y
     * @param finalPosition Final movement?
     * @return Move accepted
     */
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

    /**
     * Device should perform an action
     * @param v View
     */
    @Override
    public void actionRequested(DeviceView v) {
        if(editMode) {
            v.askForDelete();
            return;
        }
        Intent startActivity = new Intent(this.getContext(), DeviceActivity.class);
        startActivity.putExtra("DEVICE_ID", v.getDeviceAddress());
        startActivity.putExtra("DEVICE_NAME", v.getDeviceName());
        getContext().startActivity(startActivity);
    }

    /**
     * Repaint needed, keep fps in check
     * @param force Disable frame rate check
     */
    @Override
    public void needsRepaint(boolean force) {
        long deltaMillis = System.currentTimeMillis() - lastRenderTimeMilliseconds ;

        // it's either a forced redraw, or we need to check if we are over 60 fps
        if (force || deltaMillis > (1000/60)) {
            invalidate();
            lastRenderTimeMilliseconds = System.currentTimeMillis();
        }
    }

    /**
     * Delete of a device was requested
     * @param v View
     */
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

    public void setDevicePosition(DevicePosition devicePosition){
        setDevicePosition(devicePosition, DeviceView.DEVICE_STATUS_UNKNOWN);
    }

    /**
     * Adds or changes the position of a device
     * @param devicePosition Position of a device
     */
    public void setDevicePosition(DevicePosition devicePosition, int status) {
        // Find the view with the changed position
        DeviceView foundView = findDeviceView(devicePosition);

        // If we found the view, update it's position,
        // otherwise we'll need to create a new one
        if (foundView!=null) {
            foundView.setDeviceType(devicePosition.getCategory());
            DevicePosition found = foundView.getPosition();
            found.setPosition(devicePosition.getPosition());
            found.setCategory(devicePosition.getCategory());
            needsRepaint(false);
        }
        else {
            DeviceView deviceRenderer = createDeviceView(devicePosition);
            deviceRenderer.popUp();
            views.add(deviceRenderer);
            needsRepaint(true);
        }
    }

    /**
     * Finds a device view by DevicePosition
     * @param other Search key
     * @return Found view or null
     */
    @Nullable
    private DeviceView findDeviceView(DevicePosition other) {
        DeviceView found = null;
        for(DeviceView deviceView : views) {
            if(deviceView.getPosition().getDeviceId().equals(other.getDeviceId())) {
                found = deviceView;
                break;
            }
        }
        return found;
    }

    /**
     * Removes a view from the area
     * @param deviceId Id
     */
    public void removePosition(String deviceId) {
        for(DeviceView deviceView : views) {
            if(deviceView.getPosition().getDeviceId().equals(deviceId)) {
                deviceView.remove();
                break;
            }
        }
    }

    /**
     * Returns the minimal zoom to fully see the image
     * @return Minimal zoom
     */
    private float getMinZoom() {
        return Math.min((float)getHeight() / bmpHeight, (float)getWidth() / bmpWidth);
    }

    /**
     * Gets the center of the camera
     * @return Center of the camera
     */
    public Point getCenterPosition() {
        PointF c =cam.getAsUntransformedCoordinates(getWidth()/2, getHeight()/2);

        return new Point((int)c.x, (int)c.y);
    }

    /**
     * Cleans up any dangling references
     */
    public void cleanUp(){
        for(DeviceView deviceView : views) {
            deviceView.cleanUp();
        }
    }
}