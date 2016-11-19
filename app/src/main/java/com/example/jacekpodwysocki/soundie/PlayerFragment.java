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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;
import static com.example.jacekpodwysocki.soundie.MenuActivity.file;
import static com.example.jacekpodwysocki.soundie.MenuActivity.general;
import static com.example.jacekpodwysocki.soundie.R.id.coverImage;
import static com.example.jacekpodwysocki.soundie.R.id.map;
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
import static java.lang.Integer.parseInt;


public class PlayerFragment extends Fragment {

    private SQLiteHandler db;
    public String currentTrack;
    private TextView currentTrackText;
    private ImageView trackAlbumCover;
    private MediaPlayer mediaPlayer;
    private General general;
//    private File file;
    private ImageButton trackButtonPlayPause;
    private ImageButton seekBack;
    private ImageButton seekForward;
    private ImageView backButton;
    private SeekBar trackProgressBar;
    private TextView tCurrent;
    private TextView tLeft;
    private Integer length;
    private File fileInfo;
    private String globalPlaybackType;
    private String currentPath;
    private int trackSeekFwTime = 5000; // 5000 milliseconds
    private int trackSeekBwTime = 5000; // 5000 milliseconds
    // Handler to update UI timer, progress bar
    private Handler mHandler = new Handler();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_player, container, false);

        // SqLite database handler
        db = new SQLiteHandler(getActivity());

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
        currentPath = activity.getTrackPathToBePlayed();

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
        reportCurrentSong("start");
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
        // stop track timer handler
        mHandler.removeCallbacks(mUpdateTimeTask);

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;

        reportCurrentSong("stop");
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

    public void saveCurrentSongToServer(final Integer userId, final Integer fileId, final String playbackType){
        /**
         * Function to sync device music information to server
         * */

        // Tag used to cancel the request
        String tag_string_req = "req_savecurrentplayback";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SAVECURRENTPLAYBACK, new Response.Listener<String>() {



            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String reponseMessage = jObj.getString("message");
                        general.log("PLAYER","Playback data saved: "+reponseMessage);

                    } else {
                        // Error occurred, get error message
                        // message
                        String errorMsg = jObj.getString("message");
                        general.log("PLAYER","ERROR saving playback data: "+errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().toLowerCase().contains("network is unreachable")) {
                    general.showToast("Brak połączenia z internetem!\nWłącz sieć, aby móc korzystać z aplikacji", getActivity());
                }else{
                    general.showToast(error.getMessage(), getActivity());
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //return super.getHeaders();
                Map<String, String> params = new HashMap<>();
                params.put("AUTHORIZATION",getResources().getString(R.string.soundieApiKey));
                return params;
            }
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", String.valueOf(userId));
                params.put("fileId", String.valueOf(fileId));
                params.put("playbackType", playbackType);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public File getFileByChecksumReponse(File responseFile){
        general.log("PLAYER","repsponse ---> ");

        if(globalPlaybackType == "start") {
            saveCurrentSongToServer(parseInt(db.getUserId()), responseFile.getFileId(), globalPlaybackType);
        }else if(globalPlaybackType == "stop"){
            saveCurrentSongToServer(parseInt(db.getUserId()), responseFile.getFileId(), globalPlaybackType);
        }
        return responseFile;
    }

    // pobierz id pliku na podstawie jego hash'a
    public void getFileInfoByChecksum(String checksum){
        Integer fileId = -1;
        // Tag used to cancel the request
        String tag_string_req = "req_getfileinfobychecksum";


        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETFILEINFOBYCHECKSUM+checksum, new Response.Listener<String>() {


            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");


                    if (!error) {

                        String fileMsg = jObj.getString("fileInfo");
                        general.log("PLAYER","MSG: " + fileMsg);

                        JSONArray fileArray = new JSONArray(fileMsg);

                        general.log("PLAYER","Fetching file info OK: " + fileArray.length());

                        for(int i=0; i<fileArray.length(); i++){

                            JSONObject json_data = fileArray.getJSONObject(i);
                            file = new File(Integer.valueOf(json_data.getString("FileId")),Integer.valueOf(json_data.getString("FileTitleId")),Integer.valueOf(json_data.getString("FileArtistId")),Integer.valueOf(json_data.getString("FileAlbumId")),json_data.getString("FileChecksum"));

                            getFileByChecksumReponse(file);

                        }


                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        general.log("PLAYER","ERROR getting file info: "+errorMsg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {


            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().toLowerCase().contains("network is unreachable")) {
                    general.showToast("Brak połączenia z internetem!\nWłącz sieć, aby móc korzystać z aplikacji", getActivity());
                }else{
                    general.showToast(error.getMessage(), getActivity());
                }
            }
        }) {

            public Map<String, String> getHeaders() throws AuthFailureError {
                //return super.getHeaders();
                Map<String, String> params = new HashMap<>();
                params.put("AUTHORIZATION",getResources().getString(R.string.soundieApiKey));
                return params;
            }


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void reportCurrentSong(String playbackType){
        globalPlaybackType = playbackType;
        getFileInfoByChecksum(general.getMD5EncryptedString(currentPath));

    }


}
