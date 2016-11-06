package com.example.jacekpodwysocki.soundie;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;
import static com.example.jacekpodwysocki.soundie.R.id.coverImage;
import static com.example.jacekpodwysocki.soundie.R.id.playerButtonBack;
import static com.example.jacekpodwysocki.soundie.R.id.profileHeadline;
import static com.example.jacekpodwysocki.soundie.R.id.registerBtn;
import static com.example.jacekpodwysocki.soundie.R.id.registrationEmail;
import static com.example.jacekpodwysocki.soundie.R.id.registrationFirstName;
import static com.example.jacekpodwysocki.soundie.R.id.registrationLastName;
import static com.example.jacekpodwysocki.soundie.R.id.registrationPassword;
import static com.example.jacekpodwysocki.soundie.R.id.trackButtonBack;
import static com.example.jacekpodwysocki.soundie.R.id.trackButtonForward;
import static com.example.jacekpodwysocki.soundie.R.id.trackButtonPlay;
import static com.example.jacekpodwysocki.soundie.R.id.trackProgress;
import static com.example.jacekpodwysocki.soundie.R.id.trackTimeCurrent;
import static com.example.jacekpodwysocki.soundie.R.id.trackTimeLeft;
import static com.example.jacekpodwysocki.soundie.R.id.trackTitleText;


public class PlayerFragment extends Fragment {

    public String currentTrack;
    private TextView currentTrackText;
    private ImageView trackAlbumCover;
    private MediaPlayer mediaPlayer;
    private General general;
    private ImageButton trackButtonPlayPause;
    private ImageButton seekBack;
    private ImageButton seekForward;
    private ImageView backButton;
    private SeekBar trackProgressBar;
    private TextView tCurrent;
    private TextView tLeft;
    private Integer length;
    private int trackSeekFwTime = 5000; // 5000 milliseconds
    private int trackSeekBwTime = 5000; // 5000 milliseconds
    // Handler to update UI timer, progress bar
    private Handler mHandler = new Handler();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_player, container, false);

        mediaPlayer = MenuActivity.mediaPlayer;
        trackAlbumCover = (ImageView) rootView.findViewById(coverImage);
        currentTrackText = (TextView) rootView.findViewById(trackTitleText);
        backButton = (ImageView) rootView.findViewById(playerButtonBack);
        seekBack = (ImageButton) rootView.findViewById(trackButtonBack);
        seekForward = (ImageButton) rootView.findViewById(trackButtonForward);
        trackProgressBar = (SeekBar) rootView.findViewById(trackProgress);
        tCurrent = (TextView) rootView.findViewById(trackTimeCurrent);
        tLeft = (TextView) rootView.findViewById(trackTimeLeft);

        trackButtonPlayPause = (ImageButton) rootView.findViewById(R.id.trackButtonPlayPause);

        MenuActivity activity = (MenuActivity) getActivity();
        String currentTrack = activity.getTrackInfoToBePlayed();
        String currentPath = activity.getTrackPathToBePlayed();

        currentTrackText.setText(currentTrack);
        trackButtonPlayPause.setImageResource(R.drawable.icon_pause);

        trackAlbumCover.setImageResource(R.drawable.album_cover_placeholder);

        general = MenuActivity.general;

        if (mediaPlayer != null) {
        Log.i("PLAYER","player not null na poczatku");
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
        String strDate = sdf.format(c.getTime());

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

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.i("player","back button clicked");
                goToMyMusic(view);
            }
        });

        seekForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                if(currentPosition + trackSeekFwTime <= mediaPlayer.getDuration()){
                    // forward song
                    mediaPlayer.seekTo(currentPosition + trackSeekFwTime);
                }else{
                    // go to end
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                }
            }
        });

        seekBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                if(currentPosition - trackSeekBwTime >= 0){
                    // forward song
                    mediaPlayer.seekTo(currentPosition - trackSeekBwTime);
                }else{
                    // go to end
                    mediaPlayer.seekTo(0);
                }

            }
        });

        trackProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
            *
            * */
            @Override
            public void onProgressChanged (SeekBar seekBar,int progress, boolean fromTouch){
            }


            /**
            * When user starts moving the progress handler
            */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            // remove message Handler from updating progress bar
            mHandler.removeCallbacks(mUpdateTimeTask);
            }

            /**
            * When user stops moving the progress hanlder
            */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            int totalDuration = mediaPlayer.getDuration();
            int currentPosition = general.progressToTimer(seekBar.getProgress(), totalDuration);

            // forward or backward to certain seconds
            mediaPlayer.seekTo(currentPosition);

            // update timer progress again
            updateProgressBar();
            }
        });


        playSong(currentPath);
        return rootView;
    }

    public void goToMyMusic(View v) {
        // pass parameter
        Intent startIntent = new Intent(getActivity(), MenuActivity.class);
        startIntent.putExtra("fragmentParam", "mymusic");
        startActivity(startIntent);

//        mediaPlayer.stop();
//        mediaPlayer.release();


    }

    public void  playSong(String songPath){
//        try{
//            Thread.sleep(2000);
//        }
//        catch(InterruptedException e){
//        }

        if(isPlaybackOn() == true){
            Log.i("PLAYER","playback ON");
        }else{
            Log.i("PLAYER","playback OFF");
        }
        try {
            if (mediaPlayer != null) {
                Log.i("player","player not null, destroy and create new one");
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

                resetSeekBar();
                // update seek bar
                updateProgressBar();
            }else{
                Log.i("player","player null, create new one");
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(songPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                resetSeekBar();
                // update seek bar
                updateProgressBar();
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
        return manager.isMusicActive();
    }

    public void stopSong(){
        // to be implemented
    }



    @Override
    public void onPause() {
        super.onPause();
        // stopb track timer handler
        mHandler.removeCallbacks(mUpdateTimeTask);

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            //Log.i("test",general.milliSecondsToTimer(totalDuration));

            tLeft.setText(""+general.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            tCurrent.setText(""+general.milliSecondsToTimer(currentDuration));


            // Updating progress bar
            int progress = general.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            trackProgressBar.setProgress(progress);

            // run thread after 100 ms
            mHandler.postDelayed(this, 100);
        }
    };


    public void resetSeekBar() {
        trackProgressBar.setProgress(0);
        trackProgressBar.setMax(100);
    }





}
