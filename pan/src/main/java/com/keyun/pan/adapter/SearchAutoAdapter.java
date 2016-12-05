package com.keyun.pan.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.keyun.pan.R;

/**
 * Created by cunguoyao on 2016/8/18.
 */

public class SearchAutoAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mData;// 过滤后的item
    private OnClickListener mOnClickListener;

    public SearchAutoAdapter(Context context, List<String> mData,
                             OnClickListener onClickListener) {
        this.mContext = context;
        this.mData = mData;
        this.mOnClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        Log.i("cyl", "getCount");
        return null == mData ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null == mData ? 0 : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AutoHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_search_history_item, parent, false);
            holder = new AutoHolder();
            holder.deleteBtn = (TextView) convertView.findViewById(R.id.auto_delete);
            holder.content = (TextView) convertView.findViewById(R.id.auto_content);
            holder.autoImage = (TextView) convertView.findViewById(R.id.auto_image);
            convertView.setTag(holder);
        } else {
            holder = (AutoHolder) convertView.getTag();
        }

        String data = mData.get(position);
        holder.content.setText(data);
        holder.deleteBtn.setTag(data);
        holder.deleteBtn.setOnClickListener(mOnClickListener);
        return convertView;
    }

    private class AutoHolder {
        TextView autoImage;
        TextView content;
        TextView deleteBtn;

    }
}

