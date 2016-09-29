package com.example.jacekpodwysocki.soundie;

/**
 * Created by jacekpodwysocki on 17/08/2016.
 */

public class ListViewCounterRowItem {
    private String text;
    private String value;

    public ListViewCounterRowItem(String text, String value){
        this.text = text;
        this.value = value;
    }

    public String getText(){
        return text;
    }
    public void setText(String title){
        this.text = text;
    }
    public String getValue(){
        return value;
    }
    public void setValue(String value){
        this.value = value;
    }
}
