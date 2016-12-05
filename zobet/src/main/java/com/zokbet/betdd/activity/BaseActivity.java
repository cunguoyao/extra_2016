package com.zokbet.betdd.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;
import com.zokbet.betdd.app.ZobetApplication;
import com.zokbet.betdd.data.User;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/5/15.
 */
public class BaseActivity extends FragmentActivity {

    protected DbManager db;
    protected User user;

    protected User getDefaultUser() {
        try {
            user = db.selector(User.class).where("default_acc", "=", 1).findFirst();
        }catch (DbException e) {
        }
        return user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = x.getDb(((ZobetApplication)getApplicationContext()).getDaoConfig());
        ScreenManager.getScreenManager().pushActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManager.getScreenManager().popActivity(this);
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
