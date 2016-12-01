package com.example.jacekpodwysocki.soundie;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static android.R.attr.action;
import static android.R.attr.host;
import static android.R.attr.name;
import static android.R.attr.path;
import static com.example.jacekpodwysocki.soundie.MenuActivity.file;
import static com.example.jacekpodwysocki.soundie.MenuActivity.general;
import static com.example.jacekpodwysocki.soundie.R.id.loginBtn;
import static com.example.jacekpodwysocki.soundie.R.id.slidingMenuContainer;
import static com.example.jacekpodwysocki.soundie.R.id.songArtistTransfer;
import static java.lang.Thread.sleep;

public class TransferActivity extends AppCompatActivity {
    private static Context tContext;
    private static Activity activity;
    private General general;

    private final static int REQUEST_ENABLE_BT = 1; // request code for onActivityResult
    private UUID SoundieUUID;
    private String transferFileChecksum;
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

    private TextView songTitleTransfer;
    private TextView songArtistTransfer;

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

    private String transferType;
    private Integer requestId;
    private String transferFilePath;
    private String transferFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        TransferActivity.tContext = getApplicationContext();
        general = new General(this);
        activity = this;

        SoundieUUID = UUID.fromString((AppConfig.SoundieUUID));

        songTitleTransfer = (TextView) findViewById(R.id.songTitleTransfer);
        songArtistTransfer = (TextView) findViewById(R.id.songArtistTransfer);

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

        if(b != null) {
            transferType = b.getString("transferType");
            requestId = b.getInt("requestId");
        }else{
            transferType = "empty";
        }
        getSupportActionBar().setTitle("Transfer BT: "+transferType + "(id "+requestId+")");

        getFileDetails(requestId);

        BluetoothListeningThread t = new BluetoothListeningThread(tContext,activity,transferFilePath,transferFileName,transferType);
        t.start();


            visibleDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RowItemDevices deviceClick = (RowItemDevices) parent.getItemAtPosition(position);
                    String deviceAddress = deviceClick.getDeviceAddress();
                    BluetoothDevice selectedDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);

                    BluetoothConnectingThread t = new BluetoothConnectingThread(tContext, selectedDevice,transferFilePath,transferFileName,transferType);
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


    /**
     * Function to get file information by passing request id
     * parameter: requestId
     * */
    private void getFileDetails(final Integer requestId) {
        // Tag used to cancel the request
        String tag_string_req = "req_getfiledetails";


        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETFILEDETAILS+requestId, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        String fileMsg = jObj.getString("fileDetails");

                        JSONArray fileDetailsArray = new JSONArray(fileMsg);


                        for(int i=0; i<fileDetailsArray.length(); i++){

                            JSONObject json_data = fileDetailsArray.getJSONObject(i);
                            songTitleTransfer.setText(json_data.getString("fileTitle"));
                            songArtistTransfer.setText(json_data.getString("fileArtist"));
                            transferFileChecksum = json_data.getString("fileChecksum");
                        }

                        getSongByChecksum();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        general.log("MAP","ERROR getting user details: "+errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().toLowerCase().contains("network is unreachable")) {
                    general.showToast("Brak połączenia z internetem!\nWłącz sieć, aby móc korzystać z aplikacji", activity);
                }else{
                    general.showToast(error.getMessage(), activity);
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


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void getSongByChecksum() {
        general.log("Transfer Activity","getSongByChecksum");
        ContentResolver musicResolver = activity.getContentResolver();
        String fileExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String sel = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selExtARGS = new String[]{fileExtension};
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = musicResolver.query(musicUri, null, sel, selExtARGS, null);


        if(songCursor!=null && songCursor.moveToFirst()){
            //get columns

            int titleColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM);
            long albumIdColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM_ID);
            int mimeColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.MIME_TYPE);
            int fullpathColumn = songCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.DATA);


            //add songs to list

            do {
                long thisId = songCursor.getLong(idColumn);
                String thisTitle = songCursor.getString(titleColumn);
                String thisArtist = songCursor.getString(artistColumn) ;
                String thisAlbum = songCursor.getString(albumColumn);
                String thisPath = songCursor.getString(fullpathColumn);

                // get file name from path
                java.io.File file = new java.io.File(thisPath);
                String fileName = file.getName();

                if(general.getMD5EncryptedString(thisPath).equals(transferFileChecksum)) {
                    general.log("Transfer Activity", "ZNALEZIONY: Id: " + thisId + " thisPath: " + thisPath + " Title: " + thisTitle + " Filename: " + fileName);
                    transferFileName = fileName;
                    transferFilePath = thisPath;
                }else{
                    general.log("Transfer Activity", "NIE TEN PLIK");
                }
            }
            while (songCursor.moveToNext());
        }

        general.log("Transfer Activity","check: "+transferFileName+"==="+transferFilePath+"==="+transferType);

    }

}
