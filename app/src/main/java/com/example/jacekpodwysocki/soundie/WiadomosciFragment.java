package com.example.jacekpodwysocki.soundie;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class WiadomosciFragment extends Fragment {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private LinearLayout contentOne;
    private LinearLayout contentTwo;

    private MessagesListAdapter adapter;
    private List<RowItemMessages> rowItemsReceived;
    private List<RowItemMessages> rowItemsSent;
    private ListView receivedMessagesListView;
    private ListView sentMessagesListView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_wiadomosci, container, false);

        initTabs(rootView);
        populateReceivedMessagesTab(rootView);
        populateSentMessagesTab(rootView);

        return rootView;
    }

    private void initTabs(View rootView) {


        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        contentOne = (LinearLayout) rootView.findViewById(R.id.tab_1);
        contentTwo = (LinearLayout) rootView.findViewById(R.id.tab_2);

        tabLayout.addTab(tabLayout.newTab().setText("Odebrane").setIcon(R.drawable.icon_messages_in));
        tabLayout.addTab(tabLayout.newTab().setText("Wysłane").setIcon(R.drawable.icon_messages_out));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                System.out.println("tab selected: "+ tab.getPosition());
                if(tab.getPosition() == 0){
                    contentOne.setVisibility(View.VISIBLE);
                    contentTwo.setVisibility(View.GONE);
                }else if(tab.getPosition() == 1){
                    contentTwo.setVisibility(View.VISIBLE);
                    contentOne.setVisibility(View.GONE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                doSomething();
            }
        });
    }

    private void populateReceivedMessagesTab(View rootView){
        receivedMessagesListView = (ListView) rootView.findViewById(R.id.receivedMessages);

        // trim text
        // s = s.substring(0, Math.min(s.length(), 10));

        rowItemsReceived = new ArrayList<RowItemMessages>();
        RowItemMessages items = new RowItemMessages("Andrzej Malinowski","Dzięki za wiadomość, kawałek naprawdę dobry...");
        RowItemMessages items1 = new RowItemMessages("Paweł Skocz","No właśnie ostatnio to sprawdzałem i wynika z tego...");
        RowItemMessages items2 = new RowItemMessages("Paweł Skocz","Pewnie 34, ale może więcej");
        RowItemMessages items3 = new RowItemMessages("Marta Stachurska","Spoko zestaw, polecam swój profil");
        RowItemMessages items4 = new RowItemMessages("Filip Sokołowski","Queen, Nirvana, Green Day, The Cranberries...");
        RowItemMessages items5 = new RowItemMessages("Paweł Nowak","Powiedzmy, że to alternatywny rock");
        rowItemsReceived.add(items);
        rowItemsReceived.add(items1);
        rowItemsReceived.add(items2);
        rowItemsReceived.add(items3);
        rowItemsReceived.add(items4);
        rowItemsReceived.add(items5);
        adapter = new MessagesListAdapter(rootView.getContext(), rowItemsReceived);
        receivedMessagesListView.setAdapter(adapter);
    }

    private void populateSentMessagesTab(View rootView){
        sentMessagesListView = (ListView) rootView.findViewById(R.id.sentMessages);

        rowItemsSent = new ArrayList<RowItemMessages>();
        RowItemMessages items = new RowItemMessages("Marta Stachurska","No jasne, że tak. Poza tym mam jeszcze 2 nowe...");
        RowItemMessages items1 = new RowItemMessages("Anna Pawłowicz","Pobrałem wczoraj");
        RowItemMessages items2 = new RowItemMessages("Michał Nowak","No okej, ale czy to jest na pewnonajnowszy album...");
        RowItemMessages items3 = new RowItemMessages("Mariusz Sękowski","Zawiesila mi się, wyślij jeszcze raz");
        RowItemMessages items4 = new RowItemMessages("Michał Nowak","To był fragment wersji studyjnej");
        RowItemMessages items5 = new RowItemMessages("Andrzej Malinowski","Nie wiem jak się nazywa ale na pewno jest...");
        rowItemsSent.add(items);
        rowItemsSent.add(items1);
        rowItemsSent.add(items2);
        rowItemsSent.add(items3);
        rowItemsSent.add(items4);
        rowItemsSent.add(items5);
        adapter = new MessagesListAdapter(rootView.getContext(), rowItemsSent);
        sentMessagesListView.setAdapter(adapter);
    }


}
