package com.example.jacekpodwysocki.soundie;

import android.graphics.Bitmap;

import static android.R.attr.id;
import static android.R.attr.path;

/**
 * Created by jacekpodwysocki on 16/10/2016.
 */

public class File {
    private Integer fileid;
    private Integer filetitleid;
    private Integer fileartistid;
    private Integer filealbumid;
    private String filechecksum;

    public File(Integer fileId, Integer fileTitleId,Integer fileArtistId, Integer fileAlbumId, String fileChecksum) {
        fileid=fileId;
        filetitleid=fileTitleId;
        fileartistid=fileArtistId;
        filealbumid=fileAlbumId;
        filechecksum=fileChecksum;
    }

    public Integer getFileId(){return fileid;}
    public Integer getFileTitleId(){return filetitleid;}
    public Integer getFileArtistId(){return fileartistid;}
    public Integer getFileAlbumId(){return filealbumid;}
    public String getFileChecksum(){return filechecksum;}
}
