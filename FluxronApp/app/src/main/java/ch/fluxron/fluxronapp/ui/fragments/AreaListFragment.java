package ch.fluxron.fluxronapp.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.ui.adapters.AreaListAdapter;
import ch.fluxron.fluxronapp.ui.adapters.IAreaClickedListener;
import ch.fluxron.fluxronapp.ui.decorators.SpacesItemDecoration;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Implements a fragment that displays a list of kitchen areas
 */
public class AreaListFragment extends Fragment {
    public static final String KITCHEN_ID = "kitchen_id";

    private String kitchenId;
    private IEventBusProvider provider;
    private AreaListAdapter listAdapter;
    private IAreaClickedListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            kitchenId = savedInstanceState.getString(KITCHEN_ID);
        }
        else if (getArguments() != null) {
            kitchenId = getArguments().getString(KITCHEN_ID);
        }
    }

    public void setEventBusProvider(IEventBusProvider provider) {
        this.provider = provider;
    }

    public void setClickListener(IAreaClickedListener listener){
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KITCHEN_ID, kitchenId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View listView = getActivity().getLayoutInflater().inflate(R.layout.fragment_area_list, container, false);

        // Set the list's properties
        RecyclerView kitchenListView = (RecyclerView) listView.findViewById(R.id.areaList);

        // Layout for the list
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        kitchenListView.setLayoutManager(layoutManager);

        // Item decoration for the list
        SpacesItemDecoration deco = new SpacesItemDecoration(30);
        kitchenListView.addItemDecoration(deco);

        // List adapter
        listAdapter = new AreaListAdapter(this.listener, this.provider);
        kitchenListView.setAdapter(listAdapter);

        return listView;
    }

    public AreaListAdapter getListAdapter() {
        return listAdapter;
    }
}
