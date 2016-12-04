package com.example.jacekpodwysocki.soundie;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;



public class StatystykiFragment extends Fragment {

    private ListView ListViewYourStats;
    private ListView ListViewUsersStats;
    private List<ListViewStatsRowItem> rowItemsYourStats;
    private List<ListViewStatsRowItem> rowItemsUsersStats;
    private ListViewElementStatsAdapter adapter;
    private ListViewStatsRowItem itemsYourStats;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = inflater.getContext();

        View rootView = inflater
                .inflate(R.layout.fragment_statystyki, container, false);

        int totalHeight = 0;

        ListViewYourStats = (ListView) rootView.findViewById(R.id.yourStats);
        ListViewUsersStats = (ListView) rootView.findViewById(R.id.usersStats);

        rowItemsYourStats = new ArrayList<ListViewStatsRowItem>();
        rowItemsUsersStats = new ArrayList<ListViewStatsRowItem>();

        itemsYourStats = new ListViewStatsRowItem("126. ","Jacek Podwysocki","Sound Newbie","5/12",0);
        rowItemsYourStats.add(itemsYourStats);

        adapter = new ListViewElementStatsAdapter(context, rowItemsYourStats);
        ListViewYourStats.setAdapter(adapter);

        itemsYourStats = new ListViewStatsRowItem("1. ","Kasia Marczuk","Music Freak","12/12",0);
        rowItemsUsersStats.add(itemsYourStats);
        itemsYourStats = new ListViewStatsRowItem("2. ","Michał Sawczuk","Music Freak","12/12",0);
        rowItemsUsersStats.add(itemsYourStats);
        itemsYourStats = new ListViewStatsRowItem("3. ","Paweł Nowak","Music Master","11/12",0);
        rowItemsUsersStats.add(itemsYourStats);
        itemsYourStats = new ListViewStatsRowItem("4. ","Andrzej Sęk","Sound King","10/12",0);
        rowItemsUsersStats.add(itemsYourStats);
        itemsYourStats = new ListViewStatsRowItem("5. ","Marta Bujak","Sound King","10/12",0);
        rowItemsUsersStats.add(itemsYourStats);
        itemsYourStats = new ListViewStatsRowItem("6. ","Marcin Kowalski","DJ","9/12",0);
        rowItemsUsersStats.add(itemsYourStats);
        itemsYourStats = new ListViewStatsRowItem("7. ","Tomasz Niemczuk","Music Enjoyer","8/12",0);
        rowItemsUsersStats.add(itemsYourStats);
        itemsYourStats = new ListViewStatsRowItem("8. ","Mateusz Pawczuk","Music Enjoyer","8/12",0);
        rowItemsUsersStats.add(itemsYourStats);



        adapter = new ListViewElementStatsAdapter(context, rowItemsUsersStats);
        ListViewUsersStats.setAdapter(adapter);

        return rootView;
    }
}
