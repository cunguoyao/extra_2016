package com.keyun.pan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.keyun.pan.R;
import com.keyun.pan.activity.ShareToUserActivity;
import com.keyun.pan.data.ShareToUser;

import java.util.List;

/**
 * Created by cunguoyao on 2016/8/22.
 */
public class ShareToUserAdapter extends BaseAdapter {

    private ShareToUserActivity activity;
    private List<ShareToUser> mData;
    private View.OnClickListener mOnClickListener;

    public ShareToUserAdapter(ShareToUserActivity activity, List<ShareToUser> mData, View.OnClickListener mOnClickListener) {
        this.activity = activity;
        this.mData = mData;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public ShareToUser getItem(int position) {
        return mData != null ? mData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.adapter_share_to_user_item, parent, false);
            holder = new ViewHolder();
            holder.useridText = (TextView) convertView.findViewById(R.id.user_id);
            holder.userNameText = (TextView) convertView.findViewById(R.id.user_name);
            holder.userGroupText = (TextView) convertView.findViewById(R.id.user_group);
            holder.deleteBtn = (Button) convertView.findViewById(R.id.user_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ShareToUser item = mData.get(position);
        holder.useridText.setText(item.getUserid());
        holder.userNameText.setText(item.getName());
        holder.userGroupText.setText(item.getGroup());
        holder.deleteBtn.setTag(item);
        holder.deleteBtn.setOnClickListener(mOnClickListener);
        return convertView;
    }

    class ViewHolder {
        TextView useridText;
        TextView userNameText;
        TextView userGroupText;
        Button deleteBtn;
    }
}
