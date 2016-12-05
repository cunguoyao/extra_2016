package com.keyun.pan.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.keyun.pan.app.BaseApplication;
import com.keyun.pan.app.Const;
import com.keyun.pan.app.Urls;
import com.keyun.pan.data.DownFileItem;
import com.keyun.pan.data.FileItem;
import com.keyun.pan.utils.FileService;
import com.keyun.pan.utils.SharedPreferencesUtils;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cunguoyao on 2016/7/14.
 */
public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getSimpleName();
    //存储所有的startId
    private List<Integer> allStartIdList = new ArrayList<>();
    //存储已经下载完成的startId
    private List<Integer> finishedStartIdList = new ArrayList<>();
    private Map<String, Callback.Cancelable> requestMap = new HashMap<>();

    protected DbManager db;
    protected Urls Urls;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            DownFileItem fileItem = (DownFileItem)msg.obj;
            switch (msg.what) {
                case DownFileItem.STATE_DOWN_NONE:
                    fileItem.setDownloadState(DownFileItem.STATE_DOWN_NONE);
                    fileItem.setClickDownTime(System.currentTimeMillis());
                    updateDownFileItem(fileItem);
                    break;
                case DownFileItem.STATE_DOWN_ING:
                    fileItem.setDownloadState(DownFileItem.STATE_DOWN_ING);
                    updateDownFileItem(fileItem);
                    break;
                case DownFileItem.STATE_DOWN_PAUSE:
                    fileItem.setDownloadState(DownFileItem.STATE_DOWN_PAUSE);
                    updateDownFileItem(fileItem);
                    break;
                case DownFileItem.STATE_DOWN_CANCEL:
                    fileItem.setDownloadState(DownFileItem.STATE_DOWN_CANCEL);
                    updateDownFileItem(fileItem);
                    break;
                case DownFileItem.STATE_DOWN_DONE:
                    fileItem.setDownloadState(DownFileItem.STATE_DOWN_DONE);
                    updateDownFileItem(fileItem);
                    break;
            }
        }
    };

    private void updateDownFileItem(DownFileItem downFileItem) {
        if(db == null) {
            db = x.getDb(((BaseApplication)getApplicationContext()).getDaoConfig());
        }
        try {
            db.saveOrUpdate(downFileItem);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void cancelDownloadFile(DownFileItem downFileItem) {
        if(downFileItem == null) return;
        Callback.Cancelable request = null;
        for (String key : requestMap.keySet()) {
            if(downFileItem.getHash().equals(key)) {
                request = requestMap.get(key);
            }
        }
        if(request != null && !request.isCancelled()) {
            request.cancel();
        }
        Message msg = handler.obtainMessage();
        msg.what = DownFileItem.STATE_DOWN_CANCEL;
        msg.obj = downFileItem;
        handler.sendMessage(msg);
    }

    private void downloadFile(final DownFileItem downFileItem) {
        if(requestMap.containsKey(downFileItem.getHash())) {
            requestMap.remove(downFileItem.getHash());
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("DownloadToken", SharedPreferencesUtils.fetchByKey(this, "FileService", "UploadToken"));
        params.put("FileMD5", downFileItem.getHash());
        params.put("FileDownloadOffset", String.valueOf(0));
        File fileDir = new File(Const.getDownloadDir() + "/" + downFileItem.getHash() + "/");
        if(!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(fileDir, downFileItem.getFileName());
        final Callback.Cancelable request = FileService.downLoadFile(Urls.downloadFile(), params, file.getAbsolutePath(), new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
                LogUtil.e("-----onWaiting");
            }

            @Override
            public void onStarted() {
                Message msg = handler.obtainMessage();
                msg.what = DownFileItem.STATE_DOWN_NONE;
                downFileItem.setDownloadSize(0);
                msg.obj = downFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Message msg = handler.obtainMessage();
                msg.what = DownFileItem.STATE_DOWN_ING;
                downFileItem.setDownloadSize(current);
                msg.obj = downFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onSuccess(File result) {
                LogUtil.e("-----onSuccess");
                downFileItem.setDownloadPath(result.getAbsolutePath());
                Message msg = handler.obtainMessage();
                msg.what = DownFileItem.STATE_DOWN_DONE;
                msg.obj = downFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("-----onError");
                Message msg = handler.obtainMessage();
                msg.what = DownFileItem.STATE_DOWN_NONE;
                msg.obj = downFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("-----onCancelled");
                Message msg = handler.obtainMessage();
                msg.what = DownFileItem.STATE_DOWN_CANCEL;
                msg.obj = downFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onFinished() {
                LogUtil.e("-----onFinished");
                if(requestMap.containsKey(downFileItem.getHash())) {
                    requestMap.remove(downFileItem.getHash());
                }
            }
        });
        requestMap.put(downFileItem.getHash(), request);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("DemoLog", "DownloadService -> onCreate");
        db = x.getDb(((BaseApplication)getApplicationContext()).getDaoConfig());
        Urls = new Urls(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        allStartIdList.add(startId);
        Log.i("DemoLog", "DownloadService -> onStartCommand, startId: " + startId);
        int operate = intent.getIntExtra("op", 0);//op 0 下载 1取消 2重新下载
        if(operate == 1) {
            DownFileItem downFileItem = (DownFileItem) intent.getSerializableExtra("FileItem");
            cancelDownloadFile(downFileItem);
        }else if(operate == 2) {
            DownFileItem downFileItem = (DownFileItem) intent.getSerializableExtra("FileItem");
            downloadFile(downFileItem);
        }else {
            FileItem fileItem = (FileItem) intent.getSerializableExtra("FileItem");
            DownFileItem downFileItem = DownFileItem.copyFiled(fileItem);
            downloadFile(downFileItem);
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("DemoLog", "DownloadService -> onDestroy");
    }
}
