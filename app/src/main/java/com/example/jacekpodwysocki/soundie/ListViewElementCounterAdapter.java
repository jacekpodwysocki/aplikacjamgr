package com.example.jacekpodwysocki.soundie;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jacekpodwysocki on 17/08/2016.
 */

public class ListViewElementCounterAdapter extends BaseAdapter {

    Context context;
    List<ListViewCounterRowItem> ListViewCounterRowItem;

    ListViewElementCounterAdapter(Context context, List<ListViewCounterRowItem> ListViewCounterRowItem) {
        this.context = context;
        this.ListViewCounterRowItem = ListViewCounterRowItem;
    }

    private class ViewHolder {
        TextView text;
        TextView value;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.element_counter_list_item, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.value = (TextView) convertView.findViewById(R.id.value);

            ListViewCounterRowItem row_pos = ListViewCounterRowItem.get(position);
            // setting values
            holder.text.setText(row_pos.getText());
            holder.value.setText(row_pos.getValue());
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;

    }

    @Override
    public int getCount() {
        return ListViewCounterRowItem.size();
    }

    @Override
    public Object getItem(int position) {
        return ListViewCounterRowItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ListViewCounterRowItem.indexOf(getItem(position));
    }

}
