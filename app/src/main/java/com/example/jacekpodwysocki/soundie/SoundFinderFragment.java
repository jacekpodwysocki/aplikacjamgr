package com.example.jacekpodwysocki.soundie;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.R.attr.fragment;


public class SoundFinderFragment extends Fragment {
    MapView mapView;
    GoogleMap map;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        View rootView = inflater
                .inflate(R.layout.fragment_sound_finder, container, false);


        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
//        map = mapView.getMapAsync(rootView);
        Log.i(getResources().getString(R.string.debugTag), "create sound finder fragment");
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        return rootView;
    }

    @Override
    public void onResume() {
        Log.i(getResources().getString(R.string.debugTag), "onResume inside fragment");

        super.onResume();
        mapView.onResume();
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(52.207748, 21.014918);
        map.addMarker(new MarkerOptions().position(sydney).title("Warszawa"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


}
