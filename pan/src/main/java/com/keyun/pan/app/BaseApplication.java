package com.keyun.pan.app;

import android.app.Application;
import android.util.Log;

import com.keyun.pan.data.User;
import com.tencent.bugly.Bugly;

import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by cunguoyao on 2016/6/2.
 */
public class BaseApplication extends Application {

    private static final String DB_NAME = "keyun";
    //private static final String DB_PATH = "/sdcard/xx";
    private static final int DB_VERSION = 6;

    private DbManager.DaoConfig daoConfig;

    public DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }

    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        x.Ext.init(this);//Xutils初始化
        x.Ext.setDebug(true);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        daoConfig = new DbManager.DaoConfig()
                .setDbName(DB_NAME)//创建数据库的名称
                //.setDbDir(new File(DB_PATH))//如果不设置，那么数据库默认存储在/data/data/你的应用程序/database/xxx.db下
                .setDbVersion(DB_VERSION)//数据库版本号
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        Log.e("------ba--------", "---onUpgrade---");
                        try {
                            db.dropTable(User.class);
                            db.replace(User.class);
                            db.execNonQuery("delete from user");
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                });//数据库更新操作

        Bugly.init(getApplicationContext(), "900041464", false);
    }

    public static BaseApplication getInstance(){
        return instance;
    }

    public String getJPushId() {
        String jPushRegistrationId = JPushInterface.getRegistrationID(getApplicationContext());
        LogUtil.e("JPushId-" + jPushRegistrationId);
        return jPushRegistrationId;
    }
}
