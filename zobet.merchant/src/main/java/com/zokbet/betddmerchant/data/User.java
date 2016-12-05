package com.zokbet.betddmerchant.data;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by cunguoyao on 2016/5/23.
 */
@Table(name="user")
public class User implements Serializable {

    @Column(name = "id", isId = true)
    private String id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "merch_name")
    private String merchName;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "token")
    private String token;
    @Column(name = "expires_in")
    private long expiresIn;
    @Column(name = "login_time")
    private long loginTime;
    @Column(name = "mon_rmb")
    private double rmbMon;
    @Column(name = "mon_bit")
    private double bitMon;
    @Column(name = "pay_pwd_blank")
    private int payPwdBlank;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getMerchName() {
        return merchName;
    }

    public void setMerchName(String merchName) {
        this.merchName = merchName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public double getRmbMon() {
        return rmbMon;
    }

    public void setRmbMon(double rmbMon) {
        this.rmbMon = rmbMon;
    }

    public double getBitMon() {
        return bitMon;
    }

    public void setBitMon(double bitMon) {
        this.bitMon = bitMon;
    }

    public int getPayPwdBlank() {
        return payPwdBlank;
    }

    public void setPayPwdBlank(int payPwdBlank) {
        this.payPwdBlank = payPwdBlank;
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
