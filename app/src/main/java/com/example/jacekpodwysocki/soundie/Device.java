package com.example.jacekpodwysocki.soundie;

/**
 * Created by jacekpodwysocki on 20/11/2016.
 */

public class Device {
    private String deviceName;
    private String macAddress;

    public Device(String deviceName, String macAddress){
        this.deviceName=deviceName;
        this.macAddress=macAddress;
    }

    public String getDeviceName(){return deviceName;}
    public String getMacAddress(){return macAddress;}
}
