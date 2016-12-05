package com.keyun.pan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.keyun.pan.R;
import com.keyun.pan.data.PushMessage;

import java.util.List;

/**
 * Created by cunguoyao on 2016/11/8.
 */
public class PushMessageAdapter extends BaseAdapter {

    private Context context;
    private List<PushMessage> mData;

    public PushMessageAdapter(Context context, List<PushMessage> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public PushMessage getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final PushMessage item = getItem(position);
        final LayoutInflater mInflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_message, parent, false);
            holder = new ViewHolder();
            holder.contentText = (TextView)convertView.findViewById(R.id.message_content);
            holder.timeText = (TextView)convertView.findViewById(R.id.message_time);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.contentText.setText(item.getTitle());
        holder.timeText.setText(item.getTime());
        return convertView;
    }

    class ViewHolder {
        TextView contentText;
        TextView timeText;
    }

}
