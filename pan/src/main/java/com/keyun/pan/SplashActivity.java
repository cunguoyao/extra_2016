package com.keyun.pan;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.keyun.pan.activity.BaseActivity;
import com.keyun.pan.activity.GuideActivity;
import com.keyun.pan.activity.LoginActivity;
import com.keyun.pan.activity.MainActivity;
import com.keyun.pan.utils.DownloadManagerPro;
import com.keyun.pan.utils.SharedPreferencesUtils;
import com.keyun.pan.widget.CommonProgressDialog;
import com.keyun.pan.widget.CustomDialog;

import org.json.JSONException;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_splash)
public class SplashActivity extends BaseActivity {

    @ViewInject(R.id.image)
    private ImageView imageView;

    private long startTime;
    private DownloadManager downloadManager;
    private DownloadManagerPro downloadManagerPro;
    private long lastDownloadId = 0;
    private CommonProgressDialog progressDialog;

    private int newVersionCode;
    private int forceUpdate;
    private String newVersionDesc;
    private String newVersionUrl;

    private String apkPath;
    private String apkFile;
    private Uri contentUri;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                CustomDialog.Builder builder = new CustomDialog.Builder(SplashActivity.this);
                builder.setTitle("检测到新版本");
                builder.setMessage(newVersionDesc);
                builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        /*Intent updateIntent =new Intent(SplashActivity.this, UpdateAppService.class);
                        startService(updateIntent);
                        sendEmptyMessageDelayed(1, 500);*/
                        apkPath = Environment.getExternalStorageDirectory().getPath() + "/zobet/";
                        File file = new File(apkPath);
                        if(!file.exists()) {
                            file.mkdir();
                        }
                        apkFile = "zobet" + System.currentTimeMillis() + ".apk";
                        contentUri = Uri.fromFile(new File(apkPath, apkFile));
                        startDown(newVersionUrl);
                    }
                });
                if(forceUpdate == 1) {
                    builder.setNegativeButton(null, null);
                }else {
                    builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            sendEmptyMessageDelayed(1, 500);
                        }
                    });
                }
                CustomDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }else if(msg.what == 1){
                String guide = SharedPreferencesUtils.fetchByKey(SplashActivity.this, "SERVER", "guide");
                if(null == guide || "".equals(guide)) {
                    Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    if (getDefaultUser() == null) {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }
    };

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
    private Runnable command = new Runnable() {
        @Override
        public void run() {
            updateView();
        }
    };

    private Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    int status = (Integer)msg.obj;
                    if (isDownloading(status)) {
                        LogUtil.e("-----------------" + msg.arg1 + "/" + msg.arg2);
                        if (msg.arg2 < 0) {
                            if(progressDialog != null && !progressDialog.isShowing()) {
                                progressDialog.show();
                            }
                            progressDialog.setProgress(0);
                        } else {
                            progressDialog.setMax(msg.arg2);
                            progressDialog.setProgress(msg.arg1);
                        }
                    } else {
                        scheduledExecutorService.shutdownNow();
                        if (status == DownloadManager.STATUS_FAILED) {
                            if(progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(SplashActivity.this, "新版本下载失败", Toast.LENGTH_LONG).show();
                            if(forceUpdate != 1) {
                                handler.sendEmptyMessage(1);
                            }else {
                                finish();
                            }
                        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            if(progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Intent intent1 = new Intent();
                            intent1.setAction(Intent.ACTION_VIEW);
                            intent1.setDataAndType(contentUri, "application/vnd.android.package-archive");
                            startActivity(intent1);
                            finish();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        x.view().inject(this);

        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        downloadManagerPro = new DownloadManagerPro(downloadManager);
        progressDialog = new CommonProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        startTime = System.currentTimeMillis();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(2000);
        imageView.startAnimation(alphaAnimation);

        //fetchData();
        handler.sendEmptyMessageDelayed(1, 2000);
    }

    private void fetchData() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final int localVersion = packageInfo.versionCode;
        RequestParams params = new RequestParams(Urls.version());
        params.addBodyParameter("type", "android");
        params.addBodyParameter("version", String.valueOf(localVersion));
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(final String result) {
                LogUtil.e("---onSuccess-result----" + result);
                long now = System.currentTimeMillis();
                if(now - startTime < 4000) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resolveFetchDataSuccess(localVersion, result);
                        }
                    }, (4000 - (now - startTime)));
                }else {
                    resolveFetchDataSuccess(localVersion, result);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                long now = System.currentTimeMillis();
                if(now - startTime < 4000) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(1);
                        }
                    }, (4000 - (now - startTime)));
                }else {
                    handler.sendEmptyMessage(1);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                long now = System.currentTimeMillis();
                if(now - startTime < 4000) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(1);
                        }
                    }, (4000 - (now - startTime)));
                }else {
                    handler.sendEmptyMessage(1);
                }
            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void resolveFetchDataSuccess(int localVersion, String result) {
        if(result != null) {
            try {
                org.json.JSONObject json = new org.json.JSONObject(result);
                if (json.optInt("code") == 100) {
                    org.json.JSONObject jsonObj = json.optJSONObject("data");
                    newVersionCode = jsonObj.optInt("newVersionCode");
                    forceUpdate = jsonObj.optInt("force");
                    newVersionDesc = jsonObj.optString("newVersionDesc");
                    newVersionUrl = jsonObj.optString("newVersionUrl");
                    if(newVersionCode > localVersion) {
                        handler.sendEmptyMessage(0);
                    }else {
                        handler.sendEmptyMessage(1);
                    }
                }
            } catch (JSONException e) {
                handler.sendEmptyMessage(1);
            }
        }else {
            handler.sendEmptyMessage(1);
        }
    }

    private void startDown(String url) {
        //开始下载
        Uri resource = Uri.parse(encodeGB(url));
        DownloadManager.Request request = new DownloadManager.Request(resource);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        //设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        //在通知栏中显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        //request.setShowRunningNotification(true);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹
        request.setDestinationUri(contentUri);
        request.setTitle("下载新版本");
        //long id = downloadManager.enqueue(request);
        lastDownloadId = downloadManager.enqueue(request);
        //保存id
        //prefs.edit().putLong(DL_ID, id).commit();

        scheduledExecutorService.scheduleAtFixedRate(command, 0, 3, TimeUnit.SECONDS);
    }
    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     * @param string
     * @return
     */
    public String encodeGB(String string) {
        //转换中文编码
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0]+"/"+split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");//处理空格
        return split[0];
    }

    public void updateView() {
        int[] bytesAndStatus = downloadManagerPro.getBytesAndStatus(lastDownloadId);
        downloadHandler.sendMessage(handler.obtainMessage(0, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]));
    }

    public static boolean isDownloading(int downloadManagerStatus) {
        return downloadManagerStatus == DownloadManager.STATUS_RUNNING
                || downloadManagerStatus == DownloadManager.STATUS_PAUSED
                || downloadManagerStatus == DownloadManager.STATUS_PENDING;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
    }
}
