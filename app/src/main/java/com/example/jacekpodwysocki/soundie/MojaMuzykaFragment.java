package com.example.jacekpodwysocki.soundie;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import static android.R.attr.data;
import static android.R.id.list;
import static android.content.ContentValues.TAG;
import static com.example.jacekpodwysocki.soundie.R.id.iconToggle;
import static com.example.jacekpodwysocki.soundie.R.id.loginBtn;


public class MojaMuzykaFragment extends Fragment {
    private int lastExpandedPosition = -1;
    private ArrayList<Song> songList;
    private ExpandableListView songView;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_moja_muzyka, container, false);

        // get the listview
        expListView = (ExpandableListView) rootView.findViewById(R.id.tracks);

        // query songs
        songView = (ExpandableListView)rootView.findViewById(R.id.tracks);
        songList = new ArrayList<Song>();
        getSongList();

        // sort songs
        // poprawic: sortowanie rozwala click listener (listener się nie sortuje)
//        Collections.sort(songList, new Comparator<Song>(){
//            public int compare(Song b, Song a){
//                return b.getTitle().compareTo(a.getTitle());
//            }
//        });

        SongAdapter songAdt = new SongAdapter(rootView.getContext(), songList,listDataHeader,listDataChild);
        songView.setAdapter(songAdt);


        // Listview Group click listener
        songView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                 Toast.makeText(getActivity().getApplicationContext(), // getApplicationContext - cała apka, getActivity - tylko obecna aktywność
                 "Group Clicked " + listDataHeader.get(groupPosition),
                 Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        // Listview Group expanded listener
        songView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getActivity().getApplicationContext(),
//                        listDataHeader.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();

                // hide all groups but clicked
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;



            }
        });

        // Listview Group collasped listener
        songView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getActivity().getApplicationContext(),
//                        listDataHeader.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();


            }
        });

        // Listview on child click listener
        songView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });



        return rootView;

    }

    public void getSongList() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        Integer audioCounter = -1;

        ContentResolver musicResolver = getActivity().getContentResolver();
        String fileExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String sel = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selExtARGS = new String[]{fileExtension};
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = musicResolver.query(musicUri, null, sel, selExtARGS, null);


        if(songCursor!=null && songCursor.moveToFirst()){
            //get columns

            int titleColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM);
            int mimeColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.MIME_TYPE);
            int fullpathColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.DATA);

            //add songs to list

            do {
                audioCounter++;
                long thisId = songCursor.getLong(idColumn);
                String thisTitle = songCursor.getString(titleColumn);
                String thisArtist = songCursor.getString(artistColumn) ;
                String thisAlbum = songCursor.getString(albumColumn);
                String thisPath = songCursor.getString(fullpathColumn);
                if(thisArtist.toLowerCase().contains("unknown")){
                    thisArtist = getResources().getString(R.string.textSongNoArtistAvailable);
                }
                if(thisAlbum.toLowerCase().contains("music")){
                    thisAlbum = getResources().getString(R.string.textSongNoAlbumAvailable);
                }
                String thisMime = songCursor.getString(mimeColumn);

                listDataHeader.add(thisTitle);
                List<String> songDetails = new ArrayList<String>();
                songDetails.add(thisTitle);
                listDataChild.put(listDataHeader.get(audioCounter), songDetails);

                songList.add(new Song(thisPath,thisId, thisTitle, thisArtist, thisAlbum));
            }
            while (songCursor.moveToNext());
        }
    }


}
