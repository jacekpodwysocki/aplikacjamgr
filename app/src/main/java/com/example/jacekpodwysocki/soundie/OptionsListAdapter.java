package com.example.jacekpodwysocki.soundie;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jacekpodwysocki on 17/08/2016.
 * custom adapter for navigation list view
 */

public class OptionsListAdapter extends BaseAdapter {

    Context context;
    List<RowItemOptions> rowItemOptions;

    OptionsListAdapter(Context context, List<RowItemOptions> rowItemOptions) {
        this.context = context;
        this.rowItemOptions = rowItemOptions;
    }

    private class ViewHolder {
        TextView songTitle;
        TextView songArtist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listwithoptions, null);
            holder = new ViewHolder();
            holder.songTitle = (TextView) convertView.findViewById(R.id.songTitle);
            holder.songArtist = (TextView) convertView.findViewById(R.id.songArtist);

            RowItemOptions row_pos = rowItemOptions.get(position);

            holder.songTitle.setText(row_pos.getSongTitle());
            holder.songArtist.setText(row_pos.getSongArtist());
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;

    }

    @Override
    public int getCount() {
        return rowItemOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItemOptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItemOptions.indexOf(getItem(position));
    }

}
