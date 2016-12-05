package com.keyun.pan.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/7/18.
 */
public class ApprovalFile implements Serializable {

    private String FileName;
    private String FilePath;
    private long FileSize;
    private String FileMD5;
    private String LastModifyTime;
    private int DecodeType;

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public long getFileSize() {
        return FileSize;
    }

    public void setFileSize(long fileSize) {
        FileSize = fileSize;
    }

    public String getFileMD5() {
        return FileMD5;
    }

    public void setFileMD5(String fileMD5) {
        FileMD5 = fileMD5;
    }

    public String getLastModifyTime() {
        return LastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        LastModifyTime = lastModifyTime;
    }

    public int getDecodeType() {
        return DecodeType;
    }

    public void setDecodeType(int decodeType) {
        DecodeType = decodeType;
    }

    public static ApprovalFile parseJson(JSONObject json) {
        if (json == null) return null;
        ApprovalFile file = new ApprovalFile();
        file.setFileName(json.optString("FileName"));
        file.setFilePath(json.optString("FilePath"));
        file.setFileSize(json.optLong("FileSize"));
        file.setFileMD5(json.optString("FileMD5"));
        file.setLastModifyTime(json.optString("LastModifyTime"));
        file.setDecodeType(json.optInt("DecodeType"));
        return file;
    }

    public static List<ApprovalFile> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<ApprovalFile> list = new ArrayList<ApprovalFile>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            ApprovalFile fileItem = parseJson(json);
            if(fileItem != null) {
                list.add(fileItem);
            }
        }
        return list;
    }
}
