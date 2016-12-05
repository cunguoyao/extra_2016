package com.keyun.pan.adapter;

import android.app.Service;
import android.content.Intent;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.keyun.pan.R;
import com.keyun.pan.data.DownFileItem;
import com.keyun.pan.fragment.BaseFragment;
import com.keyun.pan.service.DownloadService;
import com.keyun.pan.utils.MyUtils;
import com.keyun.pan.widget.RoundProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cunguoyao on 2016/7/11.
 */
public class DownloadFileAdapter extends BaseAdapter {

    private BaseFragment context;
    private List<DownFileItem> fileItemList;
    private Vibrator vibrator;
    private PopupWindow pop;

    public DownloadFileAdapter(BaseFragment context, List<DownFileItem> fileItems) {
        this.context = context;
        this.fileItemList = new ArrayList<>();
        resolveData(fileItems);
        this.vibrator = (Vibrator)context.getActivity().getSystemService(Service.VIBRATOR_SERVICE);
    }

    private void resolveData(List<DownFileItem> fileItems) {
        if(fileItems != null && fileItems.size() > 0) {
            for(DownFileItem item : fileItems) {
                /*fileItemss.add(item);
                if(fileItemMap.containsKey(item.getDownloadState())) {
                    List<DownFileItem> fileItemss = fileItemMap.get(item.getDownloadState());
                    if(fileItemss == null) {
                        fileItemss = new ArrayList<>();
                    }
                    fileItemss.add(item);
                }else {
                    List<DownFileItem> fileItemss = new ArrayList<>();
                    fileItemss.add(item);
                    fileItemMap.put(item.getDownloadState(), fileItemss);
                }*/
                fileItemList.add(item);
            }
            /*if(fileItemMap.containsKey(DownFileItem.STATE_DOWN_NONE)) {
                DownFileItem f = new DownFileItem();
                f.setCate(true);
                f.setDownloadState(DownFileItem.STATE_DOWN_NONE);
                fileItemList.add(f);
                fileItemList.addAll(fileItemMap.get(DownFileItem.STATE_DOWN_NONE));
            }
            if(fileItemMap.containsKey(DownFileItem.STATE_DOWN_ING)) {
                DownFileItem f = new DownFileItem();
                f.setCate(true);
                f.setDownloadState(DownFileItem.STATE_DOWN_ING);
                fileItemList.add(f);
                fileItemList.addAll(fileItemMap.get(DownFileItem.STATE_DOWN_ING));
            }
            if(fileItemMap.containsKey(DownFileItem.STATE_DOWN_PAUSE)) {
                DownFileItem f = new DownFileItem();
                f.setCate(true);
                f.setDownloadState(DownFileItem.STATE_DOWN_PAUSE);
                fileItemList.add(f);
                fileItemList.addAll(fileItemMap.get(DownFileItem.STATE_DOWN_PAUSE));
            }
            if(fileItemMap.containsKey(DownFileItem.STATE_DOWN_DONE)) {
                DownFileItem f = new DownFileItem();
                f.setCate(true);
                f.setDownloadState(DownFileItem.STATE_DOWN_DONE);
                fileItemList.add(f);
                fileItemList.addAll(fileItemMap.get(DownFileItem.STATE_DOWN_DONE));
            }*/
        }
    }

    public void dataChange(List<DownFileItem> fileItems) {
        fileItemList.clear();
        //fileItemMap.clear();
        resolveData(fileItems);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fileItemList == null ? 0 : fileItemList.size();
    }

