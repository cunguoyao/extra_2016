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
import com.keyun.pan.activity.SearchFileActivity;
import com.keyun.pan.data.FileItem;
import com.keyun.pan.fragment.BaseFragment;
import com.keyun.pan.fragment.MyPanFragment;
import com.keyun.pan.fragment.SearchResultFragment;
import com.keyun.pan.utils.MyUtils;

import java.util.List;

/**
 * Created by cunguoyao on 2016/6/3.
 */
public class SearchFileListAdapter extends BaseAdapter {

    private SearchResultFragment activity;
    private List<FileItem> fileItemList;
    private Vibrator vibrator;
    private PopupWindow pop;

    public SearchFileListAdapter(SearchResultFragment context, List<FileItem> fileItemList) {
        this.activity = context;
        this.fileItemList = fileItemList;
        this.vibrator = (Vibrator)context.getActivity().getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    public int getCount() {
        return fileItemList == null ? 0 : fileItemList.size();
    }

    @Override
    public FileItem getItem(int position) {
        return fileItemList == null ? null : fileItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        final LayoutInflater mInflater = LayoutInflater.from(activity.getActivity());
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_file, parent, false);
            holder = new ViewHolder();
            holder.iv_fileIcon = (ImageView) convertView.findViewById(R.id.iv_file_icon);
            holder.tv_fileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            holder.tv_fileMsg = (TextView) convertView.findViewById(R.id.tv_file_msg);
            holder.ibtn_fileOperate = (CheckBox) convertView.findViewById(R.id.ibtn_file_operate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final FileItem item = fileItemList.get(position);
        if(item.getIsDir() == 0) {
            holder.iv_fileIcon.setImageResource(R.drawable.icon_list_folder);
            holder.tv_fileMsg.setText(" " + item.getModified());
        }else {
            if(FileItem.TXT.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_txtfile);
            }else if(FileItem.PPT.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_ppt);
            }else if(FileItem.PDF.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_pdf);
            }else if(FileItem.HTM.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_html);
            }else if(FileItem.XLS.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_excel);
            }else if(FileItem.DOC.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_doc);
            }else if(FileItem.ZIP.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_compressfile);
            }else if(FileItem.APK.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_apk);
            }else if(FileItem.JPG.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_album);
            }else if(FileItem.PNG.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_album);
            }else if(FileItem.ADO.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_audiofile);
            }else if(FileItem.VDO.equalsIgnoreCase(item.getFileFormat())) {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_videofile);
            }else {
                holder.iv_fileIcon.setImageResource(R.drawable.icon_list_unknown);
            }
            holder.tv_fileMsg.setText(MyUtils.convertFileSize(item.getSize()) + " 修改时间:" + item.getModified());
        }
        if("我的安全盘".equals(item.getFileName())) {
            holder.iv_fileIcon.setImageResource(R.drawable.bxg);
        }
        holder.tv_fileName.setText(item.getFileName());
        /*if (item.getFileMsg().length() != 0) {
            holder.tv_fileMsg.setVisibility(View.VISIBLE);
            holder.tv_fileMsg.setText(item.getSize() + " 修改时间:" + item.getModified());
        } else {
            holder.tv_fileMsg.setVisibility(View.GONE);
        }*/
        //holder.ibtn_fileOperate.setOnClickListener(listener);
        if(item.isCheckboxVisualable()) {
            holder.ibtn_fileOperate.setVisibility(View.VISIBLE);
            if(item.isChecked()) {
                holder.ibtn_fileOperate.setChecked(true);
            }else {
                holder.ibtn_fileOperate.setChecked(false);
            }
        }else {
            holder.ibtn_fileOperate.setVisibility(View.INVISIBLE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.listViewOnItemClick(item);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                vibrator.vibrate(new long[]{0,20}, -1);
                checkItem(item);
                notifyDataSetChanged();
                activity.pop(mInflater, parent, item);
                return false;
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView iv_fileIcon;
        TextView tv_fileName;
        TextView tv_fileMsg;
        CheckBox ibtn_fileOperate;
    }

    public void checkItem(FileItem item) {
        if(fileItemList != null && fileItemList.size() > 0) {
            if(item == null) {
                for (FileItem fi : fileItemList) {
                    fi.setCheckboxVisualable(false);
                    fi.setChecked(false);
                }
            }else {
                for (FileItem fi : fileItemList) {
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
