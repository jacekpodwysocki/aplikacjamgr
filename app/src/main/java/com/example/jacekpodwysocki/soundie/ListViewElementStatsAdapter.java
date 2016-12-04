package com.example.jacekpodwysocki.soundie;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import static android.R.attr.x;

/**
 * Created by jacekpodwysocki on 17/08/2016.
 */

public class ListViewElementStatsAdapter extends BaseAdapter {

    Context context;
    List<ListViewStatsRowItem> ListViewStatsRowItem;
    Resources res;

    ListViewElementStatsAdapter(Context context, List<ListViewStatsRowItem> ListViewStatsRowItem) {
        this.context = context;
        this.ListViewStatsRowItem = ListViewStatsRowItem;
    }

    private class ViewHolder {
        TextView position;
        TextView name;
        TextView level;
        TextView stats;
        ImageView badge;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.element_stats_list_item, null);
            holder = new ViewHolder();

            holder.position = (TextView) convertView.findViewById(R.id.position);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.level = (TextView) convertView.findViewById(R.id.level);
            holder.stats = (TextView) convertView.findViewById(R.id.stats);
            holder.badge = (ImageView) convertView.findViewById(R.id.badge);

            ListViewStatsRowItem row_pos = ListViewStatsRowItem.get(position);
            // setting values
            holder.position.setText(row_pos.getPosition());
            holder.name.setText(row_pos.getName());
            holder.level.setText(row_pos.getLevel());
            holder.stats.setText(row_pos.getStats());

            Random rand = new Random();
            int imageNumberPostfix = rand.nextInt(4);

            res = context.getResources();
            int resID = res.getIdentifier("badge"+imageNumberPostfix, "drawable", context.getPackageName());
            System.out.println("random number: "+imageNumberPostfix);

            holder.badge.setImageResource(resID);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;

    }

    @Override
    public int getCount() {
        return ListViewStatsRowItem.size();
    }

    @Override
    public Object getItem(int position) {
        return ListViewStatsRowItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ListViewStatsRowItem.indexOf(getItem(position));
    }

}
