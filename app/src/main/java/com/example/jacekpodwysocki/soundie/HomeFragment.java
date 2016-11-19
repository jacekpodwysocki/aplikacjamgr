package com.example.jacekpodwysocki.soundie;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.jacekpodwysocki.soundie.R.array.array1;
import static com.example.jacekpodwysocki.soundie.R.array.array2;
import static com.example.jacekpodwysocki.soundie.R.array.array3;

public class HomeFragment extends Fragment {

    String[] listItems;
    String[] ListItemsValues;
    private ListView generalStats;
    private List<ListViewCounterRowItem> rowItems;
    private ListViewElementCounterAdapter adapter;
    private ListViewCounterRowItem items;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context context = inflater.getContext();

        View rootView = inflater
                .inflate(R.layout.fragment_home, container, false);



        listItems = getResources().getStringArray(array2);

        ListItemsValues = getResources().getStringArray(array3);

        generalStats = (ListView) rootView.findViewById(R.id.generalStats);
        rowItems = new ArrayList<ListViewCounterRowItem>();


        for (int i = 0; i < listItems.length; i++) {

            if(i == 0) {
                items = new ListViewCounterRowItem(listItems[i], String.valueOf(getSongsCount()));
            }else if(i == 1){
                items = new ListViewCounterRowItem(listItems[i], String.valueOf(getAlbumsCount()));
            }else if(i == 2){
                items = new ListViewCounterRowItem(listItems[i], String.valueOf(getArtistsCount()));
            }
            rowItems.add(items);
        }

        adapter = new ListViewElementCounterAdapter(context, rowItems);
        generalStats.setAdapter(adapter);

        generalStats.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent startIntent = new Intent(getActivity(), MenuActivity.class);
                startIntent.putExtra("fragmentParam", "mymusic");
                startActivity(startIntent);
            }

        });

        return rootView;

    }

    public Integer getSongsCount(){
        Integer audioCounter = 0;

        ContentResolver musicResolver = getActivity().getContentResolver();
        String fileExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String sel = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selExtARGS = new String[]{fileExtension};
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = musicResolver.query(musicUri, null, sel, selExtARGS, null);


        if(songCursor!=null && songCursor.moveToFirst()){
            do {
                audioCounter++;
            }
            while (songCursor.moveToNext());
        }

        return audioCounter;
    }

    public Integer getAlbumsCount(){
        Integer albumCounter = 0;

        ContentResolver musicResolver = getActivity().getContentResolver();
        String fileExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String sel = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selExtARGS = new String[]{fileExtension};
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = musicResolver.query(musicUri, null, sel, selExtARGS, null);


        if(songCursor!=null && songCursor.moveToFirst()){
            List<String> albumsList = new ArrayList<String>();
            int albumColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM);

            do {
                String thisAlbum = songCursor.getString(albumColumn);
                if (!albumsList.contains(thisAlbum)) {
                    albumsList.add(thisAlbum);
                }
            }
            while (songCursor.moveToNext());
            albumCounter = albumsList.size();
        }

        return albumCounter;
    }

    public Integer getArtistsCount(){
        Integer artistsCounter = 0;

        ContentResolver musicResolver = getActivity().getContentResolver();
        String fileExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String sel = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selExtARGS = new String[]{fileExtension};
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = musicResolver.query(musicUri, null, sel, selExtARGS, null);


        if(songCursor!=null && songCursor.moveToFirst()){
            List<String> artistsList = new ArrayList<String>();
            int artistColumn = songCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);

            do {
                String thisAlbum = songCursor.getString(artistColumn);
                if (!artistsList.contains(thisAlbum)) {
                    artistsList.add(thisAlbum);
                }
            }
            while (songCursor.moveToNext());
            artistsCounter = artistsList.size();
        }

        return artistsCounter;
    }
}
