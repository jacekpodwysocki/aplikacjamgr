package com.example.jacekpodwysocki.soundie;


/**
 * Created by jacekpodwysocki on 04/11/2016.
 */

public class MarkerTag {
    private Integer userid;
    private Boolean activearker;


    public MarkerTag(Integer userId,Boolean activeMarker) {
        userid=userId;
        activearker=activeMarker;
    }

    public Integer getUserId(){return userid;}
    public Boolean getIsActive(){return activearker;}


}
