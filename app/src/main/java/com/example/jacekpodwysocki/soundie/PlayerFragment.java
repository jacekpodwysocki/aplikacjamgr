package com.example.jacekpodwysocki.soundie;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;
import static com.example.jacekpodwysocki.soundie.R.id.profileHeadline;
import static com.example.jacekpodwysocki.soundie.R.id.registerBtn;
import static com.example.jacekpodwysocki.soundie.R.id.registrationEmail;
import static com.example.jacekpodwysocki.soundie.R.id.registrationFirstName;
import static com.example.jacekpodwysocki.soundie.R.id.registrationLastName;
import static com.example.jacekpodwysocki.soundie.R.id.registrationPassword;
import static com.example.jacekpodwysocki.soundie.R.id.trackButtonPlay;
import static com.example.jacekpodwysocki.soundie.R.id.trackTitleText;


public class PlayerFragment extends Fragment {

    public String currentTrack;
    private TextView currentTrackText;
    private MediaPlayer mediaPlayer;
    private ImageButton trackButtonPlayPause;
    private Integer length;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_player, container, false);

        currentTrackText = (TextView) rootView.findViewById(trackTitleText);
        trackButtonPlayPause = (ImageButton) rootView.findViewById(R.id.trackButtonPlayPause);

        MenuActivity activity = (MenuActivity) getActivity();
        String currentTrack = activity.getTrackInfoToBePlayed();
        String currentPath = activity.getTrackPathToBePlayed();

        currentTrackText.setText(currentTrack);

        playSong(currentPath);


        // play pause Button Click event
        trackButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if(isPlaybackOn() == true){
                    Log.e("PLAYER ---> ", "Song playing->pause");

                    mediaPlayer.pause();
                    trackButtonPlayPause.setImageResource(R.drawable.icon_track_play);
                    length=mediaPlayer.getCurrentPosition();
                }else{
                    Log.e("PLAYER ---> ", "Song not playing->resume");
                    mediaPlayer.seekTo(length);
                    trackButtonPlayPause.setImageResource(R.drawable.icon_pause);
                    mediaPlayer.start();
                }
            }
        });

        return rootView;
    }

    public void  playSong(String songPath){
        try {
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                } catch (Exception e) {

                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(songPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }else{
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(songPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaybackOn(){
        AudioManager manager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        if(manager.isMusicActive())
        {
            return true;
        }else{
            return false;
        }
    }

    public void stopSong(){
        // to be implemented
    }

}
