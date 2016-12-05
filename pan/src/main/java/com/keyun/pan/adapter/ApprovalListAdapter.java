package com.keyun.pan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.keyun.pan.R;
import com.keyun.pan.data.Approval;

import java.util.List;

/**
 * Created by cunguoyao on 2016/6/3.
 */
public class ApprovalListAdapter extends BaseAdapter {

    private Context context;
    private List<Approval> fileItemList;

    public ApprovalListAdapter(Context context, List<Approval> fileItemList) {
        this.context = context;
        this.fileItemList = fileItemList;
    }

    @Override
    public int getCount() {
        return fileItemList == null ? 0 : fileItemList.size();
    }

    @Override
    public Approval getItem(int position) {
        return fileItemList == null ? null : fileItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        final LayoutInflater mInflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_approval, parent, false);
            holder = new ViewHolder();
            holder.ivStatusIcon = (ImageView) convertView.findViewById(R.id.iv_status_icon);
            holder.tvApprovalTitle = (TextView) convertView.findViewById(R.id.tv_approval_title);
            holder.tvApprovalRuleModel = (TextView) convertView.findViewById(R.id.tv_approval_rule_model);
            holder.tvApprovalRequestUsername = (TextView) convertView.findViewById(R.id.tv_request_username);
            holder.tvApprovalRequestTime = (TextView) convertView.findViewById(R.id.tv_request_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Approval item = fileItemList.get(position);
        if(item.getApprovalStatus() == -1) {
            holder.ivStatusIcon.setImageResource(R.drawable.wfjj);
        }else if(item.getApprovalStatus() == 0) {
            holder.ivStatusIcon.setImageResource(R.drawable.wfspz);
        }else if(item.getApprovalStatus() == 1) {
            holder.ivStatusIcon.setImageResource(R.drawable.wfty);
        }else {
            holder.ivStatusIcon.setImageResource(R.drawable.wf);
        }
        if(item.getRuleModel() == 0) {
            holder.tvApprovalRuleModel.setText("提交方式：" + "在线提交");
        }else if(item.getRuleModel() == 1) {
            holder.tvApprovalRuleModel.setText("提交方式：" + "离线提交");
        }else if(item.getRuleModel() == 2) {
            holder.tvApprovalRuleModel.setText("提交方式：" + "微信提交");
        }
        holder.tvApprovalTitle.setText(item.getRequestTitle());
        holder.tvApprovalRequestUsername.setText("申请人：" + item.getUserName());
        holder.tvApprovalRequestTime.setText("申请时间" + item.getRequestTime());
        return convertView;
    }

    private class ViewHolder {
        ImageView ivStatusIcon;
        TextView tvApprovalTitle;
        TextView tvApprovalRuleModel;
        TextView tvApprovalRequestUsername;
        TextView tvApprovalRequestTime;
    }

}
