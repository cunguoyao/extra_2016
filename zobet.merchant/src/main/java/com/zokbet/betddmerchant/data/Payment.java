package com.zokbet.betddmerchant.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cunguoyao on 2016/5/26.
 */
public class Payment implements Serializable {

    private String merchId;
    private String merchName;
    private String merchAvatar;
    private int type;//10-收入，20-支付
    private String payMoney;
    private String payNote;
    private String payDate;

    public String getMerchId() {
        return merchId;
    }

    public void setMerchId(String merchId) {
        this.merchId = merchId;
    }

    public String getMerchName() {
        return merchName;
    }

    public void setMerchName(String merchName) {
        this.merchName = merchName;
    }

    public String getMerchAvatar() {
        return merchAvatar;
    }

    public void setMerchAvatar(String merchAvatar) {
        this.merchAvatar = merchAvatar;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(String payMoney) {
        this.payMoney = payMoney;
    }

    public String getPayNote() {
        return payNote;
    }

    public void setPayNote(String payNote) {
        this.payNote = payNote;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public static Payment parseJsonForList(JSONObject json) {
        if(json == null)return null;
        Payment payment = new Payment();
        payment.setMerchId(json.optString("sid"));
        payment.setMerchName(json.optString("title"));
        payment.setMerchAvatar(json.optString("logo"));
        payment.setType(json.optInt("type"));
        payment.setPayMoney(json.optString("amt"));
        payment.setPayNote(json.optString("sourceMobile"));
        long create = json.optLong("created");
        Date date = new Date(create);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        payment.setPayDate(simpleDateFormat.format(date));
        return payment;
    }

    public static List<Payment> parseJsonForList(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<Payment> list = new ArrayList<Payment>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            Payment payment = parseJsonForList(json);
            if(payment != null) {
                list.add(payment);
            }
        }
        return list;
    }
}
