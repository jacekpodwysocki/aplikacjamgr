package com.example.jacekpodwysocki.soundie;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;

import static android.R.attr.targetActivity;
import static android.R.attr.type;
import static android.R.id.input;
import static android.os.Environment.getExternalStorageDirectory;
import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;
import static com.example.jacekpodwysocki.soundie.MenuActivity.file;
import static com.example.jacekpodwysocki.soundie.R.id.btnSendFile;

/**
 * Created by jacekpodwysocki on 21/11/2016.
 */

public class BluetoothConnectedThread extends Thread {
    private Context context;
    private Handler btHandler;
    private General general;
    private final BluetoothSocket mmSocket;
    private final InputStream inStream;
    private final OutputStream outStream;
    private FileOutputStream fos;
    private Integer current;
    private Integer bytesRead;
    private String transferStatus;

    public BluetoothConnectedThread(Context context,BluetoothSocket socket) {
        this.context=context;
        mmSocket = socket;
        general = new General(context);

        InputStream tempIn = null;
        OutputStream tempOut = null;

        try{
            tempIn = socket.getInputStream();
            tempOut = socket.getOutputStream();
        } catch (IOException e){
            general.log("BT Connected","Problem getting Input and Output streams");
        }
        inStream = tempIn;
        outStream = tempOut;
    }



    public void run() {

            general.log("BT Connected", "Listening for incoming files =========================================================================");
            // RECEIVING
            try {
                java.io.File fileOutputstream = new java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "/test3.mp3");
                fos = new FileOutputStream(fileOutputstream);
            } catch (IOException e) {
                general.log("BT Connected", "error getting destination directory");
            }

            this.sendFile();


            int bufferSize = 8 * 1024;
            byte[] buffer = new byte[bufferSize];
            bytesRead = 0;
            transferStatus = "idle";

            // Keep listening to the InputStream while connected
            while (true) {
                if(transferStatus.equals("sent")){
//                    General.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            general.log("Bluetooth Connected", "Plik wysłany!!!!");
////                            Toast.makeText(context, "Plik wysłany", Toast.LENGTH_LONG).show();
//                        }
//                    });
                    general.log("Bluetooth Connected", "Plik wysłany!!!!");
                    transferStatus ="idle";
                }

                try {
                    try {
                        int read;

                        while ((read = inStream.read(buffer)) != -1) {
                            transferStatus = "sending";
                            bytesRead += read;
//
                            general.log("Bluetooth Connected", "odebrano: " + bytesRead);
                            fos.write(buffer, 0, read);
                            transferStatus = "sent";
                        }

                    } finally {
                        outStream.flush();
                        inStream.close();
                    }
                }catch (Exception e) {
                    e.printStackTrace(); // handle exception, define IOException and others
                }
            }
        }
//
//    mmSocket.close();



    public void sendFile(){
        general.log("BT Connected", "TRANSFER TYPE SENDING =========================================================================");

        String fileUri = "/system/media/Pre-loaded/Music/Bach_Suite.mp3";
        java.io.File myFile = new java.io.File(fileUri);

        if (myFile.exists()) {
            general.log("BT Connected", "file /Bach_Suite.mp3 ISTNIEJE");
        } else {
            general.log("BT Connected", "file /Bach_Suite.mp3 NIE ISTNIEJE");
        }

        general.log("BT Connected", "file /Bach_Suite.mp3 created success!");

        byte[] mybytearray = new byte[(int) myFile.length()];


        try {
            FileInputStream fis = new FileInputStream(myFile);

            BufferedInputStream bis = new BufferedInputStream(fis, 8 * 1024);

            bis.read(mybytearray, 0, mybytearray.length);

            outStream.write(mybytearray, 0, mybytearray.length);

            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            Log.e("BT Connected", "disconnected", e);
        }
    }

    public void write(byte[] bytes) {
        try {
            outStream.write(bytes);
        } catch
                (IOException e) { }
    }
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
