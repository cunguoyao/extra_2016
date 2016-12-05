package com.zokbet.betdd.app;

import android.app.Application;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/5/20.
 */
public class ZobetApplication extends Application {

    private static final String DB_NAME = "zobet";
    //private static final String DB_PATH = "/sdcard/xx";
    private static final int DB_VERSION = 1;

    private DbManager.DaoConfig daoConfig;

    public DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);//Xutils初始化
        x.Ext.setDebug(true);
        daoConfig = new DbManager.DaoConfig()
                .setDbName(DB_NAME)//创建数据库的名称
                //.setDbDir(new File(DB_PATH))//如果不设置，那么数据库默认存储在/data/data/你的应用程序/database/xxx.db下
                .setDbVersion(DB_VERSION)//数据库版本号
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);//....
                    }
                });//数据库更新操作

    }
}
