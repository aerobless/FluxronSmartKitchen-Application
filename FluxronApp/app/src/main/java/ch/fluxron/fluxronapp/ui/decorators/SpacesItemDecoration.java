package ch.fluxron.fluxronapp.ui.decorators;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Adds equal spacing decoration to a horizontal list
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    /**
     * Creates a new space decorator
     * @param space Space that should be between two controls
     */
    public SpacesItemDecoration(int space){
        this.space = space;
    }

    /**
     * Returns the offsets of the icons
     * @param outRect Rectangle
     * @param view View
     * @param parent Parent view
     * @param state State
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // All items but the first one get a left margin
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.left += space;
        }
    }
}
