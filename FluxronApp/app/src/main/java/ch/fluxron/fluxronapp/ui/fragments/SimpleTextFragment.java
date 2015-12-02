package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;

/**
 * A simple fragment displaying just text. Can be used for debugging or to cap unimplemented areas
 * in the application.
 */
public class SimpleTextFragment extends Fragment {
    /**
     * Creates the fragment
     *
     * @param savedInstanceState State
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates the simple text view
     *
     * @param inflater           Inflater
     * @param container          Container
     * @param savedInstanceState State
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(container.getContext());
        textView.setText(R.string.no_view_found);
        return textView;
    }
}
