package com.example.jacekpodwysocki.soundie;

import android.graphics.Bitmap;

import java.util.zip.Checksum;

import static android.R.attr.path;
import static com.example.jacekpodwysocki.soundie.MenuActivity.general;

/**
 * Created by jacekpodwysocki on 16/10/2016.
 */

public class Song {
    private String path;
    private long id;
    private Integer dbId;
    private String title;
    private String artist;
    private String album;
    private Bitmap cover;
    private String checksum;
    private String dbChecksum;

    public Song(String songPath, long songID, String songTitle, String songArtist, String songAlbum,Bitmap albumCover) {
        this.path=songPath;
        this.id=songID;
        this.title=songTitle;
        this.artist=songArtist;
        this.album=songAlbum;
        this.cover=albumCover;
    }

    public Song(String songTitle, String songArtist, Integer songDbId, String dbChecksum) {
        this.title=songTitle;
        this.artist=songArtist;
        this.dbId=songDbId;
        this.dbChecksum=dbChecksum;
    }

    public Song(String songTitle, String songArtist) {
        this.title=songTitle;
        this.artist=songArtist;
    }

    public String getPath(){return path;}
    public long getID(){return id;}
    public Integer getDbId(){return dbId;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getAlbum(){return album;}
    public Bitmap getAlbumCover(){return cover;}
    public String getDbChecksum(){return dbChecksum;}
    public String getChecksum(){
        checksum = general.getMD5EncryptedString(getPath());
        return checksum;
    }
}
