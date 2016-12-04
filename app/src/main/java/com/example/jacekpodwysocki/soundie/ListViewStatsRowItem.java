package com.example.jacekpodwysocki.soundie;

import android.content.res.Resources;

/**
 * Created by jacekpodwysocki on 17/08/2016.
 */

public class ListViewStatsRowItem {
    private String position;
    private String name;
    private  String level;
    private String stats;
    private int badge;

    public ListViewStatsRowItem(String position, String name, String level, String stats, int badge){
        this.position = position;
        this.name = name;
        this.level = level;
        this.stats = stats;
        this.badge = badge;
    }

    public String getPosition(){
        return position;
    }
    public String getName(){
        return name;
    }
    public String getLevel(){
        return level;
    }
    public String getStats(){
        return stats;
    }
    public int getBadge(){
        return badge;
    }
}
