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
    public static String URL_SAVETRANSFERREQUEST = APIURL+"SoundieApi/v1/savetransferrequest";
    public static String URL_SAVEDEVICEDETAILS = APIURL+"SoundieApi/v1/savedevicedetails";
    public static String URL_GETDEVICEDETAILS = APIURL+"SoundieApi/v1/getdevicedetails/";
    public static String URL_GETFILEINFOBYCHECKSUM = APIURL+"SoundieApi/v1/getfileinfobychecksum/";
    public static String URL_GETCURRENTFILE = APIURL+"SoundieApi/v1/getcurrentfile/";
    public static String URL_GETSONGSHISTORY = APIURL+"SoundieApi/v1/getsongshistory/";
    public static String URL_GETFILEREQUESTDETAILS = APIURL+"SoundieApi/v1/getfilerequestdetails/";
    public static String URL_GETFILEDETAILS = APIURL+"SoundieApi/v1/getfiledetails/";
    public static String URL_GETFILEREQUESTRESPONSE = APIURL+"SoundieApi/v1/getfilerequestresponse/";
    public static String URL_GETFILEREQUESTSCOUNT = APIURL+"SoundieApi/v1/getfilerequestscount/";
    public static String URL_UPDATEREQUESTSTATUS = APIURL+"SoundieApi/v1/updaterequeststatus";

//    public static String SoundieUUID = "79bf651e-18f7-4ddc-a293-bdd6b3392558";

    public static String SoundieUUID = "fc6925f2-b8c7-11e6-80f5-76304dec7eb7";
    public static Integer usersUpdateInterval = 3000; // 3 seconds
    public static Integer gpsCheckInterval = 3000; // 3 seconds
    public static Integer fileTransferCheckInterval = 3000; // 3 sec
}
