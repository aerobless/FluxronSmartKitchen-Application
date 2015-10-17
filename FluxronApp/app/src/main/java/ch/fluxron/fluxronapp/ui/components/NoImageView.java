package ch.fluxron.fluxronapp.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import ch.fluxron.fluxronapp.R;

/**
 * Represents rectangle that states a "no image found" to the user
 */
public class NoImageView extends FrameLayout {
    /**
     * Create a new temperature bar
     * @param context Context
     * @param attrs Attributes
     */
    public NoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.compound_noimage_view, this, true);
    }
}
