package ch.fluxron.fluxronapp.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private int scrollPos = 0;

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
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        kitchenListView.setLayoutManager(layoutManager);

        // Item decoration for the list
        SpacesItemDecoration deco = new SpacesItemDecoration(30);
        kitchenListView.addItemDecoration(deco);

        // List adapter
        listAdapter = new AreaListAdapter(this.listener, this.provider);
        kitchenListView.setAdapter(listAdapter);

        // Scroll listener
        kitchenListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    snapListAndNotify(recyclerView, layoutManager);
                }
            }
        });

        return listView;
    }

    private void snapListAndNotify(RecyclerView v, LinearLayoutManager m) {
        // Find the middle of the visible view positions
        int firstVisibleView = m.findFirstVisibleItemPosition();
        int lastVisibleView = m.findLastVisibleItemPosition();
        int controlCenter = v.getWidth()/2;

        int scrollDistanceMin = Integer.MAX_VALUE;
        int scrollPosition = -1;
        for(int i = firstVisibleView; i <= lastVisibleView; i++) {
            View currentView = m.findViewByPosition(i);
            int viewCenter = (currentView.getLeft()+ currentView.getRight())/2;
            int dx =  viewCenter - controlCenter;
            if(Math.abs(dx) < Math.abs(scrollDistanceMin)) {
                scrollDistanceMin = dx;
                scrollPosition = i;
            }
        }

        if(scrollDistanceMin != Integer.MAX_VALUE) {
            v.smoothScrollBy(scrollDistanceMin, 0);

            if (this.listener!=null){
                this.listener.areaScrolled(scrollPosition);
            }
        }
    }

    public AreaListAdapter getListAdapter() {
        return listAdapter;
    }
}