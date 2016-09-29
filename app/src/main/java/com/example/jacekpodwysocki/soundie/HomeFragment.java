package com.example.jacekpodwysocki.soundie;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    String[] listItems;
    String[] ListItemsValues;
    private ListView generalStats;
    private List<ListViewCounterRowItem> rowItems;
    private ListViewElementCounterAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context context = inflater.getContext();

        View rootView = inflater
                .inflate(R.layout.fragment_home, container, false);

        listItems = getResources().getStringArray(R.array.array2);
        ListItemsValues = getResources().getStringArray(R.array.array3);

        generalStats = (ListView) rootView.findViewById(R.id.generalStats);
        rowItems = new ArrayList<ListViewCounterRowItem>();


        for (int i = 0; i < listItems.length; i++) {
            ListViewCounterRowItem items = new ListViewCounterRowItem(listItems[i], ListItemsValues[i]);
            rowItems.add(items);
        }

        adapter = new ListViewElementCounterAdapter(context, rowItems);
        generalStats.setAdapter(adapter);

        return rootView;



    }
}
