package com.example.jacekpodwysocki.soundie;

import static com.example.jacekpodwysocki.soundie.R.id.songArtist;
import static com.example.jacekpodwysocki.soundie.R.id.songTitle;

/**
 * Created by jacekpodwysocki on 11/11/2016.
 */

public class RowItemDevices {
    private String deviceName;
    private String deviceAddress;

    public RowItemDevices(String deviceName, String deviceAddress){
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceName(){
        return deviceName;
    }
    public String getDeviceAddress(){
        return deviceAddress;
    }


}
