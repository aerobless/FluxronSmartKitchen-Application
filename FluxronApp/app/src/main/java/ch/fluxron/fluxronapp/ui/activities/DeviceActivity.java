package ch.fluxron.fluxronapp.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.ui.activities.common.FluxronBaseActivity;
import ch.fluxron.fluxronapp.ui.adapters.DeviceFragmentAdapter;

public class DeviceActivity extends FluxronBaseActivity {
    String address = "Unkown";
    String type = "Unkown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        ViewPager viewPager = (ViewPager) findViewById(R.id.deviceViewPager);
        viewPager.setAdapter(new DeviceFragmentAdapter(getSupportFragmentManager(), this));

        TabLayout tabs = (TabLayout)findViewById(R.id.deviceTabs);
        tabs.setupWithViewPager(viewPager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            address = extras.getString("DEVICE_ID");
            type = extras.getString("DEVICE_TYPE");
        }

        ((TextView)findViewById(R.id.deviceStatusName)).setText(type); //TODO: replace with Device Name once we have it
        ((TextView)findViewById(R.id.deviceStatusDescription)).setText(address);
    }

    public void onEventMainThread(Object msg){
        // TODO: Add event handling for device activity
        postMessage(null);
    }

    public void onBackButtonClicked(View button){
        // Close this activity and navigate back to the activity
        // that is below on the stack.
        finish();
    }

    public void onSampleText(View btn){
        //TemperatureBar tb = (TemperatureBar) findViewById(R.id.barTemp);
        //tb.setMinMax(0,0);
    }
}
