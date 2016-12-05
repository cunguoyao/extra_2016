package com.zokbet.betdd.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/5/18.
 */
public class Merchant implements Serializable {

    private String id;
    private String name;
    private String face;
    private int star;
    private double average;
    private MerchantCate cate;
    private MerchantArea area;
    private String discount;
    private int integrity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public MerchantCate getCate() {
        return cate;
    }

    public void setCate(MerchantCate cate) {
        this.cate = cate;
    }

    public MerchantArea getMerchantArea() {
        return area;
    }

    public void setMerchantArea(MerchantArea area) {
        this.area = area;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getIntegrity() {
        return integrity;
    }

    public void setIntegrity(int integrity) {
        this.integrity = integrity;
    }

    public static Merchant parseJsonForList(JSONObject json) {
        if(json == null)return null;
        Merchant merchant = new Merchant();
        merchant.setId(String.valueOf(json.optLong("sid")));
        merchant.setName(json.optString("name"));
        merchant.setFace(json.optString("face"));
        String star = json.optString("star");
        String reg = "\\d+(\\.\\d+)?";
        if(star != null && star.matches(reg)) {
            double starD = Double.valueOf(star);
            int starI = (int)(starD * 20);
            merchant.setStar(starI);
        }
        merchant.setAverage(json.optDouble("average"));

        MerchantCate cate = new MerchantCate();
        cate.id = json.optInt("cid");
        cate.name = json.optString("category");
        merchant.setCate(cate);

        MerchantArea area = new MerchantArea();
        area.city = json.optString("city");
        area.area = json.optString("area");
        merchant.setMerchantArea(area);

        merchant.setDiscount(json.optString("discount"));
        merchant.setIntegrity(json.optInt("integrity"));

        return merchant;
    }

    public static List<Merchant> parseJsonForList(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<Merchant> list = new ArrayList<Merchant>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            Merchant category = parseJsonForList(json);
            if(category != null) {
                list.add(category);
            }
        }
        return list;
    }
}
