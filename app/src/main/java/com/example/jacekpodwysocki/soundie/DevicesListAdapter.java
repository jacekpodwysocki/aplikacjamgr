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

public class DevicesListAdapter extends BaseAdapter {

    Context context;
    List<RowItemDevices> rowItemDevices;

    DevicesListAdapter(Context context, List<RowItemDevices> rowItemDevices) {
        this.context = context;
        this.rowItemDevices = rowItemDevices;
    }

    private class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listdevice, null);
            holder = new ViewHolder();
            holder.deviceName = (TextView) convertView.findViewById(R.id.deviceName);
            holder.deviceAddress = (TextView) convertView.findViewById(R.id.deviceAddress);

            RowItemDevices row_pos = rowItemDevices.get(position);

            holder.deviceName.setText(row_pos.getDeviceName());
            holder.deviceAddress.setText(row_pos.getDeviceAddress());
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;

    }

    @Override
    public int getCount() {
        return rowItemDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItemDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItemDevices.indexOf(getItem(position));
    }

}
