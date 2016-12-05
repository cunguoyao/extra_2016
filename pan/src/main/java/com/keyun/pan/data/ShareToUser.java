package com.keyun.pan.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/8/22.
 */
public class ShareToUser implements Serializable {

    private String userid;
    private String name;
    private String group;
    private boolean checked;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public static ShareToUser parseJson(JSONObject json) {
        if(json == null) return null;
        ShareToUser file = new ShareToUser();
        file.setUserid(json.optString("UserID"));
        file.setName(json.optString("UserName"));
        file.setGroup(json.optString("GroupName"));
        return file;
    }

    public static List<ShareToUser> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<ShareToUser> list = new ArrayList<ShareToUser>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            ShareToUser fileItem = parseJson(json);
            if(fileItem != null) {
                list.add(fileItem);
            }
        }
        return list;
    }
}
