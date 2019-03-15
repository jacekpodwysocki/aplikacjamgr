package com.example.jacekpodwysocki.soundie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.fragment;
import static android.R.attr.toYDelta;
import static android.R.id.list;
import static android.content.Context.LOCATION_SERVICE;
import static android.provider.Contacts.SettingsColumns.KEY;
import static android.support.v7.widget.AppCompatDrawableManager.get;
import static com.example.jacekpodwysocki.soundie.AppConfig.gpsCheckInterval;
import static com.example.jacekpodwysocki.soundie.MenuActivity.general;
import static com.example.jacekpodwysocki.soundie.MenuActivity.mediaPlayer;
import static com.example.jacekpodwysocki.soundie.R.id.currentSong;
import static com.example.jacekpodwysocki.soundie.R.id.generalStats;
import static com.example.jacekpodwysocki.soundie.R.id.loginBtn;
import static com.example.jacekpodwysocki.soundie.R.id.parent;
import static com.example.jacekpodwysocki.soundie.R.id.rectimage;
import static com.example.jacekpodwysocki.soundie.R.id.slidingMenuContainer;
import static com.example.jacekpodwysocki.soundie.R.id.registrationLastName;
import static java.lang.Integer.parseInt;


public class SoundFinderFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public RequestQueue mRequestQueueSF;

    protected LocationManager locationManager;
    private SQLiteHandler db;
    protected LocationListener locationListener;
    protected Context context;
    private General general;
    private MarkerTag markerTag;
    public MapView mapView;
    public GoogleMap map;
    public LatLng latLng;
    public Location mLastLocation;
    public Marker myLocationMarker;
    public List visibleUsers;
    public Integer selectedUserId;
    public Boolean gpsSignalAcquired = false;
    public RelativeLayout gpsLoaderContainer;
    private Song currentSong;
    final Handler gpsCheckHandler = new Handler();
    private ProgressDialog pDialog;


    // Handler to update users on the map
    private Handler usersHandler = new Handler();
    private Marker userMarker;
    private List<Marker> userMarkers;
    private RelativeLayout slidingMenuContainer;
    private ImageView closeIcon;
    private TextView infoWindowFirstLastName;
    private TextView infoWindowSongTitle;
    private TextView infoWindowSongArtist;
    private TextView infoWindowCity;
    public static final String volleyRequestsTagSF = "volleyRequestsTagSF";

    private final Handler fileRequestResponseHandler = new Handler();
    private Runnable runn;
    private Runnable runnRequest;
    public static Integer lastRequestRowId;

    // current song list
    private List<RowItemOptions> rowItems;
    private OptionsListAdapter adapter;
    private OptionsListAdapter adapterHistory;
    private ListView currentSongListView;

    // songs history
    private List<RowItemOptions> rowItemsHistory;
    private ListView previousSongsListView;

    protected static final int SONGMENU_OPTION1 = 1;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_sound_finder, container, false);

        slidingMenuContainer = (RelativeLayout) rootView.findViewById(R.id.slidingMenuContainer);
        slidingMenuContainer.animate().translationY(550).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                infoWindowFirstLastName.setText("Pobieranie...");
                infoWindowCity.setText("Warszawa");
            }
        });

        general = MenuActivity.general;

        userMarkers = new ArrayList<Marker>();

        // SQLite database handler
        db = new SQLiteHandler(getActivity());

        rowItems = new ArrayList<RowItemOptions>();
        rowItemsHistory = new ArrayList<RowItemOptions>();

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        gpsLoaderContainer = (RelativeLayout) rootView.findViewById(R.id.gpsLoaderContainer);

        mapView.onResume(); // show map immediately


        slidingMenuContainer.setVisibility(View.INVISIBLE);
        closeIcon = (ImageView) rootView.findViewById(R.id.closeIcon);
        //slidingMenuContainer.animate().translationY(375);

        infoWindowFirstLastName = (TextView) rootView.findViewById(R.id.infoName);
        infoWindowSongArtist = (TextView) rootView.findViewById(R.id.songArtist);
        infoWindowSongTitle = (TextView) rootView.findViewById(R.id.songTitle);
        infoWindowCity = (TextView) rootView.findViewById(R.id.infoCity);

        closeIcon.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
