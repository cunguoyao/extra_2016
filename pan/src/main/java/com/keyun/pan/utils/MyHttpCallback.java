package com.keyun.pan.utils;

import android.app.Activity;
import android.content.Intent;

import com.keyun.pan.activity.LoginActivity;
import com.keyun.pan.activity.ScreenManager;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;

/**
 * Created by cunguoyao on 2016/9/28.
 */
public abstract class MyHttpCallback implements Callback.CommonCallback<String> {

    private Activity activity;

    public MyHttpCallback(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onSuccess(String result) {
        LogUtil.e("---onSuccess-result----" + result);
        try {
            JSONObject json = new JSONObject(result);
            if (json.optInt("ErrorCode") == 401 || json.optInt("ErrCode") == 401) {
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.putExtra(LoginActivity.AutoLogin, 1);
                activity.startActivity(intent);
                ScreenManager.getScreenManager().popAllActivityExceptOne(LoginActivity.class);
                activity.finish();
            }else {
                onSucceed(result);
            }
        } catch (Exception e) {
            onError(e, true);
        }
    }

    public abstract void onSucceed(String result);

}
