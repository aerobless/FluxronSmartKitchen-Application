package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple fragment displaying just text. Can be used for debugging or to cap unimplemented areas
 * in the application.
 */
public class SimpleTextFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(container.getContext());
        textView.setText("Some view");
        return textView;
    }
}
