package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import ch.fluxron.fluxronapp.objectBase.DevicePosition;

/**
 * Displays a big image or parts of it
 */
public class KitchenAreaDisplay extends View {
    private Bitmap[] splitMaps = new Bitmap[4];
    private float currentX;
    private float currentY;
    private float scrollPosX;
    private float scrollPosY;
    private float maxScrollX;
    private float maxScrollY;
    private List<DevicePosition> devices;
    private Paint deviceDefaultPaint;

    public KitchenAreaDisplay(Context context) {
        super(context);
    }

    public KitchenAreaDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KitchenAreaDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        // render the image consisting out of the parts
        if(splitMaps.length > 0 && splitMaps[0] != null) {
            int splitArraySide = (int)Math.sqrt(splitMaps.length);
            int imageWidth = splitMaps[0].getWidth();
            int imageHeight = splitMaps[0].getHeight();
            for(int x = 0; x < splitArraySide; x++) {
                for (int y = 0; y < splitArraySide; y++) {
                    canvas.drawBitmap(splitMaps[y * splitArraySide + x], scrollPosX+x*imageWidth, scrollPosY+y*imageHeight, null);
                }
            }
        }

        deviceDefaultPaint = new Paint();
        deviceDefaultPaint.setColor(Color.rgb(255,0,50));

        // Draw the positions for the devices
        if (devices!= null) {
            for (DevicePosition p : devices){
                canvas.drawCircle(scrollPosX + p.getPosition().x, scrollPosY + p.getPosition().y, 30, deviceDefaultPaint);
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

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            currentX = event.getRawX();
            currentY = event.getRawY();
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getRawX();
            float y = event.getRawY();

            // Update absolute values with delta and clip inside maximum
            // scrolling rectangle
            scrollPosX = Math.max(-maxScrollX, Math.min(0, scrollPosX + x - currentX));
            scrollPosY = Math.max(-maxScrollY, Math.min(0, scrollPosY + y - currentY));

            // Store last touch position for next delta calculation
            currentX = x;
            currentY = y;

            invalidate();
        }

        // Consume the event
        return true;
    }
}