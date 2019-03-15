package com.example.jacekpodwysocki.soundie;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.location.Address;
import android.os.Bundle;
//import android.support.v4.app.ActionBarDrawerToggle;
import android.os.Handler;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.media.MediaPlayer;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.jacekpodwysocki.soundie.AppConfig.fileTransferCheckInterval;

import static com.example.jacekpodwysocki.soundie.SoundFinderFragment.volleyRequestsTagSF;
import static java.lang.Integer.parseInt;

public class MenuActivity extends AppCompatActivity {


    /* SIDE MENU */
    String[] menutitles;
    String[] fragmenttitles;
    TypedArray menuIcons;

    // nav drawer title
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private static Activity activity;
    private Integer requestsCount;
    private Boolean dialogRequestConfirmation;
    private String requestingUserName;
    private String requestingUserId;
    private String requestId;
    private Integer userIdTemp;

    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerContainer;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private List<RowItem> rowItems;
    private CustomAdapter adapter;
    /* SIDE MENU */
    private SQLiteHandler db;
    private TextView profileHeadline;
    private SessionManager session;
    public static MediaPlayer mediaPlayer;
    public static General general;
    public static File file;

    private final Handler fileRequestHandler = new Handler();
    private Runnable runn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        requestsCount = 0;
        dialogRequestConfirmation = false;
        mediaPlayer = new MediaPlayer();
        general = new General(this);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        profileHeadline = (TextView) findViewById(R.id.profileHeadline);

        String name = user.get("FirstName") + " " + user.get("LastName") + " ("+parseInt(db.getUserId())+")";

        // Displaying the user details on the screen
        profileHeadline.setText(name);

        mTitle = mDrawerTitle = getTitle();

        menutitles = getResources().getStringArray(R.array.main_menu_items_array);
        fragmenttitles = getResources().getStringArray(R.array.fragments_array);
        menuIcons = getResources().obtainTypedArray(R.array.sliding_drawer_icons_array);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerContainer = (RelativeLayout) findViewById(R.id.slider_list_container);
        mDrawerList = (ListView) findViewById(R.id.slider_list);

        rowItems = new ArrayList<RowItem>();

        for (int i = 0; i < menutitles.length; i++) {
            RowItem items = new RowItem(menutitles[i], menuIcons.getResourceId(
                    i, -1));
            rowItems.add(items);
        }


        menuIcons.recycle();

        adapter = new CustomAdapter(getApplicationContext(), rowItems);

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new MenuActivity.SlideitemListener());

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        String fragmentParamString;

        try {
            fragmentParamString = getIntent().getExtras().getString("fragmentParam");
        } catch (NullPointerException e ) {
            fragmentParamString = "";
        }

        if(fragmentParamString != null && fragmentParamString.equals("player")){
            updateDisplay(6);
        }else if (fragmentParamString != null && fragmentParamString.equals("mymusic")) {
            updateDisplay(2);
        }else if (savedInstanceState == null) {
            // on first time display view for first nav item
            updateDisplay(0);
        }

        fileRequestHandler.postDelayed(runn = new Runnable() {
            public void run() {
                checkForFileRequests();
                fileRequestHandler.postDelayed(this,AppConfig.fileTransferCheckInterval);
            }
        }, AppConfig.fileTransferCheckInterval);


    }

    private void logoutUser() {
        session = new SessionManager(getApplicationContext());
        session.setLogin(false);
        db.deleteUsers();

        // Launching the Login (MainActivity) activity
        Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(startIntent);
        //finish();
    }

    class SlideitemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if(position != 5) {
                updateDisplay(position);
            }else if(position == 5){ // pozycja 5 "Wyloguj"
                logoutUser();
            }
        }

    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        mExpandableListView = (mExpandableListView) findViewById(R.id.mExpandableListView);
