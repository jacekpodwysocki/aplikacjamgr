package com.example.jacekpodwysocki.soundie;

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

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button loginBtn;
    private TextView registerBtn;
    private EditText loginEmail;
    private EditText loginPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;


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

        // Session manager
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            Toast.makeText(getApplicationContext(), "User jest zalogowany", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "User nie zalogowany", Toast.LENGTH_LONG).show();
        }

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

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
                    Toast.makeText(getApplicationContext(),
                            "Wpisz adres email oraz nazwę użytkownika", Toast.LENGTH_LONG)
                            .show();
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
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(getResources().getString(R.string.debugTag), "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
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
                // Posting parameters to login url
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

    public void bypassLogin(View v) {
        Button btnBypassLogin = (Button) v;

        Intent startIntent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(startIntent);
    }

    public void goToRegistration(View v) {
        TextView registerBtn = (TextView) v;

        Intent startIntent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(startIntent);
    }



}
