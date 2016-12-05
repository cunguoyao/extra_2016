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
import com.keyun.pan.data.DownFileItem;
import com.keyun.pan.fragment.BaseFragment;
import com.keyun.pan.utils.MyUtils;
import com.keyun.pan.widget.RoundProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cunguoyao on 2016/7/11.
 */
public class DownloadFileAdapter2 extends BaseAdapter {

    private static final int TYPE_CATEGORY_ITEM = 0;
    private static final int TYPE_ITEM = 1;

    private BaseFragment context;
    private HashMap<Integer, List<DownFileItem>> fileItemMap;
    private List<DownFileItem> fileItemList;
    private Vibrator vibrator;
    private PopupWindow pop;

    public DownloadFileAdapter2(BaseFragment context, List<DownFileItem> fileItems) {
        this.context = context;
        this.fileItemMap = new HashMap<>();
        this.fileItemList = new ArrayList<>();
        resolveData(fileItems);
        this.vibrator = (Vibrator)context.getActivity().getSystemService(Service.VIBRATOR_SERVICE);
    }

    private void resolveData(List<DownFileItem> fileItems) {
        if(fileItems != null && fileItems.size() > 0) {
            for(DownFileItem item : fileItems) {
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
                }

            }
            if(fileItemMap.containsKey(DownFileItem.STATE_DOWN_NONE)) {
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
            }
        }
    }

    public void dataChange(List<DownFileItem> fileItems) {
        fileItemList.clear();
        fileItemMap.clear();
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
        ItemViewHolder itemViewHolder = null;
        FileViewHolder fileViewHolder = null;
        final LayoutInflater mInflater = LayoutInflater.from(context.getActivity());
        int itemViewType = getItemViewType(position);
        if (null == convertView) {
            switch (itemViewType) {
                case TYPE_CATEGORY_ITEM:
                    convertView = mInflater.inflate(R.layout.item_file_downstate, parent, false);
                    itemViewHolder = new ItemViewHolder();
                    itemViewHolder.tv_CateName = (TextView) convertView.findViewById(R.id.tv_text);
                    convertView.setTag(itemViewHolder);
                    break;
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.item_file_down2, parent, false);
                    fileViewHolder = new FileViewHolder();
                    fileViewHolder.iv_fileIcon = (ImageView) convertView.findViewById(R.id.iv_file_icon);
                    fileViewHolder.tv_fileName = (TextView) convertView.findViewById(R.id.tv_file_name);
                    fileViewHolder.tv_fileMsg = (TextView) convertView.findViewById(R.id.tv_file_msg);
                    fileViewHolder.ibtn_fileOperate = (CheckBox) convertView.findViewById(R.id.ibtn_file_operate);
                    fileViewHolder.progressBar = (RoundProgressBar) convertView.findViewById(R.id.progress);
                    convertView.setTag(fileViewHolder);
                    break;
            }
        }else {
            switch (itemViewType) {
                case TYPE_CATEGORY_ITEM:
                    itemViewHolder = (ItemViewHolder) convertView.getTag();
                    break;
                case TYPE_ITEM:
                    fileViewHolder = (FileViewHolder) convertView.getTag();
                    break;
            }
        }
        DownFileItem item = getItem(position);
        switch (itemViewType) {
            case TYPE_CATEGORY_ITEM:
                switch (item.getDownloadState()) {
                    case DownFileItem.STATE_DOWN_NONE:
                        itemViewHolder.tv_CateName.setText("未下载");
                        break;
                    case DownFileItem.STATE_DOWN_ING:
                        itemViewHolder.tv_CateName.setText("正在下载");
                        break;
                    case DownFileItem.STATE_DOWN_PAUSE:
                        itemViewHolder.tv_CateName.setText("暂停下载");
                        break;
                    case DownFileItem.STATE_DOWN_DONE:
                        itemViewHolder.tv_CateName.setText("完成下载");
                        break;
                }
                break;
            case TYPE_ITEM:
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
                fileViewHolder.tv_fileMsg.setText(MyUtils.convertFileSize(item.getSize()) + " 修改时间:" + item.getModified());
                fileViewHolder.tv_fileName.setText(item.getFileName());
                if(item.getDownloadState() == DownFileItem.STATE_DOWN_DONE) {
                    fileViewHolder.progressBar.setVisibility(View.GONE);
                }else {
                    fileViewHolder.progressBar.setVisibility(View.VISIBLE);
                    fileViewHolder.progressBar.setMax(100);
                    fileViewHolder.progressBar.setProgress((int) (100 * item.getDownloadSize() / item.getSize()));
                }
                break;
        }
        return convertView;
    }

    private class ViewHolder {

    }

    private class ItemViewHolder extends  ViewHolder {
        TextView tv_CateName;
    }

    private class FileViewHolder extends  ViewHolder  {
        ImageView iv_fileIcon;
        TextView tv_fileName;
        TextView tv_fileMsg;
        CheckBox ibtn_fileOperate;
        RoundProgressBar progressBar;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // 异常情况处理
        if (null == fileItemList || position <  0|| position > getCount()) {
            return TYPE_ITEM;
        }
        DownFileItem fileItem = fileItemList.get(position);
        if(fileItem.isCate()) {
            return TYPE_CATEGORY_ITEM;
        }
        return TYPE_ITEM;
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
