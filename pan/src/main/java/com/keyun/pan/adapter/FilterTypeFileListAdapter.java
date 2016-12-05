package com.keyun.pan.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.keyun.pan.R;
import com.keyun.pan.activity.ShareToRepoActivity;
import com.keyun.pan.data.FilterTypeFile;
import com.keyun.pan.data.Repository;
import com.keyun.pan.data.RespFile;
import com.keyun.pan.utils.MyUtils;

import java.util.List;

/**
 * Created by cunguoyao on 2016/9/21.
 */
public class FilterTypeFileListAdapter extends BaseAdapter{

    private Context myCon;
    private int type;
    private List<FilterTypeFile> mData;

    public FilterTypeFileListAdapter(Context con, int type, List<FilterTypeFile> mData){
        this.myCon = con;
        this.type = type;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public FilterTypeFile getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final FilterTypeFile item = getItem(position);
        final LayoutInflater mInflater = LayoutInflater.from(myCon);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_file, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.videoTitle = (TextView)convertView.findViewById(R.id.tv_file_name);
            viewHolder.videoPath = (TextView)convertView.findViewById(R.id.tv_file_msg);
            viewHolder.videoItem = (ImageView)convertView.findViewById(R.id.iv_file_icon);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.videoTitle.setText(item.title);
        viewHolder.videoPath.setText(item.path);
        if(type == 1) {
            viewHolder.videoItem.setImageResource(R.drawable.netdisk_category_document_normal);
        }else if(type == 2) {
            viewHolder.videoItem.setImageResource(R.drawable.netdisk_category_audio_normal);
        }else if(type == 3) {
            viewHolder.videoItem.setImageResource(R.drawable.netdisk_category_video_normal);
        }
        return convertView;
    }

    class ViewHolder {
        TextView videoTitle;
        TextView videoPath;
        ImageView videoItem;
    }

}