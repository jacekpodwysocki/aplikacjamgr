package com.example.jacekpodwysocki.soundie;

/**
 * Created by jacekpodwysocki on 25/10/2016.
 */

public class MediaPlayer {

        private MediaPlayer mp;

        private MediaPlayer () {
            mp = new MediaPlayer();
        }

        public MediaPlayer getMediaPlayer () {
            if (mp == null) {
                new MediaPlayer();
            }
            return mp;
        }

}
