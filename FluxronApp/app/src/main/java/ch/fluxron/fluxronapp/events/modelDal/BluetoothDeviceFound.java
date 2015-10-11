package ch.fluxron.fluxronapp.events.modelDal;

import java.util.Date;

/**
 * Notifies listeners that a bluetooth device has been found
 */
public class BluetoothDeviceFound {
    private Date date;
    private String name;
    private String address;

    public BluetoothDeviceFound(Date date, String name, String address) {
        this.date = date;
        this.name = name;
        this.address = address;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}


