package com.keyun.pan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.keyun.pan.app.BaseApplication;
import com.keyun.pan.app.Urls;
import com.keyun.pan.data.User;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/5/15.
 */
public class BaseActivity extends FragmentActivity {

    protected DbManager db;
    protected User user;
    protected Urls Urls;
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
        db = x.getDb(((BaseApplication)getApplicationContext()).getDaoConfig());
        ScreenManager.getScreenManager().pushActivity(this);
        user = getDefaultUser();
        Urls = new Urls(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManager.getScreenManager().popActivity(this);
    }

    public void exit() {
        ScreenManager.getScreenManager().popAllActivityExceptOne(LoginActivity.class);
    }

    public void exitToLogin() {
        ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity.class);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
