package com.example.jacekpodwysocki.soundie;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.example.jacekpodwysocki.soundie.R.id.songArtist;

/**
 * Created by jacekpodwysocki on 22/10/2016.
 */

public class SongAdapter extends BaseExpandableListAdapter {
    // passing current context to adapter and store as a field
    private Context context;

    private ArrayList<Song> songs;
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private LayoutInflater songInf;

    public SongAdapter(Context context, ArrayList<Song> theSongs,List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        songs=theSongs;
        songInf=LayoutInflater.from(context);
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.context = context;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //map to song layout
        RelativeLayout songLay = (RelativeLayout)songInf.inflate
                (R.layout.list_group, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.trackTitleText);
        TextView artistView = (TextView)songLay.findViewById(R.id.trackArtist);

        //get song using position
        Song currSong = songs.get(groupPosition);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());

        //set position as tag
        songLay.setTag(groupPosition);

        if (isExpanded == true) {
            ImageView iconToggle = (ImageView) songLay.findViewById(R.id.iconToggle);

            iconToggle.setImageResource(R.drawable.arror_up);
        }else{
            ImageView iconToggle = (ImageView) songLay.findViewById(R.id.iconToggle);
            iconToggle.setImageResource(R.drawable.arrow_down);
        }

        return songLay;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        RelativeLayout songLay = (RelativeLayout)songInf.inflate
                (R.layout.list_item, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.songTitle);
        TextView artistView = (TextView)songLay.findViewById(songArtist);
        TextView albumView = (TextView)songLay.findViewById(R.id.songAlbum);
        ImageButton trackButtonPlay = (ImageButton)songLay.findViewById(R.id.trackButtonPlay);
        //get song using position
        Song currSong = songs.get(groupPosition);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        albumView.setText(currSong.getAlbum());

        final String songTitle = currSong.getTitle();
        final String songArtist = currSong.getArtist();
        final String songPath = currSong.getPath();
        //set position as tag
        songLay.setTag(groupPosition);

        trackButtonPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToPlayer(songTitle, songArtist, songPath);
            }
        });

        return songLay;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void goToPlayer(String songTitle, String songArtist, String songPath){
        // pass parameter
        Intent startIntent = new Intent(context, MenuActivity.class);
        startIntent.putExtra("fragmentParam", "player");
        startIntent.putExtra("songTitle", songTitle);
        startIntent.putExtra("songArtist", songArtist);
        startIntent.putExtra("songPath", songPath);
        context.startActivity(startIntent);

    }

}
