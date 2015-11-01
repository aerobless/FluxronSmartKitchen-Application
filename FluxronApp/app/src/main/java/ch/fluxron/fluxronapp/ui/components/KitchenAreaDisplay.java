package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.List;

import ch.fluxron.fluxronapp.objectBase.DevicePosition;
import ch.fluxron.fluxronapp.ui.util.Camera;

/**
 * Displays a big image or parts of it
 */
public class KitchenAreaDisplay extends View {
    // States
    protected static int ACTION_MODE_NONE = 0;
    protected static int ACTION_MODE_DRAG = 1;
    protected static int ACTION_MODE_ZOOM = 2;

    // Zoom variables
    private float maxZoom = 1;
    private float minZoom = 0.5f;
    private Camera cam;
    private int currentAction;
    private PointF currentDragStart;
    private PointF cameraDragStartTranslation;
    private ScaleGestureDetector detector;

    // Bitmaps and device positions
    private Bitmap[] splitMaps = new Bitmap[4];
    private List<DevicePosition> devices;
    private Paint deviceDefaultPaint;

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

    private float getDragDistanceFromStart(MotionEvent event) {
        if (currentDragStart != null) {
            float dx = currentDragStart.x - event.getX();
            float dy = currentDragStart.y - event.getY();
            return (float)Math.sqrt(dx * dx + dy * dy);
        }
        return 0.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        canvas.concat(cam.getTransformMatrix());

        // render the image consisting out of the parts
        if(splitMaps.length > 0 && splitMaps[0] != null) {
            int splitArraySide = (int)Math.sqrt(splitMaps.length);
            int imageWidth = splitMaps[0].getWidth();
            int imageHeight = splitMaps[0].getHeight();
            for(int x = 0; x < splitArraySide; x++) {
                for (int y = 0; y < splitArraySide; y++) {
                    canvas.drawBitmap(splitMaps[y * splitArraySide + x], (x*imageWidth), (y*imageHeight), null);
                }
            }
        }

        deviceDefaultPaint = new Paint();
        deviceDefaultPaint.setColor(Color.rgb(255, 0, 50));

        // Draw the positions for the devices
        if (devices!= null) {
            for (DevicePosition p : devices){
                canvas.drawCircle((p.getPosition().x), (p.getPosition().y), 30, deviceDefaultPaint);
            }
        }

        canvas.restore();
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
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                currentDragStart = new PointF(event.getX(), event.getY());
                cameraDragStartTranslation = cam.copyTranslation();
                currentAction = ACTION_MODE_DRAG;
                break;

            case MotionEvent.ACTION_MOVE:
                PointF dragTranslation = new PointF();
                dragTranslation.x = event.getX() - currentDragStart.x;
                dragTranslation.y = event.getY() - currentDragStart.y;
                dragTranslation.x /= cam.getScale();
                dragTranslation.y /= cam.getScale();
                cam.setTranslation(cameraDragStartTranslation.x + dragTranslation.x, cameraDragStartTranslation.y + dragTranslation.y);
                invalidate();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                currentAction = ACTION_MODE_ZOOM;
                break;

            case MotionEvent.ACTION_UP:
                currentAction = ACTION_MODE_NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                currentAction = ACTION_MODE_NONE;
                break;
        }

        detector.onTouchEvent(event);
        return true;
    }

    private void setUp() {
        cam = new Camera();
        cam.setScale(1);

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
        int splitArraySide = (int)Math.sqrt(splitMaps.length);
        int splitHeight = bmp.getHeight() / splitArraySide;
        int splitWidth = bmp.getWidth() / splitArraySide;

        for(int x = 0; x < splitArraySide; x++){
            for(int y = 0; y < splitArraySide; y++){
                splitMaps[y * splitArraySide + x] = Bitmap.createBitmap(bmp, x * splitWidth, y * splitHeight, splitWidth, splitHeight);
            }
        }

        invalidate();
    }

    public void setDevicePositions(List<DevicePosition> devices){
        this.devices = devices;
        invalidate();
    }

}