package com.example.jacekpodwysocki.soundie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.example.jacekpodwysocki.soundie.SQLiteHandler;
import com.example.jacekpodwysocki.soundie.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.R.attr.name;
import static com.example.jacekpodwysocki.soundie.R.id.registerBtn;
import static com.example.jacekpodwysocki.soundie.R.id.registrationEmail;
import static com.example.jacekpodwysocki.soundie.R.id.registrationFirstName;
import static com.example.jacekpodwysocki.soundie.R.id.registrationLastName;

public class RegistrationActivity extends AppCompatActivity {
    private Button registerBtn;
    private EditText registrationFirstName;
    private EditText registrationLastName;
    private EditText registrationEmail;
    private EditText registrationPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registrationFirstName = (EditText) findViewById(R.id.registrationFirstName);
        registrationLastName = (EditText) findViewById(R.id.registrationLastName);
        registrationEmail = (EditText) findViewById(R.id.registrationEmail);
        registrationPassword = (EditText) findViewById(R.id.registrationPassword);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(getApplicationContext(),
                    MenuActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        registerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String firstName = registrationFirstName.getText().toString().trim();
                String lastName = registrationLastName.getText().toString().trim();
                String email = registrationEmail.getText().toString().trim();
                String password = registrationPassword.getText().toString().trim();

                if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(firstName, lastName, email, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Uzupełnij wszystkie pola!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String firstName, final String lastName, final String email,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("sss", "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String Id = jObj.getString("Id");
                        String FirstName = jObj.getString("FirstName");
                        String LastName = jObj.getString("LastName");
                        String Email = jObj.getString("Email");


                        // Inserting row in users table
                        db.addUser(Id, FirstName,LastName, Email);

                        Toast.makeText(getApplicationContext(), "Zarejestrowano pomyślnie, możesz się zalogować", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(getApplicationContext(),
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ssss", "Registration Error: " + error.getMessage());
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
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstName", firstName);
                params.put("lastName", lastName);
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

    public void goToLogin(View v) {
        TextView btnLogin = (TextView) v;

        Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(startIntent);
    }
}
