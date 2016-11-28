package com.example.jacekpodwysocki.soundie;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import java.io.File;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileDescriptor;
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
import java.util.Map;

import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.data;
import static android.R.attr.path;
import static android.R.id.list;
import static android.content.ContentValues.TAG;
import static com.example.jacekpodwysocki.soundie.MenuActivity.general;
import static com.example.jacekpodwysocki.soundie.R.id.iconToggle;
import static com.example.jacekpodwysocki.soundie.R.id.loginBtn;
import static com.example.jacekpodwysocki.soundie.R.id.map;


public class MojaMuzykaFragment extends Fragment {
    private int lastExpandedPosition = -1;
    private ArrayList<Song> songList;
    private ExpandableListView songView;
    final public static Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");

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


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
//                for(int i = 0;i< songList.size();i++){
//                    // add songs to database
//                    //syncSongsWithServer(songList.get(i).getTitle(),songList.get(i).getArtist(),songList.get(i).getAlbum(),general.getMD5EncryptedString(songList.get(i).getPath()));
//                }
                try{
                    syncSongsWithServer(songList);
                }
                catch (JSONException e) {
                    general.log("MOJA MUZYKA", "error w funkcji");
                }
            }
        });

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
//                 Toast.makeText(getActivity().getApplicationContext(), // getApplicationContext - cała apka, getActivity - tylko obecna aktywność
//                 "Group Clicked " + listDataHeader.get(groupPosition),
//                 Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(
//                        getActivity().getApplicationContext(),
//                        listDataHeader.get(groupPosition)
//                                + " : "
//                                + listDataChild.get(
//                                listDataHeader.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT)
//                        .show();
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
            long albumIdColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM_ID);
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
                Bitmap coverBm = this.getAlbumart(albumIdColumn);

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

                songList.add(new Song(thisPath,thisId, thisTitle, thisArtist, thisAlbum,coverBm));
            }
            while (songCursor.moveToNext());
        }
    }

    public Integer getSongsCount(){
        return songList.size();
    }

    // C Nepster, http://stackoverflow.com/questions/1954434/cover-art-on-android
    public Bitmap getAlbumart(Long album_id)
    {
        Bitmap bm = null;
        try
        {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = getActivity().getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null)
            {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }else{
                Log.i("player","pdf = null");
            }
        } catch (Exception e) {
        }
        return bm;


    }

    private void syncSongsWithServer(final ArrayList<Song> songListArray) throws JSONException {
        /**
         * Function to sync device music information to server
         * */

        // Tag used to cancel the request
        String tag_string_req = "req_syncmusic";

        Map<String, JSONObject> postParam= new HashMap<String, JSONObject>();
        for (int i = 0; i < songListArray.size(); i++) {
            JSONObject innerObject = new JSONObject();
            innerObject.put("fileTitle", songListArray.get(i).getTitle());
            innerObject.put("fileArtist", songListArray.get(i).getArtist());
            innerObject.put("fileAlbum", songListArray.get(i).getAlbum());
            innerObject.put("fileChecksum", songListArray.get(i).getChecksum());
            postParam.put(String.valueOf(i), innerObject);
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,AppConfig.URL_SYNCMUSIC, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        general.log("MOJA MUZYKA", "Sync to database OK: "+response.toString());
                        try {
                            boolean error = response.getBoolean("error");

                            if (!error) {
                                String reponseMessage = response.getString("message");
                                general.log("PLAYER","Sync to database OK: "+reponseMessage);

                            } else {
                                // Error occurred, get error message
                                // message
                                String errorMsg = response.getString("message");
                                general.log("PLAYER","Sync to database ERROR: "+errorMsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                general.log("MOJA MUZYKA","Sync to database ERROR: "+error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("AUTHORIZATION",getResources().getString(R.string.soundieApiKey));
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }



        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,tag_string_req);
    }



}
