package com.example.jacekpodwysocki.aplikacjamgr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by jacekpodwysocki on 06/08/2016.
 */

public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000); // 3 seconds
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // prevent splash screen from showing when "back" button is pressed
        super.onPause();
        finish();
    }
}