    @Override
    public DownFileItem getItem(int position) {
        return fileItemList == null ? null : fileItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        FileViewHolder fileViewHolder = null;
        final LayoutInflater mInflater = LayoutInflater.from(context.getActivity());
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_file_down, parent, false);
            fileViewHolder = new FileViewHolder();
            fileViewHolder.iv_fileIcon = (ImageView) convertView.findViewById(R.id.iv_file_icon);
            fileViewHolder.tv_fileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            fileViewHolder.tv_fileMsg = (TextView) convertView.findViewById(R.id.tv_file_msg);
            fileViewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
            fileViewHolder.tv_fileSize = (TextView) convertView.findViewById(R.id.tv_file_size);
            fileViewHolder.tv_fileProgress = (TextView) convertView.findViewById(R.id.tv_file_progress);
            fileViewHolder.bt_fileOperate = (Button) convertView.findViewById(R.id.bt_file_oper);
            convertView.setTag(fileViewHolder);
        }else {
            fileViewHolder = (FileViewHolder) convertView.getTag();
        }
        final DownFileItem item = getItem(position);
        if(DownFileItem.TXT.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_txtfile);
        }else if(DownFileItem.PPT.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_ppt);
        }else if(DownFileItem.PDF.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_pdf);
        }else if(DownFileItem.HTM.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_html);
        }else if(DownFileItem.XLS.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_excel);
        }else if(DownFileItem.DOC.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_doc);
        }else if(DownFileItem.ZIP.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_compressfile);
        }else if(DownFileItem.APK.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_apk);
        }else if(DownFileItem.JPG.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_album);
        }else if(DownFileItem.PNG.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_album);
        }else if(DownFileItem.ADO.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_audiofile);
        }else if(DownFileItem.VDO.equalsIgnoreCase(item.getFileFormat())) {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_videofile);
        }else {
            fileViewHolder.iv_fileIcon.setImageResource(R.drawable.icon_list_unknown);
        }
        int progress;
        if(item.getSize() != 0) {
            progress = (int) (100 * item.getDownloadSize() / item.getSize());
        }else {
            progress = 100;
        }
        fileViewHolder.tv_fileMsg.setText("文件日期:" + MyUtils.cutDate(item.getModified()));
        fileViewHolder.tv_fileName.setText(item.getFileName());
        fileViewHolder.tv_fileSize.setText(MyUtils.convertFileSize(item.getSize()));
//        if(item.getDownloadState() == DownFileItem.STATE_DOWN_DONE) {
//            fileViewHolder.progressBar.setVisibility(View.GONE);
//        }else {
            fileViewHolder.progressBar.setVisibility(View.VISIBLE);
            fileViewHolder.progressBar.setMax(100);
            fileViewHolder.progressBar.setProgress(progress);
//        }

        fileViewHolder.tv_fileProgress.setText(progress + "%");
        if(item.getDownloadState() == DownFileItem.STATE_DOWN_DONE) {
            fileViewHolder.bt_fileOperate.setBackgroundResource(R.drawable.download_state_success);
        }else if(item.getDownloadState() == DownFileItem.STATE_DOWN_ING) {
            fileViewHolder.bt_fileOperate.setBackgroundResource(R.drawable.download_state_cancel);
            fileViewHolder.bt_fileOperate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getActivity(), DownloadService.class);
                    intent.putExtra("FileItem", item);
                    intent.putExtra("op", 1);
                    context.getActivity().startService(intent);
                }
            });
        }else if(item.getDownloadState() == DownFileItem.STATE_DOWN_CANCEL) {
            fileViewHolder.bt_fileOperate.setBackgroundResource(R.drawable.download_state_restart);
            fileViewHolder.bt_fileOperate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getActivity(), DownloadService.class);
                    intent.putExtra("FileItem", item);
                    intent.putExtra("op", 2);
                    context.getActivity().startService(intent);
                    Toast.makeText(context.getActivity(), "重新加入下载队列", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return convertView;
    }

    private class ViewHolder {

    }

    private class FileViewHolder extends  ViewHolder  {
        ImageView iv_fileIcon;
        TextView tv_fileName;
        TextView tv_fileMsg;
        ProgressBar progressBar;
        TextView tv_fileSize;
        TextView tv_fileProgress;
        Button bt_fileOperate;
    }

    public void checkItem(DownFileItem item) {
        if(fileItemList != null && fileItemList.size() > 0) {
            if(item == null) {
                for (DownFileItem fi : fileItemList) {
                    fi.setCheckboxVisualable(false);
                    fi.setChecked(false);
                }
            }else {
                for (DownFileItem fi : fileItemList) {
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
