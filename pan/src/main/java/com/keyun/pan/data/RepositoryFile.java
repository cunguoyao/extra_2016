package com.keyun.pan.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/7/10.
 */
public class RepositoryFile extends RespFile implements Serializable {

    private boolean checked;

    private String id;
    private String pId;
    private String path;
    private String name;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static RepositoryFile parseJson(JSONObject json) {
        if (json == null) return null;
        RepositoryFile info = new RepositoryFile();
        info.setId(json.optString("id"));
        info.setName(json.optString("name"));
        info.setpId(json.optString("pId"));
        info.setPath(json.optString("path"));
        return info;
    }

    public static List<RepositoryFile> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<RepositoryFile> list = new ArrayList<RepositoryFile>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            RepositoryFile fileItem = parseJson(json);
            if(fileItem != null) {
                list.add(fileItem);
            }
        }
        return list;
    }
}
