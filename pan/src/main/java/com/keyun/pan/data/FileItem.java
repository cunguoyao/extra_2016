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
@Table(name="file_item")
public class FileItem extends RespFile implements Serializable {

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
    private int isDir;//0文件夹 1文件
    @Column(name = "file_format")
    private String fileFormat;
    @Column(name = "is_deleter")
    private int isDeleted;
    @Column(name = "full_path")
    private String fullPath;
    @Column(name = "delete_time")
    private String deleteTime;
    @Column(name = "hash")
    private String hash;
    @Column(name = "sync_enabled")
    private int syncEnabled;
    @Column(name = "link_id")
    private String linkID;

    @Column(name = "current_dir")
    private String currentDirPath;
    @Column(name = "t")
    private int t;//0为自己的网盘 1为资料库
    @Column(name = "user_name")
    private String userName;

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

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getSyncEnabled() {
        return syncEnabled;
    }

    public void setSyncEnabled(int syncEnabled) {
        this.syncEnabled = syncEnabled;
    }

    public String getLinkID() {
        return linkID;
    }

    public void setLinkID(String linkID) {
        this.linkID = linkID;
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

    public String getCurrentDirPath() {
        return currentDirPath;
    }

    public void setCurrentDirPath(String currentDirPath) {
        this.currentDirPath = currentDirPath;
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

    public static FileItem parseJson(JSONObject json) {
        if(json == null) return null;
        FileItem file = new FileItem();
        file.setDeleteTime(json.optString("DeleteTime"));
        file.setFileFormat(json.optString("FileFormat"));
        file.setFileId(json.optString("FileID"));
        if(json.has("filename")) {
            file.setFileName(json.optString("filename"));
        }
        if(json.has("FileName")) {
            file.setFileName(json.optString("FileName"));
        }
        file.setFileRefID(json.optInt("FileRefID"));
        file.setFullPath(json.optString("FullPath"));
        file.setGlobalFileID(json.optInt("GlobalFileID"));
        if(json.has("hash")) {
            file.setHash(json.optString("hash"));
        }
        if(json.has("FileMD5")) {
            file.setHash(json.optString("FileMD5"));
        }
        file.setIsDeleted(json.optInt("IsDeleted",0));
        if(json.has("is_dir")) {
            file.setIsDir(json.optInt("is_dir", 0));
        }
        if(json.has("FileType")) {
            file.setIsDir(json.optInt("FileType",0) == 0 ? 1 : 0);
        }
        file.setLinkID(json.optString("LinkID"));
        if(json.has("modified")) {
            file.setModified(json.optString("modified"));
        }
        if(json.has("LastModifyTime")) {
            file.setModified(json.optString("LastModifyTime"));
        }
        if(json.has("size")) {
            file.setSize(json.optInt("size", 0));
        }
        if(json.has("FileSize")) {
            file.setSize(json.optInt("FileSize", 0));
        }
        file.setSyncEnabled(json.optInt("SyncEnabled"));
        return file;
    }

    public static List<FileItem> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<FileItem> list = new ArrayList<FileItem>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            FileItem fileItem = parseJson(json);
            if(fileItem != null) {
                list.add(fileItem);
            }
        }
        return list;
    }

    public static FileItem parseJsonRepos(JSONObject json) {
        if(json == null) return null;
        FileItem file = new FileItem();
        file.setDeleteTime(json.optString("DeleteTime"));
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
        file.setSyncEnabled(json.optInt("SyncEnabled"));
        return file;
    }

    public static List<FileItem> parseJsonRepos(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<FileItem> list = new ArrayList<FileItem>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            FileItem fileItem = parseJsonRepos(json);
            if(fileItem != null) {
                list.add(fileItem);
            }
        }
        return list;
    }
}