//                slidingMenuContainer.animate().translationY(375).setDuration(300);
                hideSlidingMenuContainer();
                setLoadingText();
            }

        });

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            general.showToast("włącz GPS!",getActivity());
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        currentSongListView = (ListView) rootView.findViewById(R.id.currentSong);
        previousSongsListView = (ListView) rootView.findViewById(R.id.previousSongs);

        registerForContextMenu(currentSongListView);

        RowItemOptions items = new RowItemOptions("Pobieranie...","--");
        rowItems.add(items);
        rowItemsHistory.add(items);
        adapter =new OptionsListAdapter(rootView.getContext(), rowItems);
        adapterHistory = new OptionsListAdapter(rootView.getContext(), rowItemsHistory);
        currentSongListView.setAdapter(adapter);
        previousSongsListView.setAdapter(adapterHistory);



        slidingMenuContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                slidingMenuContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                general.log("MAP","height ----> : "+slidingMenuContainer.getHeight());
//                hideSlidingMenuContainer();
            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mapViewMap) {
                general.log("MAP","map ready");

                map = mapViewMap;

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()

                {
                    @Override
                    public boolean onMarkerClick ( final Marker marker) {
                        setLoadingText();

                        MarkerTag marketag = (MarkerTag) marker.getTag();
                        if (marketag != null) {
                            showSlidingMenuContainer();
                            final Integer clickedUserId = marketag.getUserId();
                            selectedUserId = clickedUserId;
//                        getUserDetails(markerTag.getUserId());

                            Projection projection = map.getProjection();

                            // center to current marker
                            LatLng markerLatLng = new LatLng(marker.getPosition().latitude,
                                    marker.getPosition().longitude);
                            Point markerScreenPosition = projection.toScreenLocation(markerLatLng);
                            Point pointHalfScreenAbove = new Point(markerScreenPosition.x,
                                    markerScreenPosition.y + (mapView.getHeight() / 4));

                            LatLng aboveMarkerLatLng = projection
                                    .fromScreenLocation(pointHalfScreenAbove);

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(aboveMarkerLatLng).zoom(map.getCameraPosition().zoom).build();

                            map.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));


                            // POPRAWIC, NIE ZGADZAJA SIE WARTOSCI W TABLICy
//                            for(int i=0; i<userMarkers.size(); i++) {
//
//                                MarkerTag removeMarkertag = (MarkerTag) userMarkers.get(i).getTag();
////                        general.log("MAP","markerTag: "+activeMarkertag + " marker info: "+userMarkers.get(i).getId());
//                                if(removeMarkertag.getUserId() == marketag.getUserId()){
//                                    userMarkers.get(i).remove();
//                                    general.log("MAP","element removed");
//                                }else{
//                                    general.log("MAP","element NOT removed");
//                                }
//                            }
//                            marker.remove();
//
//                            MarkerOptions activeMarkerOptions = new MarkerOptions();
//                            markerTag = new MarkerTag(clickedUserId, true);
//                            Marker userMarker = map.addMarker(activeMarkerOptions.position(markerLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_music_active)));
//                            userMarker.setTag(markerTag);
//                            userMarkers.add(userMarker);


                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    getUserDetails(clickedUserId);
                                    showCurrentFile(clickedUserId);
                                    getSongsHistory(clickedUserId,6);
                                }
                            });

                            return true;
                        }else{
                            return false;
                        }
                    }
                });


                //map.setMyLocationEnabled(true);


//                String mprovider;
//                Criteria criteria = new Criteria();
//                mprovider = locationManager.getBestProvider(criteria, false);
//                Location location = locationManager.getLastKnownLocation(mprovider);

                LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String bestProvider = locationManager.getBestProvider(criteria, false);
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    general.showToast("włącz GPS!",getActivity());
                }
                Location location = locationManager.getLastKnownLocation(bestProvider);


                if (location != null) {
                    general.log("MAP", "lokalizacja OK");
                    gpsSignalAcquired = true;
                    hideGpsSignalContainer();

                    Double lat,lon;
                    if (myLocationMarker != null) {
                        myLocationMarker.remove();
                    }
                    try {
                        lat = location.getLatitude ();
                        lon = location.getLongitude ();

                    saveLocation(parseInt(db.getUserId()    ),lat,lon);

                        LatLng initialLatLng;
                        initialLatLng = new LatLng(lat, lon);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(initialLatLng);
                        markerOptions.title("Twoja lokalizacja");
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_location));
                        myLocationMarker = map.addMarker(markerOptions);

