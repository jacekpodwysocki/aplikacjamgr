package com.example.jacekpodwysocki.soundie;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static com.example.jacekpodwysocki.soundie.MenuActivity.general;

/**
 * Created by jacekpodwysocki on 20/11/2016.
 */

public class BluetoothConnectingThread extends Thread {
    private General general;
    private Context context;
    private BluetoothSocket bluetoothSocket;
    private String transferType;
    private final BluetoothDevice bluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public BluetoothConnectingThread(Context context,BluetoothDevice device) {
        this.context=context;
        bluetoothDevice = device;
        general = new General(context);

        BluetoothSocket temp = null;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            temp = device.createRfcommSocketToServiceRecord(UUID.fromString(AppConfig.SoundieUUID));
        } catch (Exception e) {
            Log.e("","Error creating socket");}

        bluetoothSocket = temp;
    }

    public void run() {
        // Cancel any discovery as it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // This will block until it succeeds in connecting to the device
            // through the bluetoothSocket or throws an exception
            bluetoothSocket.connect();
            general.log("BT Connecting","Connected");

            BluetoothConnectedThread t = new BluetoothConnectedThread(context,bluetoothSocket);
            t.start();

        } catch (IOException connectException) {
            Log.e("",connectException.getMessage());
            try {
                // fallback C http://stackoverflow.com/a/25647197/1071719
                general.log("BT Connecting","trying fallback...");
                bluetoothSocket =(BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(bluetoothDevice,1);
                bluetoothSocket.connect();
                general.log("BT Connecting","Connected with failback");

                BluetoothConnectedThread t = new BluetoothConnectedThread(context,bluetoothSocket);
                t.start();
            }catch (Exception e2) {
                general.log("BT Connecting","Couldn't establish Bluetooth connection!");
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
            }
        }
            // Code to manage the connection in a separate thread
            /*
            manageBluetoothConnection(bluetoothSocket);
            */

//            BluetoothConnectedThread ct = new BluetoothConnectedThread(context,bluetoothSocket,"sending");
//            ct.start();
    }

    // Cancel an open connection and terminate the thread
    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}