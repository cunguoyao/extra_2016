package com.keyun.pan.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/6/3.
 */
@Table(name="file_down_item")
public class DownFileItem extends RespFile implements Serializable {

    public final static String Dir = "DIR";
    public final static String TXT = "TXT";
    public final static String PPT = "PPT";
    public final static String PDF = "PDF";
    public final static String HTM = "HTM";
    public final static String XLS = "XLS";
    public final static String DOC = "DOC";
    public final static String ZIP = "ZIP";
    public final static String APK = "APK";
    public final static String JPG = "JPG";
    public final static String PNG = "PNG";
    public final static String ADO = "ADO";
    public final static String VDO = "VDO";

    public final static int STATE_DOWN_NONE = 0;
    public final static int STATE_DOWN_ING = 1;
    public final static int STATE_DOWN_PAUSE = 2;
    public final static int STATE_DOWN_DONE = 3;
    public final static int STATE_DOWN_CANCEL = 4;

    @Column(name = "id", isId = true)
    private String fileId;
    @Column(name = "file_name")
    private String fileName;
    private boolean checkboxVisualable;
    private boolean checked;

    @Column(name = "file_refid")
    private long fileRefID;
    @Column(name = "global_fileid")
    private long globalFileID;
    @Column(name = "size")
    private long size;
    @Column(name = "modified")
    private String modified;
    @Column(name = "is_dir")
    private int isDir;
    @Column(name = "file_format")
    private String fileFormat;
    @Column(name = "is_deleter")
    private int isDeleted;
    @Column(name = "full_path")
    private String fullPath;
    @Column(name = "hash")
    private String hash;
    @Column(name = "link_id")
    private String linkID;

    @Column(name = "is_cate")
    private boolean isCate;//下载状态分类用，业务无关
    @Column(name = "down_state")
    private int downloadState;//0未下载 1正在下载 2暂停下载 3已下载
    @Column(name = "down_path")
    private String downloadPath;
    @Column(name = "down_size")
    private long downloadSize;

    @Column(name = "click_down_time")
    private long clickDownTime;
    @Column(name = "t")
    private int t;//0为自己的网盘 1为资料库
    @Column(name = "user_name")
    private String userName;

    public static DownFileItem copyFiled(FileItem fileItem) {
        if(fileItem != null) {
            DownFileItem downFileItem = new DownFileItem();
            downFileItem.setCate(false);
            downFileItem.setFileFormat(fileItem.getFileFormat());
            downFileItem.setFileId(fileItem.getFileId());
            downFileItem.setFileName(fileItem.getFileName());
            downFileItem.setFileRefID(fileItem.getFileRefID());
            downFileItem.setFullPath(fileItem.getFullPath());
            downFileItem.setGlobalFileID(fileItem.getGlobalFileID());
            downFileItem.setHash(fileItem.getHash());
            downFileItem.setIsDeleted(fileItem.getIsDeleted());
            downFileItem.setIsDir(fileItem.getIsDir());
            downFileItem.setLinkID(fileItem.getLinkID());
            downFileItem.setModified(fileItem.getModified());
            downFileItem.setUserName(fileItem.getUserName());
            downFileItem.setSize(fileItem.getSize());
            downFileItem.setT(fileItem.getT());
            return downFileItem;
        }
        return null;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileRefID() {
        return fileRefID;
    }

    public void setFileRefID(long fileRefID) {
        this.fileRefID = fileRefID;
    }

    public long getGlobalFileID() {
        return globalFileID;
    }

    public void setGlobalFileID(long globalFileID) {
        this.globalFileID = globalFileID;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public int getIsDir() {
        return isDir;
    }

    public void setIsDir(int isDir) {
        this.isDir = isDir;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getLinkID() {
        return linkID;
    }

    public void setLinkID(String linkID) {
        this.linkID = linkID;
    }

    public boolean isCate() {
        return isCate;
    }

    public void setCate(boolean cate) {
        isCate = cate;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public long getClickDownTime() {
        return clickDownTime;
    }

    public void setClickDownTime(long clickDownTime) {
        this.clickDownTime = clickDownTime;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isCheckboxVisualable() {
        return checkboxVisualable;
    }

    public void setCheckboxVisualable(boolean checkboxVisualable) {
        this.checkboxVisualable = checkboxVisualable;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public static DownFileItem parseJson(JSONObject json) {
        if(json == null) return null;
        DownFileItem file = new DownFileItem();
        file.setFileFormat(json.optString("FileFormat"));
        file.setFileId(json.optString("FileID"));
        file.setFileName(json.optString("filename"));
        file.setFileRefID(json.optInt("FileRefID"));
        file.setFullPath(json.optString("FullPath"));
        file.setGlobalFileID(json.optInt("GlobalFileID"));
        file.setHash(json.optString("hash"));
        file.setIsDeleted(json.optInt("IsDeleted",0));
        file.setIsDir(json.optInt("is_dir",0));
        file.setLinkID(json.optString("LinkID"));
        file.setModified(json.optString("modified"));
        file.setSize(json.optInt("size",0));
        return file;
    }

    public static List<DownFileItem> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<DownFileItem> list = new ArrayList<DownFileItem>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            DownFileItem fileItem = parseJson(json);
            if(fileItem != null) {
                list.add(fileItem);
            }
        }
        return list;
    }

    public static DownFileItem parseJsonRepos(JSONObject json) {
        if(json == null) return null;
        DownFileItem file = new DownFileItem();
        file.setFileFormat(json.optString("FileFormat"));
        file.setFileId(json.optString("FileID"));
        file.setFileName(json.optString("FileName"));
        file.setFileRefID(json.optInt("FileRefID"));
        file.setFullPath(json.optString("FullPath"));
        file.setGlobalFileID(json.optInt("GlobalFileID"));
        file.setHash(json.optString("FileMD5"));
        file.setIsDeleted(json.optInt("IsDeleted",0));
        file.setIsDir(json.optInt("FileType",0) == 0 ? 1 : 0);
        file.setLinkID(json.optString("LinkID"));
        file.setModified(json.optString("LastModifyTime"));
        file.setSize(json.optInt("FileSize",0));
        return file;
    }

    public static List<DownFileItem> parseJsonRepos(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<DownFileItem> list = new ArrayList<DownFileItem>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            DownFileItem fileItem = parseJsonRepos(json);
            if(fileItem != null) {
                list.add(fileItem);
            }
        }
        return list;
    }
}
