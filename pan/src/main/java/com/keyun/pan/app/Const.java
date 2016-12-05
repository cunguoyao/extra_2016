package com.keyun.pan.app;

import android.content.Context;
import android.os.Environment;

import org.xutils.common.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by cunguoyao on 2016/5/17.
 */
public class Const {

    public static final int PAGE_SIZE_10 = 10;
    public static final String DOWNLOAD_DIR = "/TS_Cloud";
    public static final int trunkSize = 1024 * 1024;

    /**
     * 获取扩展SD卡存储目录
     *
     * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录
     * 否则：返回内置SD卡目录
     *
     * @return
     */
    public static String getDownloadDir () {
        /*
         * 先判断sdcard的状态，是否存在
         */
        String state = Environment.getExternalStorageState();
        File rootFile = Environment.getExternalStorageDirectory(); // 获得sdcard的根路径
        /*
         * 表示sdcard挂载在手机上，并且可以读写
         */
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return rootFile.getAbsolutePath() + DOWNLOAD_DIR;
        }
        return null;
    }

}
