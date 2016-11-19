package com.example.jacekpodwysocki.soundie;

/**
 * Created by jacekpodwysocki on 11/11/2016.
 */

public class RowItemOptions {
    private String songTitle;
    private String songArtist;

    public RowItemOptions(String songTitle, String songArtist){
        this.songTitle = songTitle;
        this.songArtist = songArtist;
    }

    public String getSongTitle(){
        return songTitle;
    }
    public String getSongArtist(){
        return songArtist;
    }

    public void setSongTitle(String songTitle){
        this.songTitle = songTitle;
    }
    public void setSongArtist(String songArtist){
        this.songArtist = songArtist;
    }

}
