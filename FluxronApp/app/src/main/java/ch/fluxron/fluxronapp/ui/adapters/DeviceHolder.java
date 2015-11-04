package ch.fluxron.fluxronapp.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ch.fluxron.fluxronapp.R;
import ch.fluxron.fluxronapp.objectBase.Device;
import ch.fluxron.fluxronapp.ui.util.IEventBusProvider;

/**
 * Holds one device view
 */
public class DeviceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView deviceName;
    private TextView deviceAddress;
    private Button addButton;
    private View parent;
    private Device boundData;
    private IDeviceClickListener listener;
    //private IEventBusProvider provider;

    public DeviceHolder(View itemView, IDeviceClickListener listener, IEventBusProvider provider) {
        super(itemView);

        this.parent = itemView;
        this.deviceName = (TextView) itemView.findViewById(R.id.deviceName);
        this.deviceAddress = (TextView) itemView.findViewById(R.id.deviceAddress);
        this.listener = listener;
        this.addButton = (Button)itemView.findViewById(R.id.addButton);
       // this.provider = provider;
        //this.provider.getUiEventBus().register(this);

        parent.setOnClickListener(this);
        addButton.setOnClickListener(this);

    }

    public void bind(final Device d){
        boundData = d;
        deviceName.setText(d.getName());
        deviceAddress.setText(d.getAddress());
    }

    @Override
    public void onClick(View v) {
        if(listener != null && !(v instanceof Button)){
            listener.deviceClicked(boundData);
        }
        else if (listener != null){
            listener.deviceButtonPressed(boundData);
        }
    }
}
