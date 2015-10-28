package ch.fluxron.fluxronapp.ui.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import ch.fluxron.fluxronapp.ui.components.TemperatureBar;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Implements a fragment that displays a single kitchen area
 */
public class KitchenAreaFragment extends Fragment {
    public static final String KITCHEN_ID = "kitchen_id";
    public static final String AREA_RELATIVE_ID = "area_id";

    private String kitchenId;
    private int areaId;
    private IEventBusProvider provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            kitchenId = savedInstanceState.getString(KITCHEN_ID);
            areaId = savedInstanceState.getInt(AREA_RELATIVE_ID);
        }
        else if (getArguments() != null) {
            kitchenId = getArguments().getString(KITCHEN_ID);
            areaId = getArguments().getInt(AREA_RELATIVE_ID);
        }
    }

    public void setEventBusProvider(IEventBusProvider provider) {
        this.provider = provider;
    }

    @Override
    public void onStart() {
        super.onStart();

        provider.getUiEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        provider.getUiEventBus().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KITCHEN_ID, kitchenId);
        outState.putInt(AREA_RELATIVE_ID, areaId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ScrollView scroller = new ScrollView(getActivity());
        TextView text = new TextView(getActivity());
        scroller.addView(text);
        text.setText("Activity Test text");
        return scroller;
    }

    public void onEventMainThread(Object msg){

    }
}
