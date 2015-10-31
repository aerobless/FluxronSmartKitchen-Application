package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.List;

import ch.fluxron.fluxronapp.objectBase.DevicePosition;

/**
 * Displays a big image or parts of it
 */
public class KitchenAreaDisplay extends View {
    private Bitmap[] splitMaps = new Bitmap[4];
    private float zoom = 1;
    private float currentX;
    private float currentY;
    private float scrollPosX;
    private float scrollPosY;
    private float maxScrollX;
    private float maxScrollY;
    private float bmpWidth;
    private float bmpHeight;
    private List<DevicePosition> devices;
    private Paint deviceDefaultPaint;
    private ScaleGestureDetector gestureDetector;

    public KitchenAreaDisplay(Context context) {
        super(context);
        setUpDetector();
    }

    public KitchenAreaDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpDetector();
    }

    public KitchenAreaDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpDetector();
    }

    private void setUpDetector() {
        gestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                zoom = Math.max(0.1f, Math.min(1, zoom * detector.getScaleFactor()));
                maxScrollX = bmpWidth*zoom - getMeasuredWidth();
                maxScrollY = bmpHeight*zoom - getMeasuredHeight();
                invalidate();
                return true;
            }
        });
    }

    /**
     * Sets the full sized bitmap to display
     * @param bmp
     */
    public void setBitmap(Bitmap bmp){
        // Position the scrolling position so the image starts out at the center
        this.scrollPosY =-(bmp.getHeight() / 2 - getMeasuredHeight() / 2);
        this.scrollPosX =-(bmp.getWidth() / 2 - getMeasuredWidth() / 2);

        this.maxScrollX = bmp.getWidth() - getMeasuredWidth();
        this.maxScrollY = bmp.getHeight() - getMeasuredHeight();

        bmpWidth = bmp.getWidth();
        bmpHeight = bmp.getHeight();

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

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(scrollPosX, scrollPosY);
        canvas.scale(zoom,zoom);

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
        gestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN && !gestureDetector.isInProgress()) {
            currentX = event.getRawX();
            currentY = event.getRawY();
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE && !gestureDetector.isInProgress()) {
            float x = event.getRawX();
            float y = event.getRawY();

            // Update absolute values with delta and clip inside maximum
            // scrolling rectangle
            scrollPosX = Math.min(0, Math.max(-maxScrollX, scrollPosX + x - currentX));
            scrollPosY = Math.min(0, Math.max(-maxScrollY, scrollPosY + y - currentY));

            // Store last touch position for next delta calculation
            currentX = x;
            currentY = y;

            invalidate();
        }

        // Consume the event
        return true;
    }
}