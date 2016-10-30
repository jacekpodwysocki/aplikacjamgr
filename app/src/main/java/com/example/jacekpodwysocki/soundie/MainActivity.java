package com.example.jacekpodwysocki.soundie;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.fragment;
import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button loginBtn;
    private TextView registerBtn;
    private EditText loginEmail;
    private EditText loginPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private General general;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (TextView) findViewById(R.id.registerBtn);
        loginEmail = (EditText) findViewById(R.id.registrationFirstName);
        loginPassword = (EditText) findViewById(R.id.registrationPassword);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // custom Toast
        general = new General();


        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(intent);
//            //finish();
        }

        // Login button Click Event

        loginBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    general.showToast("Wpisz adres email oraz nazwę użytkownika",getApplicationContext());

                }
            }

        });

    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logowanie ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(getResources().getString(R.string.debugTag), "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String Id = jObj.getString("Id");

                        String FirstName = jObj.getString("FirstName");
                        String LastName = jObj.getString("LastName");
                        String Email = jObj.getString("Email");


                        // Inserting row in users table
                        db.addUser(Id, FirstName,LastName, Email);

                        // Launch main activity
                        Intent startIntent = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(startIntent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");

                        general.showToast(errorMsg,getApplicationContext());
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    general.showToast("Json error: " + e.getMessage(),getApplicationContext());
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
                hideDialog();
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
                // Posting parameters tozn url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public void goToRegistration(View v) {
        TextView registerBtn = (TextView) v;

        Intent startIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(startIntent);
    }

    public void goToPlayer(View v){
        // pass parameter
        Intent startIntent = new Intent(getApplicationContext(), MenuActivity.class);
        startIntent.putExtra("fragmentParam", "player");
        startActivity(startIntent);

    }
    public void bypassLogin(View v){
        Button btnBypassLogin = (Button) v;

        Intent startIntent = new Intent(getApplicationContext(),MenuActivity.class);
        startActivity(startIntent);
    }

}

