package com.keyun.pan.adapter;

import android.app.Service;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.keyun.pan.R;
import com.keyun.pan.data.FileItem;
import com.keyun.pan.data.Repository;
import com.keyun.pan.data.RespFile;
import com.keyun.pan.fragment.BaseFragment;
import com.keyun.pan.fragment.GroupPanFragment;
import com.keyun.pan.utils.MyUtils;

import java.util.List;

/**
 * Created by cunguoyao on 2016/7/10.
 */
public class RepositoryAdapter extends BaseAdapter {

    private static final int TYPE_REPOSITORY = 0;
    private static final int TYPE_FILE = 1;

    private BaseFragment context;
    private List<RespFile> fileItemList;
    private Vibrator vibrator;
    private PopupWindow pop;

    public RepositoryAdapter(BaseFragment context, List<RespFile> fileItemList) {
        this.context = context;
        this.fileItemList = fileItemList;
        this.vibrator = (Vibrator)context.getActivity().getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    public int getCount() {
        return fileItemList == null ? 0 : fileItemList.size();
    }

    @Override
    public RespFile getItem(int position) {
        return fileItemList == null ? null : fileItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        RespViewHolder respViewHolder = null;
        FileViewHolder fileViewHolder = null;
        int type = getItemViewType(position);
        final RespFile respFile = getItem(position);
        final LayoutInflater mInflater = LayoutInflater.from(context.getActivity());
        if (convertView == null) {
            switch (type) {
                case TYPE_REPOSITORY:
                    convertView = mInflater.inflate(R.layout.item_repository, parent, false);
                    respViewHolder = new RespViewHolder();
                    respViewHolder.iv_fileIcon = (ImageView) convertView.findViewById(R.id.iv_file_icon);
                    respViewHolder.tv_fileName = (TextView) convertView.findViewById(R.id.tv_file_name);
                    respViewHolder.tv_fileSpace = (TextView) convertView.findViewById(R.id.tv_file_space);
                    respViewHolder.tv_fileMsg = (TextView) convertView.findViewById(R.id.tv_file_msg);
                    respViewHolder.ibtn_fileOperate = (CheckBox) convertView.findViewById(R.id.ibtn_file_operate);
                    convertView.setTag(respViewHolder);
                    break;
                case TYPE_FILE:
                    convertView = mInflater.inflate(R.layout.item_file, parent, false);
                    fileViewHolder = new FileViewHolder();
                    fileViewHolder.iv_fileIcon = (ImageView) convertView.findViewById(R.id.iv_file_icon);
                    fileViewHolder.tv_fileName = (TextView) convertView.findViewById(R.id.tv_file_name);
                    fileViewHolder.tv_fileMsg = (TextView) convertView.findViewById(R.id.tv_file_msg);
                    fileViewHolder.ibtn_fileOperate = (CheckBox) convertView.findViewById(R.id.ibtn_file_operate);
                    convertView.setTag(fileViewHolder);
                    break;
            }
        }else {
            switch (type) {
                case TYPE_REPOSITORY:
                    respViewHolder = (RespViewHolder) convertView.getTag();
                    break;
                case TYPE_FILE:
                    fileViewHolder = (FileViewHolder) convertView.getTag();
                    break;
            }
        }
        switch (type) {
            case TYPE_REPOSITORY:
                final Repository repository = (Repository)respFile;
                respViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_zlk);
                respViewHolder.tv_fileName.setText(repository.getName());
                respViewHolder.tv_fileSpace.setText("容量：" + MyUtils.convertFileSize(repository.getUsedSpace()) + "/" + MyUtils.convertFileSize(repository.getMaxSpace()));
                respViewHolder.tv_fileMsg.setText("" + repository.getCreateTime());
                if(repository.isCheckboxVisualable()) {
                    respViewHolder.ibtn_fileOperate.setVisibility(View.VISIBLE);
                    if(repository.isChecked()) {
                        respViewHolder.ibtn_fileOperate.setChecked(true);
                    }else {
                        respViewHolder.ibtn_fileOperate.setChecked(false);
                    }
                }else {
                    respViewHolder.ibtn_fileOperate.setVisibility(View.INVISIBLE);
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((GroupPanFragment)context).listViewOnReposItemClick(repository);
                    }
                });
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        vibrator.vibrate(new long[]{0,20}, -1);
                        checkReposItem(repository);
                        notifyDataSetChanged();
                        ((GroupPanFragment)context).popWhenSelectRepo(mInflater, parent, repository);
                        return false;
                    }
                });
                break;
            case TYPE_FILE:
                final FileItem item = (FileItem)respFile;
                if(item.getIsDir() == 0) {
                    fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_folder);
                    fileViewHolder.tv_fileMsg.setText("修改时间:" + item.getModified());
                }else {
                    if(FileItem.TXT.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_txtfile);
                    }else if(FileItem.PPT.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_ppt);
                    }else if(FileItem.PDF.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_pdf);
                    }else if(FileItem.HTM.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_html);
                    }else if(FileItem.XLS.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_excel);
                    }else if(FileItem.DOC.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_doc);
                    }else if(FileItem.ZIP.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_compressfile);
                    }else if(FileItem.APK.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_apk);
                    }else if(FileItem.JPG.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_album);
                    }else if(FileItem.PNG.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_album);
                    }else if(FileItem.ADO.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_audiofile);
                    }else if(FileItem.VDO.equalsIgnoreCase(item.getFileFormat())) {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_videofile);
                    }else {
                        fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_unknown);
                    }
                    fileViewHolder.tv_fileMsg.setText(item.getSize() + " 修改时间:" + item.getModified());
                }
                fileViewHolder.tv_fileName.setText(item.getFileName());
                /*if (item.getFileMsg().length() != 0) {
                    holder.tv_fileMsg.setVisibility(View.VISIBLE);
                    holder.tv_fileMsg.setText(item.getSize() + " 修改时间:" + item.getModified());
                } else {
                    holder.tv_fileMsg.setVisibility(View.GONE);
                }*/
                //holder.ibtn_fileOperate.setOnClickListener(listener);
                if(item.isCheckboxVisualable()) {
                    fileViewHolder.ibtn_fileOperate.setVisibility(View.VISIBLE);
                    if(item.isChecked()) {
                        fileViewHolder.ibtn_fileOperate.setChecked(true);
                    }else {
                        fileViewHolder.ibtn_fileOperate.setChecked(false);
                    }
                }else {
                    fileViewHolder.ibtn_fileOperate.setVisibility(View.INVISIBLE);
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((GroupPanFragment)context).listViewOnFileItemClick(item);
                    }
                });
                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        vibrator.vibrate(new long[]{0,20}, -1);
                        checkFileItem(item);
                        notifyDataSetChanged();
                        ((GroupPanFragment)context).popWhenSelectFileItem(mInflater, parent, item);
                        return false;
                    }
                });
                break;
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        final RespFile item = getItem(position);
        if(item instanceof Repository) {
            return TYPE_REPOSITORY;
        }else {
            return TYPE_FILE;
        }
    }

    private class ViewHolder {

    }

    private class RespViewHolder extends ViewHolder {
        ImageView iv_fileIcon;
        TextView tv_fileName;
        TextView tv_fileMsg;
        TextView tv_fileSpace;
        CheckBox ibtn_fileOperate;
    }

    private class FileViewHolder extends ViewHolder {
        ImageView iv_fileIcon;
        TextView tv_fileName;
        TextView tv_fileMsg;
        CheckBox ibtn_fileOperate;
    }

    public void checkReposItem(Repository item) {
        if(fileItemList != null && fileItemList.size() > 0) {
            if(item == null) {
                for (RespFile respFile : fileItemList) {
                    Repository fi = (Repository)respFile;
                    fi.setCheckboxVisualable(false);
                    fi.setChecked(false);
                }
            }else {
                for (RespFile respFile : fileItemList) {
                    Repository fi = (Repository)respFile;
                    if (fi.getRepositoryID().equals(item.getRepositoryID())) {
                        fi.setCheckboxVisualable(true);
                        fi.setChecked(true);
                    } else {
                        fi.setCheckboxVisualable(false);
                        fi.setChecked(false);
                    }
                }
            }
        }
    }

    public void checkFileItem(FileItem item) {
        if(fileItemList != null && fileItemList.size() > 0) {
            if(item == null) {
                for (RespFile respFile : fileItemList) {
                    FileItem fi = (FileItem)respFile;
                    fi.setCheckboxVisualable(false);
                    fi.setChecked(false);
                }
            }else {
                for (RespFile respFile : fileItemList) {
                    FileItem fi = (FileItem)respFile;
                    if (fi.getFileId().equals(item.getFileId())) {
                        fi.setCheckboxVisualable(true);
                        fi.setChecked(true);
                    } else {
                        fi.setCheckboxVisualable(false);
                        fi.setChecked(false);
                    }
                }
            }
        }
    }
}