//        super.onWindowFocusChanged(hasFocus);
//        mExpandableListView.setIndicatorBounds(mExpandableListView.getRight()- 40, mExpandableListView.getWidth());
//    }

    private void updateDisplay(int position) {
        Fragment fragment = null;
        Integer fragmentsPosition = 0;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new SoundFinderFragment();
                break;
            case 2:
                fragment = new MojaMuzykaFragment();
                break;
            case 3:
                fragment = new WiadomosciFragment();
                break;
            case 4:
                fragment = new StatystykiFragment();
                break;
            case 5:
                fragment = new WylogujFragment();
                break;
            case 6:
                fragmentsPosition = 0;
                fragment = new PlayerFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).addToBackStack("").commit();

            // update selected item and title, then close the drawer
            if(position < 6) {
                setTitle(menutitles[position]);
            }else {
                setTitle(fragmenttitles[fragmentsPosition]);
            }
            mDrawerLayout.closeDrawer(mDrawerContainer);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

    }


    /* functions for side menu */
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerContainer);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public String getTrackPathToBePlayed(){
        String songPath = getIntent().getExtras().getString("songPath");

        return songPath;
    }

    public String getTrackInfoToBePlayed() {
        String songTitle = getIntent().getExtras().getString("songTitle");
        String songArtist = getIntent().getExtras().getString("songArtist");
        if(songTitle != null && songArtist != null) {
            return songTitle + " - " + songArtist;
        }else{
            return "Tytuł utworu - artysta";
        }
    }

    public void checkForFileRequests(){
        if(db.getUserId().equals("")){
            userIdTemp = 0;
        }else{
            userIdTemp = Integer.valueOf(db.getUserId());
        }
        getFileRequestsCount(Integer.valueOf(userIdTemp));

        if(requestsCount > 0 && dialogRequestConfirmation != true){ // show dialog only if user does not have one already
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Transfer");
            alert.setMessage("Użytkownik "+requestingUserName+" chce pobrać od Ciebie utwór, \nczy wyrażasz zgodę?");
            alert.setPositiveButton("Tak", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    general.log("Menu Activity","Clicked YES");
                    updateRequestStatus(Integer.valueOf(db.getUserId()),1);
                    dialog.dismiss();
                    dialogRequestConfirmation = false;
                    requestsCount = 0;
                    // go to tranfer activity
                    Intent intent = new Intent(getApplicationContext(), TransferActivity.class);
                    Bundle b = new Bundle();
                    b.putString("transferType", "Wysyłanie");
                    b.putInt("requestId", Integer.valueOf(requestId));
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            });

            alert.setNegativeButton("Nie", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    general.log("Menu Activity","Clicked NO");
                    updateRequestStatus(Integer.valueOf(db.getUserId()),2);
                    dialog.dismiss();
                    dialogRequestConfirmation = false;
                    requestsCount = 0;
                }
            });

            alert.show();
            dialogRequestConfirmation = true;
        }else{
            // no file requests or info window up
        }

    }

    private void getFileRequestsCount(final Integer userId){
        // Tag used to cancel the request
        String tag_string_req = "req_getfilerequestscount";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETFILEREQUESTSCOUNT+userId, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String responseMsg = jObj.getString("fileRequestsCount");
                        requestsCount = Integer.valueOf(responseMsg);
                        if(requestsCount > 0){
                            getFileRequestDetails(userId);
                        }
                        //handleFileRequestResponse(status);
                    } else {

                        String errorMsg = jObj.getString("message");
                        general.log("MAP","ERROR getting file request count: "+errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().toLowerCase().contains("network is unreachable")) {
                    general.showToast("Brak połączenia z internetem!\nWłącz sieć, aby móc korzystać z aplikacji", getApplicationContext());
                }else{
                    general.showToast(error.getMessage(), getApplicationContext());
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
     * Function to update request status in DB
     * requestId - id of the request
     * status - 0:open, 1:accepted, 2:denied, 3:timeout
     * */
    private void updateRequestStatus(final Integer requestedUser, final Integer status) {
        // Tag used to cancel the request
        String tag_string_req = "req_updaterequeststatus";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATEREQUESTSTATUS, new Response.Listener<String>() {

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
                        general.log("Menu Activity","ERROR updating request status: "+errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getMessage().toLowerCase().contains("network is unreachable")) {
                    general.showToast("Brak połączenia z internetem!\nWłącz sieć, aby móc korzystać z aplikacji", getApplicationContext());
                }else{
                    general.showToast(error.getMessage(), getApplicationContext());
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
                params.put("RequestedUser", String.valueOf(requestedUser));
                params.put("status", String.valueOf(status));

                return params;
            }

        };

        strReq.setTag(volleyRequestsTagSF);

        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(strReq, volleyRequestsTagSF);
    }

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

                            requestingUserName = json_data.getString("FirstName") + " " + json_data.getString("LastName");

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
                    general.showToast("Brak połączenia z internetem!\nWłącz sieć, aby móc korzystać z aplikacji", getApplicationContext());
                }else{
                    general.showToast(error.getMessage(), getApplicationContext());
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
     * Function to get selected user from db
     * parameter: userId
     * */
    private void getFileRequestDetails(final Integer userId) {
        // Tag used to cancel the request
        String tag_string_req = "req_getuserdetails";


        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_GETFILEREQUESTDETAILS+userId, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        String requestMsg = jObj.getString("requestDetails");

                        JSONArray requestDetailsArray = new JSONArray(requestMsg);


                        for(int i=0; i<requestDetailsArray.length(); i++){

                            JSONObject json_data = requestDetailsArray.getJSONObject(i);

                            requestingUserId = json_data.getString("RequestingUser");
                            requestId = json_data.getString("Id");

                        }

                        getUserDetails(Integer.valueOf(requestingUserId));

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
                    general.showToast("Brak połączenia z internetem!\nWłącz sieć, aby móc korzystać z aplikacji", getApplicationContext());
                }else{
                    general.showToast(error.getMessage(), getApplicationContext());
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
}
