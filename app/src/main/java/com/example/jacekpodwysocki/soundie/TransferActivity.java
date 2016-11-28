package com.example.jacekpodwysocki.soundie;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.R.attr.action;
import static android.R.attr.host;
import static android.R.attr.name;
import static com.example.jacekpodwysocki.soundie.MenuActivity.file;
import static com.example.jacekpodwysocki.soundie.MenuActivity.general;
import static com.example.jacekpodwysocki.soundie.R.id.loginBtn;
import static java.lang.Thread.sleep;

public class TransferActivity extends AppCompatActivity {
    private static Context tContext;
    private static Activity activity;
    private General general;

    private final static int REQUEST_ENABLE_BT = 1; // request code for onActivityResult
    private UUID SoundieUUID;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice btDevice;
    private Parcelable[] btUuid;
    private BroadcastReceiver bluetoothReceiver;
    private Button beginTransferBtn;
    private Button cancelTransferBtn;
    private Button startSearching;
    private Button btnSendFile;


    private List<RowItemDevices> rowItemsDevices;
    private DevicesListAdapter devicesAdapter;
    private ListView visibleDevices;


    private TextView hostDeviceBtName;
    private TextView targetDeviceBtName;

    private LinearLayout stepOne;
    private LinearLayout stepTwo;
    private LinearLayout stepThree;

    private ProgressBar progressBarHostStepOne;
    private ImageView tickHostStepOne;
    private ImageView alertHostStepOne;
    private ProgressBar progressBarTargetStepOne;
    private ImageView tickTargetStepOne;
    private ImageView alertTargetStepOne;

    private ProgressBar progressBarHostStepTwo;
    private ImageView tickHostStepTwo;
    private ImageView alertHostStepTwo;
    private ProgressBar progressBarTargetStepTwo;
    private ImageView tickTargetStepTwo;
    private ImageView alertTargetStepTwo;

    private ProgressBar progressBarHostStepThree;
    private ImageView tickHostStepThree;
    private ImageView alertHostStepThree;
    private ProgressBar progressBarTargetStepThree;
    private ImageView tickTargetStepThree;
    private ImageView alertTargetStepThree;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        TransferActivity.tContext = getApplicationContext();
        general = new General(this);
        activity = this;

        SoundieUUID = UUID.fromString((AppConfig.SoundieUUID));

        beginTransferBtn = (Button) findViewById(R.id.beginTransferButton);
        cancelTransferBtn = (Button) findViewById(R.id.cancelTransferButton);
        startSearching = (Button) findViewById(R.id.startSearching);
        btnSendFile = (Button) findViewById(R.id.btnSendFile);

        hostDeviceBtName = (TextView) findViewById(R.id.hostDeviceBtName);
        targetDeviceBtName = (TextView) findViewById(R.id.targetDeviceBtName);

        // layouts for steps
        stepOne = (LinearLayout) findViewById(R.id.stepOne);
        stepTwo = (LinearLayout) findViewById(R.id.stepTwo);
        stepThree = (LinearLayout) findViewById(R.id.stepThree);

        // step one
        progressBarHostStepOne = (ProgressBar) findViewById(R.id.progressBarHostStepOne);
        tickHostStepOne = (ImageView) findViewById(R.id.tickHostStepOne);
        alertHostStepOne = (ImageView) findViewById(R.id.alertHostStepOne);
        progressBarTargetStepOne = (ProgressBar) findViewById(R.id.progressBarTargetStepOne);
        tickTargetStepOne = (ImageView) findViewById(R.id.tickTargetStepOne);
        alertTargetStepOne = (ImageView) findViewById(R.id.alertTargetStepOne);

        // step two
        progressBarHostStepTwo = (ProgressBar) findViewById(R.id.progressBarHostStepTwo);
        tickHostStepTwo = (ImageView) findViewById(R.id.tickHostStepTwo);
        alertHostStepTwo = (ImageView) findViewById(R.id.alertHostStepTwo);
        progressBarTargetStepTwo = (ProgressBar) findViewById(R.id.progressBarTargetStepTwo);
        tickTargetStepTwo = (ImageView) findViewById(R.id.tickTargetStepTwo);
        alertTargetStepTwo = (ImageView) findViewById(R.id.alertTargetStepTwo);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBluetoothAdapter.getBondedDevices();


        // list for available devices
        visibleDevices = (ListView) findViewById(R.id.visibleDevices);
        rowItemsDevices = new ArrayList<RowItemDevices>();

        enableBluetoothVisibility();

        Bundle b = getIntent().getExtras();
        String value = null; // or other values
        if(b != null)
            value = b.getString("key");

//        if(value.equals("server")){

        BluetoothListeningThread t = new BluetoothListeningThread(tContext,activity);
        t.start();


            visibleDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RowItemDevices deviceClick = (RowItemDevices) parent.getItemAtPosition(position);
                    String deviceAddress = deviceClick.getDeviceAddress();
                    BluetoothDevice selectedDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);

                    BluetoothConnectingThread t = new BluetoothConnectingThread(tContext, selectedDevice);
                    t.start();
                }
        });

        btnSendFile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                general.log("Transfer Activity", "Send file button clicked");


            }

        });

//        beginTransferBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                performChecks();
//                startDevicesDiscovery();
//            }
//
//        });
//
        startSearching.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startDevicesDiscovery();
            }
        });
    }

    private void doUpdate() {
        btnSendFile.setText("I've been updated.");
    }



    private Boolean isBluetoothSupported(){
        return mBluetoothAdapter != null;
    }

    private void requestBluetoothStart(){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private Boolean bluetoothEnabled(){
        return mBluetoothAdapter.isEnabled();
    }

    private void enableBluetoothVisibility(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);
    }

    private Boolean hostDeviceDiscoverable(){
        return mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
    }

    public Boolean pairedDevicesExist(){
        return pairedDevices.size() > 0;
    }

    public ArrayList getPairedDevices(){
        ArrayList<String> pairedDevicesList = new ArrayList<String>();
        for (BluetoothDevice device : pairedDevices) {
            // Add the name and address to an array adapter to show in a ListView
            pairedDevicesList.add(device.getName() + "\n" + device.getAddress());
        }
        return pairedDevicesList;
    }

    public String getHostDeviceBluetoothName(){
        if(isBluetoothSupported()) {
            String deviceBtName = mBluetoothAdapter.getName();
            if (deviceBtName == null) {
                deviceBtName = mBluetoothAdapter.getAddress();
            }
            return deviceBtName;
        }else{
            return "Brak informacji";
        }

    }

    public void disableBluetooth(){
        mBluetoothAdapter.disable();
    }

    @Override
    protected void onStop() {
//        mBluetoothAdapter.unRegisterReceiver();
        super.onStop();
        unregisterReceiver(bluetoothReceiver);
        bluetoothReceiver = null;
    }

    public void performChecks(){

        beginTransferBtn.setVisibility(View.GONE);
        cancelTransferBtn.setVisibility(View.VISIBLE);

        // device recognize
        hostDeviceBtName.setText(getHostDeviceBluetoothName());

        // step One
        stepOne.setAlpha(1f);

        // host
        if(bluetoothEnabled()) {
            tickHostStepOne.setVisibility(View.VISIBLE);
        }else{
            alertHostStepOne.setVisibility(View.VISIBLE);
        }

        // target
        progressBarTargetStepOne.setVisibility(View.VISIBLE);

        // step Two
        stepTwo.setAlpha(1f);

        //host
        if(hostDeviceDiscoverable()) {
            tickHostStepTwo.setVisibility(View.VISIBLE);
        }else{
            alertHostStepTwo.setVisibility(View.VISIBLE);
        }

        //target
        progressBarTargetStepTwo.setVisibility(View.VISIBLE);
    }

    public void startDevicesDiscovery(){

        rowItemsDevices.clear();
        devicesAdapter =new DevicesListAdapter(tContext, rowItemsDevices);

        if(bluetoothReceiver != null) {
            unregisterReceiver(bluetoothReceiver);
            bluetoothReceiver = null;
        }

        //cancel discovery if discovering
        if (mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        bluetoothReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                General general = new General(tContext);
                String action = intent.getAction();

                // if device found
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    btUuid = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);

//                    if (btUuid != null) {
//                        for (Parcelable p : btUuid) {
//                            general.log("BT","uuidExtra " + p);
//                        }
//                    } else {
//
//                        general.log("BT","uuidExtra NULL");
//                    }

                    if(btDevice == null){
                        general.log("BT","device NULL");
                    }else{

                        Device device = new Device(btDevice.getName(),btDevice.getAddress());

                        RowItemDevices items = new RowItemDevices(device.getDeviceName(),device.getMacAddress());
                        rowItemsDevices.add(items);
                        devicesAdapter.notifyDataSetChanged();
                    }

                }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    general.log("BT","device search finished");
                }

            }
        };

        visibleDevices.setAdapter(devicesAdapter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver, filter);

        mBluetoothAdapter.startDiscovery();

    }

    public void convertFileToByteArray(){
        String fileUri = "file:///system/media/Pre-loaded/Music/Bach_Suite.mp3";

        java.io.File f = new java.io.File("/system/media/Pre-loaded/Music/Bach_Suite.mp3");
        Uri imageUri = Uri.fromFile(f);

        InputStream inputStream = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        try {

            inputStream = getContentResolver().openInputStream(imageUri);


            int buffersize = 1024;
            byte[] buffer = new byte[buffersize];

            int len = 0;
            while((len = inputStream.read(buffer)) != -1){
                byteBuffer.write(buffer, 0, len);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        general.log("BT","file buffer array: "+byteBuffer.toByteArray());

    }

}
