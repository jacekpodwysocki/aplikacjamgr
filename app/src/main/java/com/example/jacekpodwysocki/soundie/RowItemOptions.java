package com.example.jacekpodwysocki.soundie;

/**
 * Created by jacekpodwysocki on 11/11/2016.
 */

public class RowItemOptions {
    private String songTitle;
    private String songArtist;
    private Integer songId;

    public RowItemOptions(String songTitle, String songArtist, Integer songId){
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songId = songId;
    }

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
    public Integer getSongId(){
        return songId;
    }

    public void setSongTitle(String songTitle){
        this.songTitle = songTitle;
    }
    public void setSongArtist(String songArtist){
        this.songArtist = songArtist;
    }

}
