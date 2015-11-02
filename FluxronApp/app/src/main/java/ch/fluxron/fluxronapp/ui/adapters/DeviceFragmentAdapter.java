package ch.fluxron.fluxronapp.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.ui.fragments.SimpleTextFragment;

/**
 * Adapter for the sliding tab view in the device detail screen
 */
public class DeviceFragmentAdapter extends FragmentPagerAdapter {
    private Context context;
    private int[] tabTitleResIds = new int[] { R.string.device_tab_status, R.string.device_tab_usage, R.string.device_tab_errors, R.string.device_tab_params };

    public DeviceFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return new SimpleTextFragment();
    }

    @Override
    public int getCount() {
        return tabTitleResIds.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(tabTitleResIds[position]);
    }
}
