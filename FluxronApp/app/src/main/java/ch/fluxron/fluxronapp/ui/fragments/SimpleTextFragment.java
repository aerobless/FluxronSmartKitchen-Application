package ch.fluxron.fluxronapp.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragment that displays a simple text
 */
public class SimpleTextFragment extends Fragment {
    /**
     * Fragment was created
     * @param savedInstanceState State
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates the view
     * @param inflater View inflater
     * @param container Container
     * @param savedInstanceState State
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(container.getContext());
        textView.setText("View not implemented.");
        return textView;
    }
}
