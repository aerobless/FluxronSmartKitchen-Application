package ch.fluxron.fluxronapp.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.ui.fragments.DeviceConfigFragment;
import ch.fluxron.fluxronapp.ui.fragments.DeviceErrorFragment;
import ch.fluxron.fluxronapp.ui.fragments.DeviceHistoryFragment;
import ch.fluxron.fluxronapp.ui.fragments.DeviceStatusFragment;
import ch.fluxron.fluxronapp.ui.fragments.SimpleTextFragment;

/**
 * Adapter for the sliding tab view in the device detail screen
 */
public class DeviceFragmentAdapter extends FragmentPagerAdapter {
    private Context context;
    private String address;
    private String deviceClass;
    private int[] tabTitleResIds = new int[]{R.string.device_tab_status, R.string.device_tab_usage, R.string.device_tab_errors, R.string.device_tab_params};

    /**
     * New device fragment adapter
     * @param fm Manager
     * @param context Context
     */
    public DeviceFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    /**
     * Returns the item at the position
     * @param position Position
     * @return Fragment
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                DeviceStatusFragment dfs = new DeviceStatusFragment();
                dfs.init(address, deviceClass);
                return dfs;
            case 1:
                DeviceHistoryFragment history = new DeviceHistoryFragment();
                history.init(address, deviceClass);
                return history;
            case 2:
                DeviceErrorFragment errors = new DeviceErrorFragment();
                errors.init(address, deviceClass);
                return errors;
            case 3:
                DeviceConfigFragment dgf = new DeviceConfigFragment();
                dgf.init(address, deviceClass);
                return dgf;
            default:
                return new SimpleTextFragment();
        }
    }

    /**
     * Returns the number of fragments to be displayed
     * @return Number of fragments
     */
    @Override
    public int getCount() {
        return tabTitleResIds.length;
    }

    /**
     * Gets the page title for an index
     * @param position Index
     * @return Page title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(tabTitleResIds[position]);
    }

    /**
     * Initializes the adapter
     * @param address Address
     * @param deviceClass Class name
     */
    public void init(String address, String deviceClass) {
        this.address = address;
        this.deviceClass = deviceClass;
    }
}