//                    //zoom to current position:
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(initialLatLng).zoom(14).build();

                    map.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));

                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                        return;
                    }
                }else {
                    gpsSignalAcquired = false;
                    gpsCheckHandler.postDelayed(runn = new Runnable() {
                        public void run() {
                            checkGpsSignal();
                            gpsCheckHandler.postDelayed(this, gpsCheckInterval);
                        }
                    }, gpsCheckInterval);

                    // zoom to default location
                    LatLng defaultLocation = new LatLng(52.2296756, 21.012228700000037);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(defaultLocation).zoom(10).build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                general.log("MAP","map ready, get visible users");
                updateUsersOnTheMap();

            }
        });

        return rootView;
    }

    public LatLng getLocation()
    {
        // Get the location manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            general.showToast("włącz GPS!",getActivity());
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        try {
            lat = location.getLatitude ();
            lon = location.getLongitude ();
            return new LatLng(lat, lon);
        }
        catch (NullPointerException e){
            Log.i("MAP","ERROR getLocation()");
            e.printStackTrace();
            return null;
        }
    }


    // create options menu for song lists
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // Set title for the context menu

            menu.setHeaderTitle("Pobieranie utworu ");
            menu.add(Menu.NONE, SONGMENU_OPTION1, 0, "Poproś użytkownika o utwór");

    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // action for file request
        switch (item.getItemId()) {
            // option to request file transfer
            case SONGMENU_OPTION1:
                RowItemOptions rowItemSelected = (RowItemOptions) adapter.getItem(0); // first item of the current song list holding only one item
                Toast.makeText(getActivity().getApplicationContext(), "Option 1: ID " + info.id +", position " + info.position + ", songId: "+rowItemSelected.getSongId() +", song Checksum: "+rowItemSelected.getSongChecksum(), Toast.LENGTH_SHORT).show();

                saveTransferRequest(parseInt(db.getUserId()),selectedUserId,rowItemSelected.getSongId(),0);
                pDialog.setMessage("Oczekiwanie na odpowiedź użytkownika");
                showDialog();
                fileRequestResponseHandler.postDelayed(runnRequest = new Runnable() {
                    public void run() {
                        general.log("MAP","last request row id: "+lastRequestRowId);
                        getFileRequestResponse(lastRequestRowId);
                        fileRequestResponseHandler.postDelayed(this,AppConfig.fileTransferCheckInterval);
                    }
                }, AppConfig.fileTransferCheckInterval);

                break;
        }
        return true;
    }



    @Override
    public void onMapReady(GoogleMap gMap) {

    }



    @Override
    public void onConnected(Bundle bundle) {
//
    }

    @Override
    public void onConnectionSuspended(int i) {
//        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position when location changes
//        map.clear();
        if (myLocationMarker != null) {
            myLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        saveLocation(parseInt(db.getUserId()),location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Twoja lokalizacja");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_location));
        myLocationMarker = map.addMarker(markerOptions);

        //zoom to current position:
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(latLng).zoom(14).build();
//
//        map.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onPause () {
        // cancell all volley requests on stop
        super.onPause();
        RequestQueue volleyRequestQueue = AppController.getInstance().getRequestQueue();

        usersHandler.removeCallbacks(usersUpdateTimeTask);

        volleyRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                general.log("MAP","request running: "+request.getTag().toString());
                return true;
            }
        });

        if (mRequestQueueSF != null) {
            general.log("MAP","queue: "+volleyRequestQueue);
            general.log("MAP","cancel volley requests");
            mRequestQueueSF.cancelAll(volleyRequestsTagSF);
        }

    }



    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

