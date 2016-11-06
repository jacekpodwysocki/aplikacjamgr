package com.example.jacekpodwysocki.soundie;

/**
 * Created by jacekpodwysocki on 26/09/2016.
 */

public class AppConfig {
    // Server API POST/GET/PUT urls
    public static String APIURL = "http://79.96.213.74/";
    public static String URL_LOGIN = APIURL+"SoundieApi/v1/login";
    public static String URL_REGISTER = APIURL+"SoundieApi/v1/register";
    public static String URL_SAVELOCATION = APIURL+"SoundieApi/v1/savelocation";
    public static String URL_GETACTIVEUSERS = APIURL+"SoundieApi/v1/visibleusers/";
    public static Integer usersUpdateInterval = 2000; //2 seconds
}
