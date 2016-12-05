package com.keyun.pan.data;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by cunguoyao on 2016/5/23.
 */
@Table(name="user")
public class User implements Serializable {

    @Column(name = "id")
    private String id;
    @Column(name = "account",isId = true)
    private String account;
    @Column(name = "password")
    private String password;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "token")
    private String token;
    @Column(name = "userName")
    private String userName;
    @Column(name = "userGroupName")
    private String userGroupName;
    @Column(name = "filterType")
    private int filterType;
    @Column(name = "filterDocType")
    private String filterDocType;
    @Column(name = "serverID")
    private String serverID;
    @Column(name = "expires_in")
    private long expiresIn;
    @Column(name = "login_time")
    private long loginTime;
    @Column(name = "default_acc")
    private int defaultAccount;
    @Column(name = "remember")
    private int remember;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGroupName() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public String getFilterDocType() {
        return filterDocType;
    }

    public void setFilterDocType(String filterDocType) {
        this.filterDocType = filterDocType;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public int getDefaultAccount() {
        return defaultAccount;
    }

    public void setDefaultAccount(int defaultAccount) {
        this.defaultAccount = defaultAccount;
    }

    public int getRemember() {
        return remember;
    }

    public void setRemember(int remember) {
        this.remember = remember;
    }
}
