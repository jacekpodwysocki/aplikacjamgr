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
    public static String URL_SYNCMUSIC = APIURL+"SoundieApi/v1/syncmusic";
    public static String URL_GETACTIVEUSERS = APIURL+"SoundieApi/v1/visibleusers/";
    public static String URL_GETUSERDETAILS = APIURL+"SoundieApi/v1/userdetails/";
    public static String URL_SAVECURRENTPLAYBACK = APIURL+"SoundieApi/v1/savecurrentplayback";
    public static String URL_GETFILEINFOBYCHECKSUM = APIURL+"SoundieApi/v1/getfileinfobychecksum/";
    public static String URL_GETCURRENTFILE = APIURL+"SoundieApi/v1/getcurrentfile/";
    public static String URL_GETSONGSHISTORY = APIURL+"SoundieApi/v1/getsongshistory/";

    public static Integer usersUpdateInterval = 3000; //3 seconds
}
