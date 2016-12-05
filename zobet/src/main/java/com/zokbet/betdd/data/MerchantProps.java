package com.zokbet.betdd.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/5/20.
 */
public class MerchantProps implements Serializable {

    public String value;
    public String name;

    public static MerchantProps parseJson(JSONObject json) {
        if (json == null) return null;
        MerchantProps merchantPhoto = new MerchantProps();
        merchantPhoto.value = json.optString("val");
        merchantPhoto.name = json.optString("name");
        return merchantPhoto;
    }

    public static List<MerchantProps> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<MerchantProps> list = new ArrayList<MerchantProps>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            MerchantProps photo = parseJson(json);
            if(photo != null) {
                list.add(photo);
            }
        }
        return list;
    }

    @Override
    public String toString() {
        return value + name;
    }
}
