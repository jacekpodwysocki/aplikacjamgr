package com.example.jacekpodwysocki.soundie;

import android.graphics.Bitmap;

import static android.R.attr.path;

/**
 * Created by jacekpodwysocki on 16/10/2016.
 */

public class Song {
    private String path;
    private long id;
    private String title;
    private String artist;
    private String album;
    private Bitmap cover;

    public Song(String songPath, long songID, String songTitle, String songArtist, String songAlbum,Bitmap albumCover) {
        path=songPath;
        id=songID;
        title=songTitle;
        artist=songArtist;
        album=songAlbum;
        cover=albumCover;
    }

    public String getPath(){return path;}
    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getAlbum(){return album;}
    public Bitmap getAlbumCover(){return cover;}
}
