package com.keyun.pan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.keyun.pan.R;
import com.keyun.pan.activity.ShareToRepoFileActivity;
import com.keyun.pan.data.RepositoryFile;
import com.keyun.pan.data.RespFile;

import java.util.List;

/**
 * Created by cunguoyao on 2016/7/10.
 */
public class ShareRepositoryFileAdapter extends BaseAdapter {

    private Context context;
    private List<RepositoryFile> fileItemList;

    public ShareRepositoryFileAdapter(Context context, List<RepositoryFile> fileItemList) {
        this.context = context;
        this.fileItemList = fileItemList;
    }

    @Override
    public int getCount() {
        return fileItemList == null ? 0 : fileItemList.size();
    }

    @Override
    public RepositoryFile getItem(int position) {
        return fileItemList == null ? null : fileItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        RespViewHolder respViewHolder;
        final RepositoryFile repository = getItem(position);
        final LayoutInflater mInflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_share_repository_file, parent, false);
            respViewHolder = new RespViewHolder();
            respViewHolder.iv_fileIcon = (ImageView) convertView.findViewById(R.id.iv_file_icon);
            respViewHolder.tv_fileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            respViewHolder.ibtn_fileOperate = (CheckBox) convertView.findViewById(R.id.ibtn_file_operate);
            convertView.setTag(respViewHolder);
        }else {
            respViewHolder = (RespViewHolder) convertView.getTag();
        }
        respViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_zlk);
        respViewHolder.tv_fileName.setText(repository.getName());
        respViewHolder.ibtn_fileOperate.setVisibility(View.VISIBLE);
        if(repository.isChecked()) {
            respViewHolder.ibtn_fileOperate.setChecked(true);
        }else {
            respViewHolder.ibtn_fileOperate.setChecked(false);
        }
        respViewHolder.ibtn_fileOperate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((ShareToRepoFileActivity)context).check(repository);
            }
        });
        return convertView;
    }

    private class ViewHolder {

    }

    private class RespViewHolder extends ViewHolder {
        ImageView iv_fileIcon;
        TextView tv_fileName;
        CheckBox ibtn_fileOperate;
    }

}
