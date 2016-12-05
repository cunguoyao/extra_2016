package com.zokbet.betdd.data;

import com.zokbet.betdd.app.Urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/5/20.
 */
public class MerchantPhoto implements Serializable {

    public String url;
    public String desc;

    public static MerchantPhoto parseJson(JSONObject json) {
        if (json == null) return null;
        MerchantPhoto merchantPhoto = new MerchantPhoto();
        merchantPhoto.url = Urls.BASE + json.optString("url") + json.optString("fileName");
        merchantPhoto.desc = json.optString("title");
        return merchantPhoto;
    }

    public static List<MerchantPhoto> parseJson(JSONArray jsonArray) {
        if(jsonArray == null)return null;
        List<MerchantPhoto> list = new ArrayList<MerchantPhoto>();
        if(jsonArray.length() == 0)return list;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject json = jsonArray.optJSONObject(i);
            MerchantPhoto photo = parseJson(json);
            if(photo != null) {
                list.add(photo);
            }
        }
        return list;
    }
}
