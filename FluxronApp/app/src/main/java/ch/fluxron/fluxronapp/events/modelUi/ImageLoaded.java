package ch.fluxron.fluxronapp.events.modelUi;

import android.graphics.Bitmap;

import ch.fluxron.fluxronapp.events.base.RequestResponseConnection;

/**
 * Notifies subscribers that an image has been loaded
 */
public class ImageLoaded extends RequestResponseConnection{
    private Bitmap bmp;

    /**
     * Sets the Bitmap that was loaded
     * @param bmp Bitmap
     */
    public ImageLoaded(Bitmap bmp) {
        this.bmp = bmp;
    }

    /**
     * Bitmap that was loaded
     * @return Bitmap
     */
    public Bitmap getBmp() {
        return bmp;
    }
}
