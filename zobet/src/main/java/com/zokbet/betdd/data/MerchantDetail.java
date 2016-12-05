package com.zokbet.betdd.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/5/18.
 */
public class MerchantDetail implements Serializable {

    private String id;
    private String name;
    private String face;
    private int star;
    private double average;
    private String address;
    private MerchantCate cate;
    private MerchantArea area;
    private ArrayList<MerchantPhoto> images;
    private ArrayList<MerchantProps> props;
    private String mobile;
    private String detail;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public MerchantArea getArea() {
        return area;
    }

    public void setArea(MerchantArea area) {
        this.area = area;
    }

    public ArrayList<MerchantPhoto> getImages() {
        return images;
    }

    public void setImages(ArrayList<MerchantPhoto> images) {
        this.images = images;
    }

    public ArrayList<MerchantProps> getProps() {
        return props;
    }

    public void setProps(ArrayList<MerchantProps> props) {
        this.props = props;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public static MerchantDetail parseJson(JSONObject json) {
        if(json == null)return null;
        MerchantDetail merchant = new MerchantDetail();
        merchant.setId(String.valueOf(json.optLong("sid")));
        merchant.setName(json.optString("title"));
        merchant.setFace(json.optString("picPathUrl"));
        String star = json.optString("shopScore");
        String reg = "\\d+(\\.\\d+)?";
        if(star != null && star.matches(reg)) {
            double starD = Double.valueOf(star);
            int starI = (int)(starD * 20);
            merchant.setStar(starI);
        }
        merchant.setAverage(json.optDouble("consumptionPerPerson"));
        merchant.setAddress(json.optString("address"));

        MerchantCate cate = new MerchantCate();
        cate.id = json.optInt("cid");
        cate.name = json.optString("category");
        merchant.setCate(cate);

        MerchantArea area = new MerchantArea();
        area.city = json.optString("city");
        area.area = json.optString("area");
        merchant.setMerchantArea(area);

        ArrayList<MerchantPhoto> photos = new ArrayList<>();
        JSONArray photoArray = json.optJSONArray("images");
        if(photoArray != null && photoArray.length() > 0) {
            photos.addAll(MerchantPhoto.parseJson(photoArray));
            merchant.setImages(photos);
        }
        ArrayList<MerchantProps> props = new ArrayList<>();
        JSONArray propsArray = json.optJSONArray("props");
        if(propsArray != null && propsArray.length() > 0) {
            props.addAll(MerchantProps.parseJson(propsArray));
            merchant.setProps(props);
        }
        merchant.setMobile(json.optString("mobile"));
        merchant.setDetail(json.optString("detail"));
        merchant.setDiscount(json.optString("discount"));
        merchant.setIntegrity(json.optInt("integrity"));

        return merchant;
    }

    public static List<MerchantDetail> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<MerchantDetail> list = new ArrayList<MerchantDetail>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            MerchantDetail category = parseJson(json);
            if(category != null) {
                list.add(category);
            }
        }
        return list;
    }
}
