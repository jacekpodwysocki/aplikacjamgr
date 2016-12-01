package com.example.jacekpodwysocki.soundie;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import static com.example.jacekpodwysocki.soundie.AppConfig.SoundieUUID;
import static com.example.jacekpodwysocki.soundie.MenuActivity.general;

/**
 * Created by jacekpodwysocki on 20/11/2016.
 */

public class BluetoothListeningThread extends Thread{
    private Context context;
    private Activity activity;
    private General general;
    private String transferFilePath;
    private String transferType;
    private String transferFileName;
    private BluetoothServerSocket bluetoothServerSocket;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



    public BluetoothListeningThread(Context context, Activity activity,String transferFilePath,String transferFileName,String transferType){
        this.context=context;
        this.activity=activity;
        this.transferFilePath=transferFilePath;
        this.transferFileName = transferFileName;
        this.transferType = transferType;

        general = new General(context);

        BluetoothServerSocket temp = null;
        try {
            temp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(context.getString(R.string.app_name), UUID.fromString(AppConfig.SoundieUUID));

        } catch (IOException e) {
            e.printStackTrace();
        }
        bluetoothServerSocket = temp;
    }


    public void run() {
        general.log("BT Listening","value: "+transferFileName);
        BluetoothSocket bluetoothSocket;
        // This will block while listening until a BluetoothSocket is returned
        // or an exception occurs

        if(bluetoothServerSocket == null){
            general.log("BT Listening","bluetoothServerSocket == null");
        }

        while (true) {
            try {
                // start listening for connection requests
                bluetoothSocket = bluetoothServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection is accepted
            if (bluetoothSocket != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        general.log("BT Listening","A connection has been accepted");
                    }
                });

                BluetoothConnectedThread t = new BluetoothConnectedThread(context,bluetoothSocket,transferFilePath,transferFileName,transferType);
                t.start();

                break;
            }else{
                general.log("BT Listening","SOCKET NULL!");
            }
        }
    }

    // Cancel the listening socket and terminate the thread
    public void cancel() {
        try {
            bluetoothServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