//    @Override
//    public void onResume() {
//        Log.i(getResources().getString(R.string.debugTag), "onResume inside fragment");
//
//        super.onResume();
//        mapView.onResume();
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(52.207748, 21.014918);
//        map.addMarker(new MarkerOptions().position(sydney).title("Warszawa"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }

    public Boolean locationPermissions(){
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Function to store users location in db
     * */
    private void saveLocation(final Integer userId, final Double latitude, final Double longitude) {
        // Tag used to cancel the request
        String tag_string_req = "req_saveloc";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SAVELOCATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        general.log("MAP","ERROR saving users location: "+errorMsg);
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
                    //general.showToast(error.getMessage(), getActivity());
                    general.log("MAP ERROR saving LOC",error.getMessage());
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
                // Posting params to save location url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", String.valueOf(userId));
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));

                return params;
            }

        };

        strReq.setTag(volleyRequestsTagSF);

        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(strReq, volleyRequestsTagSF);
    }

    /**
     * Function to store file transfer request for a user
     * */
    private void saveTransferRequest(final Integer userId, final Integer selectedUserId, final Integer songId, final Integer status) {
        // Tag used to cancel the request
        String tag_string_req = "req_savetansferrequest";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SAVETRANSFERREQUEST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String rowId = jObj.getString("rowId");
                        lastRequestRowId = Integer.valueOf(rowId);

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        general.log("MAP","ERROR saving transfer request: "+errorMsg);
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
                // Posting params to save location url
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", String.valueOf(userId));
                params.put("selectedUserId", String.valueOf(selectedUserId));
                params.put("songId", String.valueOf(songId));
                params.put("status", String.valueOf(status));

                return params;
            }

        };

        strReq.setTag(volleyRequestsTagSF);

        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(strReq, volleyRequestsTagSF);
    }


    /**
     * Function to get users from db
     * */
    private void getVisibleUsers(final Integer currentUserId, final Double nwLatitude,final Double nwLongitude,final Double seLatitude,final Double seLongitude) {
        // Tag used to cancel the request
        String tag_string_req = "req_getvisibleusers";


        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETACTIVEUSERS+currentUserId+","+nwLatitude+","+nwLongitude+","+seLatitude+","+seLongitude, new Response.Listener<String>() {



            @Override
            public void onResponse(String response) {
                try {
//                    general.log("MAP","user updated on the map, user markers count: "+userMarkers.size());
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // remove user markers
                    Integer activeMarkerId = -1;
                    for(int i=0; i<userMarkers.size(); i++) {

                        MarkerTag activeMarkertag = (MarkerTag) userMarkers.get(i).getTag();
//                        general.log("MAP","markerTag: "+activeMarkertag + " marker info: "+userMarkers.get(i).getId());
                        //general.log("MAP","nooowy: "+activeMarketag.getUserId() + " - "+ activeMarketag.getIsActive());
                        Boolean activeMarker = activeMarkertag.getIsActive();
//                        get active marker
                        if(activeMarker == true){
                            activeMarkerId = activeMarkertag.getUserId();
                        }
                        userMarkers.get(i).remove();
                    }
                    userMarkers.clear();

                    if (!error) {

                        String usersMsg = jObj.getString("visibleUsers");
                        JSONArray activeUsersArray = new JSONArray(usersMsg);


                        for(int i=0; i<activeUsersArray.length(); i++){
                            JSONObject json_data = activeUsersArray.getJSONObject(i);

                            LatLng activeMarker = new LatLng(json_data.getDouble("Latitude"), json_data.getDouble("Longitude"));

                            if(userMarkers.size() > 0) {
                                if (json_data.getInt("userId") == activeMarkerId) {
                                    Marker userMarker = map.addMarker(new MarkerOptions().position(activeMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_music_active)));
                                    markerTag = new MarkerTag(json_data.getInt("userId"), true);
                                    userMarker.setTag(markerTag);
                                    userMarkers.add(userMarker);
                                } else {
                                    Marker userMarker = map.addMarker(new MarkerOptions().position(activeMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_music)));
                                    markerTag = new MarkerTag(json_data.getInt("userId"), false);
                                    userMarker.setTag(markerTag);
                                    userMarkers.add(userMarker);
                                }
                            }else{
                                Marker userMarker = map.addMarker(new MarkerOptions().position(activeMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_music)));
                                markerTag = new MarkerTag(json_data.getInt("userId"), false);
                                userMarker.setTag(markerTag);
                                userMarkers.add(userMarker);
                            }



                        }



                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        general.log("MAP","ERROR getting users list for map: "+errorMsg);
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


        };
        strReq.setTag(volleyRequestsTagSF);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, volleyRequestsTagSF);
    }

    /**
     * Update users on the map
     * */
    public void updateUsersOnTheMap() {
        usersHandler.postDelayed(usersUpdateTimeTask, AppConfig.usersUpdateInterval);
    }
    /**
     * Background Runnable thread
     * place other users on the map
     * */
    private Runnable usersUpdateTimeTask = new Runnable() {
        public void run() {

            // get visible area coordinates NW, SE
            LatLngBounds curScreen = map.getProjection()
                    .getVisibleRegion().latLngBounds;

//            general.log("MAP",String.valueOf(curScreen.northeast.latitude) + " - " + String.valueOf(curScreen.southwest.latitude));

            getVisibleUsers(parseInt(db.getUserId()),curScreen.southwest.latitude,curScreen.southwest.longitude,curScreen.northeast.latitude,curScreen.northeast.longitude);

            usersHandler.postDelayed(this, AppConfig.usersUpdateInterval);
        }
    };

    /**
     * Function to get selected user from db
     * parameter: userId
     * */
    private void getUserDetails(final Integer userId) {
        // Tag used to cancel the request
        String tag_string_req = "req_getuserdetails";


        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETUSERDETAILS+userId, new Response.Listener<String>() {





            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        String usersMsg = jObj.getString("userDetails");

                        JSONArray userDetailsArray = new JSONArray(usersMsg);


                        for(int i=0; i<userDetailsArray.length(); i++){

                            JSONObject json_data = userDetailsArray.getJSONObject(i);

                            infoWindowFirstLastName.setText(json_data.getString("FirstName") + " " + json_data.getString("LastName"));

                        }

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


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showSlidingMenuContainer(){
        slidingMenuContainer.setVisibility(View.VISIBLE);
        slidingMenuContainer.animate().translationY(0).setDuration(300);
    }
    private void hideSlidingMenuContainer(){
        DisplayMetrics lDisplayMetrics = getResources().getDisplayMetrics();
        Integer heightPixels = lDisplayMetrics.heightPixels;
        Integer elemHeight = slidingMenuContainer.getHeight() + 50;
        slidingMenuContainer.animate().translationY(elemHeight).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                infoWindowFirstLastName.setText("Pobieranie...");
                infoWindowCity.setText("--");
            }
        });

    }


    private void showCurrentFile(final Integer userId){
        // Tag used to cancel the request
        String tag_string_req = "req_getcurrentfiledetails";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETCURRENTFILE+userId, new Response.Listener<String>() {



            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        String songMsg = jObj.getString("fileDetails");

                        JSONArray fileDetailsArray = new JSONArray(songMsg);


                        for(int i=0; i<fileDetailsArray.length(); i++){

                            JSONObject json_data = fileDetailsArray.getJSONObject(i);

                            Song currentSong = new Song(json_data.getString("fileTitle"),json_data.getString("fileArtist"),Integer.valueOf(json_data.getString("fileId")),json_data.getString("fileChecksum"));
                            String currentSongTitle = currentSong.getTitle();
                            String currentSongArtist = currentSong.getArtist();
                            Integer currentSongId = currentSong.getDbId();
                            String currentSongChecksum = currentSong.getDbChecksum();

//                            rowItems.clear();
//                            RowItemOptions items = new RowItemOptions(currentSongTitle,currentSongArtist);
//                            rowItems.add(items);
//
//                            // update listview
//                            adapter.notifyDataSetChanged();
                                rowItems.clear();
                                RowItemOptions items = new RowItemOptions(currentSongTitle,currentSongArtist,currentSongId,currentSongChecksum);

                                rowItems.add(items);
                                adapter =new OptionsListAdapter(getActivity().getApplicationContext(), rowItems);
                                currentSongListView.setAdapter(adapter);

                        }

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        rowItems.clear();
                        RowItemOptions items = new RowItemOptions("Brak odtwarzanego utworu","");

                        rowItems.add(items);
                        adapter =new OptionsListAdapter(getActivity().getApplicationContext(), rowItems);
                        currentSongListView.setAdapter(adapter);
                        general.log("MAP","ERROR getting current file info: "+errorMsg);
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


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * gets songs history for selected user
     */
    private void getSongsHistory(final Integer userId, final Integer limit){
        // Tag used to cancel the request
        String tag_string_req = "req_gesongshistory";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETSONGSHISTORY+userId+","+limit, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        int totalHeight = 0;
                        String songMsg = jObj.getString("songsHistory");

                        JSONArray songsHistoryArray = new JSONArray(songMsg);

                        rowItemsHistory.clear();
                        for(int i=0; i<songsHistoryArray.length(); i++){

                            JSONObject json_data = songsHistoryArray.getJSONObject(i);

                            Song songHistory = new Song(json_data.getString("fileTitle"),json_data.getString("fileArtist"));
                            String historySongTitle = songHistory.getTitle();
                            String historySongArtist = songHistory.getArtist();


                            RowItemOptions itemsHistory = new RowItemOptions(historySongTitle,historySongArtist);

                            rowItemsHistory.add(itemsHistory);

                        }
                        // adjust height C Kenneth Flynn https://kennethflynn.wordpress.com/2012/09/12/putting-android-listviews-in-scrollviews/
                        for (int i = 0; i < adapterHistory.getCount(); i++) {
                            View listItem = adapterHistory.getView(i, null, previousSongsListView);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }

                        ViewGroup.LayoutParams params = previousSongsListView.getLayoutParams();
                        params.height = 20 + totalHeight + (previousSongsListView.getDividerHeight() * (adapterHistory.getCount() - 1));
                        previousSongsListView.setLayoutParams(params);
                        previousSongsListView.requestLayout();
                        adapterHistory = new OptionsListAdapter(getActivity().getApplicationContext(), rowItemsHistory);
                        previousSongsListView.setAdapter(adapterHistory);

                    } else {
                        // Error occurred, get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        ViewGroup.LayoutParams params = previousSongsListView.getLayoutParams();
                        // restore height if no items available
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        previousSongsListView.setLayoutParams(params);
                        previousSongsListView.setLayoutParams(params);
                        previousSongsListView.requestLayout();

                        rowItemsHistory.clear();
                        RowItemOptions itemsHistory = new RowItemOptions("Brak historii","");

                        rowItemsHistory.add(itemsHistory);
                        adapterHistory =new OptionsListAdapter(getActivity().getApplicationContext(), rowItemsHistory);
                        previousSongsListView.setAdapter(adapterHistory);
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


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void setLoadingText(){
        rowItemsHistory.clear();
        RowItemOptions itemsLoading = new RowItemOptions("Pobieranie...","");

        rowItems.clear();
        rowItemsHistory.clear();


        rowItems.add(itemsLoading);
        adapter = new OptionsListAdapter(getActivity().getApplicationContext(), rowItems);
        currentSongListView.setAdapter(adapter);

        rowItemsHistory.add(itemsLoading);
        adapterHistory =new OptionsListAdapter(getActivity().getApplicationContext(), rowItemsHistory);
        previousSongsListView.setAdapter(adapterHistory);

    }

    public void checkGpsSignal(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            gpsSignalAcquired = false;
            showGpsSignalContainer();
        }else if(location == null){
            gpsSignalAcquired = false;
            showGpsSignalContainer();
        }else if(location != null){
            gpsSignalAcquired = true;
            hideGpsSignalContainer();
//            LatLng foundLatLong = new LatLng(location.getLatitude(),location.getLongitude());
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(foundLatLong).zoom(14).build();
//
//            map.animateCamera(CameraUpdateFactory
//                    .newCameraPosition(cameraPosition));
//            gpsCheckHandler.removeCallbacks(runn);

        }
    }

    public void showGpsSignalContainer(){
        gpsLoaderContainer.setVisibility(View.VISIBLE);
    }
    public void hideGpsSignalContainer(){
        // run on UI thread in order to have acces to elemnt to hide
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gpsLoaderContainer.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void getFileRequestResponse(final Integer requestId){
        // Tag used to cancel the request
        String tag_string_req = "req_getfilerequestresponse";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETFILEREQUESTRESPONSE+requestId, new Response.Listener<String>() {
            Integer status = 0;


            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        String responseMsg = jObj.getString("requestResponseStatus");

                        general.log("MAP","response message: "+responseMsg);

                        status = Integer.valueOf(responseMsg);

                        handleFileRequestResponse(status,requestId);

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        general.log("MAP","ERROR getting file request status: "+errorMsg);
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


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void handleFileRequestResponse(Integer requestStatus, Integer requestId){
        general.log("MAP","handleFileRequestResponse");
        if(requestStatus.equals(0)){
            general.log("MAP","status 0");
        }else if(requestStatus.equals(1)) {
            general.log("MAP", "status 1 -> -> zatrzymaj sprawdzanie statusu -> zamknij okno -> rozpocznij transfer");
            hideDialog();
            fileRequestResponseHandler.removeCallbacks(runnRequest);
            // go to tranfer activity
            Intent intent = new Intent(getActivity(), TransferActivity.class);
            Bundle b = new Bundle();
            b.putString("transferType", "Odbieranie");
            b.putInt("requestId", requestId);
            intent.putExtras(b);
            startActivity(intent);


        }else if(requestStatus.equals(2) || requestStatus.equals(3)){
            hideDialog();
            fileRequestResponseHandler.removeCallbacks(runnRequest);
        }else{
            general.log("MAP","status nieznany");
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
