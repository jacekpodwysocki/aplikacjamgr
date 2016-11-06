package com.example.jacekpodwysocki.soundie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
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

import static android.R.attr.fragment;
import static android.R.id.list;
import static android.content.Context.LOCATION_SERVICE;
import static android.provider.Contacts.SettingsColumns.KEY;
import static com.example.jacekpodwysocki.soundie.MenuActivity.general;
import static com.example.jacekpodwysocki.soundie.MenuActivity.mediaPlayer;
import static java.lang.Integer.parseInt;


public class SoundFinderFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected LocationManager locationManager;
    private SQLiteHandler db;
    protected LocationListener locationListener;
    protected Context context;
    private General general;
    public MapView mapView;
    public GoogleMap map;
    LatLng latLng;
    Location mLastLocation;
    Marker myLocationMarker;
    List visibleUsers;
    // Handler to update users on the map
    private Handler usersHandler = new Handler();
    private List<Marker> userMarkers;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_sound_finder, container, false);

        general = MenuActivity.general;
        userMarkers = new ArrayList<Marker>();

        // SQLite database handler
        db = new SQLiteHandler(getActivity());


        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);


        mapView.onResume(); // needed to get the map to display immediately



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


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mapViewMap) {
                general.log("MAP","map ready");
                map = mapViewMap;



                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    general.showToast("włącz GPS!",getActivity());
                }
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
                    Double lat,lon;
                    try {
                        lat = location.getLatitude ();
                        lon = location.getLongitude ();

                        saveLocation(parseInt(db.getUserId()),lat,lon);

                        LatLng initialLatLng;
                        initialLatLng = new LatLng(lat, lon);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(initialLatLng);
                        markerOptions.title("Current Position");
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_location));
                        Object test;
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
                    general.showToast("brak lokalizacji", getActivity());
                    // zoom to default location
                    LatLng defaultLocation = new LatLng(52.2296756, 21.012228700000037);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(defaultLocation).zoom(10).build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

            }
        });

        updateUsersOnTheMap();

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
            Log.i("MAP","LAT: "+String.valueOf(lat));
            Log.i("MAP","LON: "+String.valueOf(lon));
            return new LatLng(lat, lon);
        }
        catch (NullPointerException e){
            Log.i("MAP","ERROR getLocation()");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap gMap) {


    }



    @Override
    public void onConnected(Bundle bundle) {
//        Toast.makeText(this,"onConnected",Toast.LENGTH_SHORT).show();
//        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        if (mLastLocation != null) {
//            //place marker at current position
//            //mGoogleMap.clear();
//            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLng);
//            markerOptions.title("Current Position");
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//            currLocationMarker = mGoogleMap.addMarker(markerOptions);
//        }
//
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(5000); //5 seconds
//        mLocationRequest.setFastestInterval(3000); //3 seconds
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter
//
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
        general.log("MAP","location finally OK");

        //place marker at current position
//        map.clear();
        if (myLocationMarker != null) {
            myLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        saveLocation(parseInt(db.getUserId()),location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
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
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
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
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Function to get users from db
     * */
    private void getVisibleUsers(final Integer currentUserId, final Double nwLatitude,final Double nwLongitude,final Double seLatitude,final Double seLongitude) {
        general.log("MAP","getVisibleUsers()");
        // Tag used to cancel the request
        String tag_string_req = "req_getvisibleusers";


        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETACTIVEUSERS+currentUserId+","+nwLatitude+","+nwLongitude+","+seLatitude+","+seLongitude, new Response.Listener<String>() {



            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // remove user markers
                    for(int i=0; i<userMarkers.size(); i++) {
                        userMarkers.get(i).remove();
                    }

                    userMarkers.clear();
                    if (!error) {


                        general.log("MAP","users list for map retrieved OK");
                        String usersMsg = jObj.getString("visibleUsers");
                        JSONArray activeUsersArray = new JSONArray(usersMsg);


                        for(int i=0; i<activeUsersArray.length(); i++){
                            JSONObject json_data = activeUsersArray.getJSONObject(i);

                            LatLng activeMarker = new LatLng(json_data.getDouble("Latitude"), json_data.getDouble("Longitude"));


                            Marker userMarker = map.addMarker(new MarkerOptions().position(activeMarker).title("Testowa lokalizacja").snippet("User: "+String.valueOf(json_data.getInt("userId"))).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_music)));
                            userMarkers.add(userMarker);

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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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


}
