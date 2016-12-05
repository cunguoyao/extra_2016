package com.keyun.pan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.keyun.pan.R;
import com.keyun.pan.activity.ShareToUserActivity;
import com.keyun.pan.data.ShareToUser;

import java.util.List;

/**
 * Created by cunguoyao on 2016/8/22.
 */
public class ShareToUserSelectAdapter extends BaseAdapter {

    private ShareToUserActivity activity;
    private List<ShareToUser> mData;
    private View.OnClickListener mOnClickListener;

    public ShareToUserSelectAdapter(ShareToUserActivity activity, List<ShareToUser> mData, View.OnClickListener mOnClickListener) {
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
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.adapter_select_share_to_user_item, parent, false);
            holder = new ViewHolder();
            holder.useridText = (TextView) convertView.findViewById(R.id.user_id);
            holder.userNameText = (TextView) convertView.findViewById(R.id.user_name);
            holder.userGroupText = (TextView) convertView.findViewById(R.id.user_group);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.user_check);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ShareToUser item = mData.get(position);
        holder.useridText.setText(item.getUserid());
        holder.userNameText.setText(item.getName());
        holder.userGroupText.setText(item.getGroup());
        if(item.isChecked()) {
            holder.checkbox.setChecked(true);
        }else {
            holder.checkbox.setChecked(false);
        }
        holder.checkbox.setTag(item);
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    holder.checkbox.setChecked(true);
                }else {
                    holder.checkbox.setChecked(false);
                }
                item.setChecked(isChecked);
                activity.mChecked(item);
            }
        });
        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.isChecked()) {
                    item.setChecked(false);
                    holder.checkbox.setChecked(false);
                }else {
                    item.setChecked(true);
                    holder.checkbox.setChecked(true);
                }
                activity.mChecked(item);
            }
        });*/
        return convertView;
    }

    class ViewHolder {
        TextView useridText;
        TextView userNameText;
        TextView userGroupText;
        CheckBox checkbox;
    }
}
