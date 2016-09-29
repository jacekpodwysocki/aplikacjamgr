package com.example.jacekpodwysocki.soundie;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jacekpodwysocki.soundie.SQLiteHandler;
import com.example.jacekpodwysocki.soundie.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.jacekpodwysocki.soundie.R.id.map;

public class WylogujFragment extends Fragment {
    private SessionManager session;
    private SQLiteHandler db;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_wyloguj, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        logoutUser();
    }

    private void logoutUser() {

        session = new SessionManager(getActivity());
        session.setLogin(false);
        //db.deleteUsers();

        // Launching the Login (MainActivity) activity
        Intent startIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(startIntent);
        //finish();
    }
}
