package com.zokbet.betdd.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cunguoyao on 2016/5/27.
 */
public class Category implements Serializable {

    private int cid;
    private String name;
    private String picPath;
    private int sort;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public static Category parseJson(JSONObject json) {
        if(json == null)return null;
        Category category = new Category();
        category.setCid(json.optInt("cid"));
        category.setName(json.optString("name"));
        category.setPicPath(json.optString("picPath"));
        category.setSort(json.optInt("sort"));
        return category;
    }

    public static ArrayList<Category> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        ArrayList<Category> list = new ArrayList<Category>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            Category category = parseJson(json);
            if(category != null) {
                list.add(category);
            }
        }
        return list;
    }

}
