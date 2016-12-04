package com.example.jacekpodwysocki.soundie;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jacekpodwysocki on 17/08/2016.
 * custom adapter for navigation list view
 */

public class MessagesListAdapter extends BaseAdapter {

    Context context;
    List<RowItemMessages> rowItemMessages;

    MessagesListAdapter(Context context, List<RowItemMessages> rowItemMessages) {
        this.context = context;
        this.rowItemMessages = rowItemMessages;
    }

    private class ViewHolder {
        TextView messageAuthor;
        TextView messageContent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listmessages, null);
            holder = new ViewHolder();
            holder.messageAuthor = (TextView) convertView.findViewById(R.id.messageAuthor);
            holder.messageContent = (TextView) convertView.findViewById(R.id.messageContent);

            RowItemMessages row_pos = rowItemMessages.get(position);

            holder.messageAuthor.setText(row_pos.getMessageAuthor());
            holder.messageContent.setText(row_pos.getMessageContent());
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;

    }

    @Override
    public int getCount() {
        return rowItemMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItemMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItemMessages.indexOf(getItem(position));
    }

}
