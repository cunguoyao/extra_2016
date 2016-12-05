package com.keyun.pan.data;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by cunguoyao on 2016/7/9.
 */
public class MyInfo implements Serializable {

    private String userID;
    private long space;
    private long userd_space;
    private String email;
    private String phone;
    private String position;
    private String regTime;
    private String curNameSpaceID;
    private int adminType;
    private String userName;
    private String machineDept;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    public long getUserd_space() {
        return userd_space;
    }

    public void setUserd_space(long userd_space) {
        this.userd_space = userd_space;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getCurNameSpaceID() {
        return curNameSpaceID;
    }

    public void setCurNameSpaceID(String curNameSpaceID) {
        this.curNameSpaceID = curNameSpaceID;
    }

    public int getAdminType() {
        return adminType;
    }

    public void setAdminType(int adminType) {
        this.adminType = adminType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMachineDept() {
        return machineDept;
    }

    public void setMachineDept(String machineDept) {
        this.machineDept = machineDept;
    }

    public static MyInfo parseJson(JSONObject json) {
        if (json == null) return null;
        MyInfo info = new MyInfo();
        info.setUserID(json.optString("UserID"));
        info.setUserName(json.optString("UserName"));
        info.setAdminType(json.optInt("AdminType"));
        info.setCurNameSpaceID(json.optString("CurNameSpaceID"));
        info.setEmail(json.optString("email"));
        info.setMachineDept(json.optString("MachineDept"));
        info.setPhone(json.optString("phone"));
        info.setPosition(json.optString("position"));
        info.setRegTime(json.optString("RegTime"));
        info.setSpace(json.optLong("space"));
        info.setUserd_space(json.optLong("used_space"));
        return info;
    }
}
