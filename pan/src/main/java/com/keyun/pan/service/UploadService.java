package com.keyun.pan.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.keyun.pan.activity.LoginActivity;
import com.keyun.pan.activity.ScreenManager;
import com.keyun.pan.app.BaseApplication;
import com.keyun.pan.app.Const;
import com.keyun.pan.app.Urls;
import com.keyun.pan.data.ChunksFile;
import com.keyun.pan.data.UploadFileItem;
import com.keyun.pan.data.User;
import com.keyun.pan.utils.FileService;
import com.keyun.pan.utils.MyUtils;
import com.keyun.pan.utils.SharedPreferencesUtils;

import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cunguoyao on 2016/7/14.
 */
public class UploadService extends Service {

    private static final String TAG = UploadService.class.getSimpleName();
    //存储所有的startId
    private List<Integer> allStartIdList = new ArrayList<>();
    //存储已经下载完成的startId
    private List<Integer> finishedStartIdList = new ArrayList<>();
    private Map<String, Callback.Cancelable> requestMap = new HashMap<>();

    private User user;
    protected DbManager db;
    protected Urls Urls;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            UploadFileItem fileItem = (UploadFileItem)msg.obj;
            switch (msg.what) {
                case UploadFileItem.STATE_UPLOAD_NONE:
                    fileItem.setUploadState(UploadFileItem.STATE_UPLOAD_NONE);
                    fileItem.setClickUploadTime(System.currentTimeMillis());
                    updateUpFileItem(fileItem);
                    break;
                case UploadFileItem.STATE_UPLOAD_ING:
                    fileItem.setUploadState(UploadFileItem.STATE_UPLOAD_ING);
                    updateUpFileItem(fileItem);
                    break;
                case UploadFileItem.STATE_UPLOAD_PAUSE:
                    fileItem.setUploadState(UploadFileItem.STATE_UPLOAD_PAUSE);
                    updateUpFileItem(fileItem);
                    break;
                case UploadFileItem.STATE_UPLOAD_CANCEL:
                    fileItem.setUploadState(UploadFileItem.STATE_UPLOAD_CANCEL);
                    updateUpFileItem(fileItem);
                    break;
                case UploadFileItem.STATE_UPLOAD_DONE:
                    fileItem.setUploadState(UploadFileItem.STATE_UPLOAD_DONE);
                    updateUpFileItem(fileItem);
                    break;
            }
        }
    };

    private void updateUpFileItem(UploadFileItem uploadFileItem) {
        if(db == null) {
            db = x.getDb(((BaseApplication)getApplicationContext()).getDaoConfig());
        }
        try {
            db.saveOrUpdate(uploadFileItem);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void cancelUploadFile(UploadFileItem uploadFileItem) {
        if(uploadFileItem == null) return;
        Callback.Cancelable request = null;
        for (String key : requestMap.keySet()) {
            if(uploadFileItem.getHash().equals(key)) {
                request = requestMap.get(key);
            }
        }
        if(request != null && !request.isCancelled()) {
            request.cancel();
        }
        Message msg = handler.obtainMessage();
        msg.what = UploadFileItem.STATE_UPLOAD_CANCEL;
        msg.obj = uploadFileItem;
        handler.sendMessage(msg);
    }

    /**
     * 上传文件主入口
     * @param cloudDirPath
     * @param file
     */
    private void uploadFile(final String cloudDirPath, final File file, final int t, String RepositoryID) {
        final UploadFileItem uploadFileItem = new UploadFileItem();
        uploadFileItem.setFile(file);
        uploadFileItem.setFileName(file.getName());
        uploadFileItem.setModified(MyUtils.format(new Date(), "yyyy-MM-dd"));
        uploadFileItem.setFileFormat(file.getName().substring(file.getName().lastIndexOf(".")));
        uploadFileItem.setSize(file.length());
        uploadFileItem.setUserName(user.getUserName());
        uploadFileItem.setT(t);
        if(t == 1) {
            uploadFileItem.setRepositoryID(RepositoryID);
        }
        String[] fileParams = FileService.getFileUploadParams(file);
        long fileSize = Long.valueOf(fileParams[2]);
        int trunk =  fileSize % Const.trunkSize == 0 ? (int)(fileSize / Const.trunkSize) : (int)(fileSize / Const.trunkSize) + 1;
        if(trunk < 2) {
            createFile(uploadFileItem, cloudDirPath, file, fileParams, false);
        }else {
            createFile(uploadFileItem, cloudDirPath, file, fileParams, true);
        }
    }

    private void createFile(final UploadFileItem uploadFileItem,
                            final String cloudDirPath,
                            final File file,
                            final String[] fileParams,
                            final boolean isLargeFile) {
        final StringBuffer sb = new StringBuffer();
        final ArrayList<ChunksFile> files = new ArrayList<ChunksFile>();
        if(isLargeFile) {
            try {
                files.addAll(FileService.splitFile(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
            sb.append(getLargeFileMd5Params(files));
        }else {
            sb.append(fileParams[0]);
        }
        RequestParams params = new RequestParams(Urls.createFile());
        params.addBodyParameter("UserToken", user.getToken());
        //String base64_path = new String(Base64.encode(currentDirPath.getBytes(), Base64.DEFAULT));
        //LogUtil.i("---base64_path:" + base64_path);
        if(uploadFileItem.getT() == 1) {
            params.addBodyParameter("RepositoryID", uploadFileItem.getRepositoryID());
        }
        params.addBodyParameter("FilePath", cloudDirPath + file.getName());
        params.addBodyParameter("FileSize", fileParams[2]);
        params.addBodyParameter("LastModifyTime", String.valueOf(System.currentTimeMillis()));
        params.addBodyParameter("FileMD5", sb.toString());
        params.addBodyParameter("FileType", "0");
        params.addBodyParameter("charset", "utf-8");
        params.setCharset("utf-8");
        for(KeyValue k : params.getQueryStringParams()) {
            LogUtil.e(k.key + ":" + k.value);
        }
        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if(json.optLong("ErrorCode") == 401 || json.optLong("ErrCode") == 401) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.putExtra(LoginActivity.AutoLogin, 1);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//主要是这行，否则会报错。
                            startActivity(intent);
                            ScreenManager.getScreenManager().popAllActivityExceptOne(LoginActivity.class);
                            stopSelf();
                        }else if (json.optLong("ErrCode") == 0) {
                            Toast.makeText(getApplicationContext(), "上传完成1", Toast.LENGTH_SHORT).show();
                        }else if(json.optLong("ErrCode") == 4294967292L) {
                            SharedPreferencesUtils.saveByKey(getApplicationContext(), "FileService", "StorageApiUrl", json.optString("StorageApiUrl"));
                            SharedPreferencesUtils.saveByKey(getApplicationContext(), "FileService", "UploadToken", json.optString("UploadToken"));
                            if(isLargeFile) {
                                uploadLargeFile(uploadFileItem, 0, sb.toString(), files, file, cloudDirPath);
                            }else {
                                uploadSmallFile(uploadFileItem, file, fileParams, cloudDirPath);
                            }
                        }else {
                            String msg = json.optString("ErrMsg", "上传失败");
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("---onCancelled-----" + "onCancelled");
            }

            @Override
            public void onFinished() {
                LogUtil.e("---onFinished-----" + "onFinished");
            }
        });
    }

    private void uploadSmallFile(final UploadFileItem uploadFileItem, final File file, final String[] fileParams, final String cloudDirPath) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("UploadToken", SharedPreferencesUtils.fetchByKey(this, "FileService", "UploadToken"));
        params.put("FileMD5", fileParams[0]);
        params.put("IsZipped", fileParams[1]);//0表示不是压缩文件，1表示是压缩文件；
        params.put("FileSize", fileParams[2]);
        params.put("upload_file", file);
        uploadFileItem.setFileId(fileParams[0]);
        uploadFileItem.setHash(fileParams[0]);
        final Callback.Cancelable request = FileService.upLoadFile(Urls.uploadFile(), params, "utf-8", new Callback.ProgressCallback<String>() {
            @Override
            public void onWaiting() {
                LogUtil.e("-----onWaiting--upload");
            }

            @Override
            public void onStarted() {
                LogUtil.e("-----onStarted--upload");
                Message msg = handler.obtainMessage();
                msg.what = UploadFileItem.STATE_UPLOAD_NONE;
                uploadFileItem.setUploadSize(0);
                msg.obj = uploadFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Message msg = handler.obtainMessage();
                msg.what = UploadFileItem.STATE_UPLOAD_ING;
                uploadFileItem.setUploadSize(current);
                msg.obj = uploadFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onSuccess(String result) {
                LogUtil.e("-----onSuccess--upload");
                try {
                    LogUtil.e("---onSuccess-result----" + result);
                    JSONObject json = new JSONObject(result);
                    if(json.optLong("ErrorCode") == 401 || json.optLong("ErrCode") == 401) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra(LoginActivity.AutoLogin, 1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//主要是这行，否则会报错。
                        startActivity(intent);
                        ScreenManager.getScreenManager().popAllActivityExceptOne(LoginActivity.class);
                        stopSelf();
                    }else if (json.optInt("ErrCode") == 0) {
                        finishCreateFile(uploadFileItem, cloudDirPath, file, fileParams[0]);
                    }
                }catch (Exception e) {
                    onError(e, true);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("-----onError--upload");
                Message msg = handler.obtainMessage();
                msg.what = UploadFileItem.STATE_UPLOAD_NONE;
                msg.obj = uploadFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("-----onCancelled--upload");
                Message msg = handler.obtainMessage();
                msg.what = UploadFileItem.STATE_UPLOAD_CANCEL;
                msg.obj = uploadFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onFinished() {
                LogUtil.e("-----onFinished--upload");
                if(requestMap.containsKey(uploadFileItem.getHash())) {
                    requestMap.remove(uploadFileItem.getHash());
                }
            }
        });
        requestMap.put(uploadFileItem.getHash(), request);
    }

    private void uploadLargeFile(final UploadFileItem uploadFileItem,
                                 final int currentChunkNo,
                                 final String originalFileMd5,
                                 final ArrayList<ChunksFile> files,
                                 final File originalFile,
                                 final String cloudDirPath) {
        if(currentChunkNo < 0 || currentChunkNo >= files.size()) {
            return;
        }
        if(currentChunkNo == 0) {
            SharedPreferencesUtils.saveByKey(getApplicationContext(), "SuccessChunks", originalFileMd5, "");
        }
        List<ChunksFile> ff = new ArrayList<>();
        if(files != null && files.size() > 0) {
            for(ChunksFile file : files) {
                int no = Integer.parseInt(file.getChunkNo());
                if(currentChunkNo <= no) {
                    ff.add(file);
                }
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<ff.size();i++) {
            stringBuffer.append(ff.get(i).getChunkNo());
            stringBuffer.append("-");
            stringBuffer.append(ff.get(i).getMd5());
            if(i != ff.size()-1) {
                stringBuffer.append(";");
            }
        }
        final ChunksFile file = files.get(currentChunkNo);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("UploadToken", SharedPreferencesUtils.fetchByKey(this, "FileService", "UploadToken"));
        params.put("TotalChunks", files.size());
        params.put("ExistChunks", stringBuffer.toString());
        params.put("UploadChunkNo", currentChunkNo);
        params.put("UploadChunkMD5", file.getMd5());
        params.put("FileMD5", originalFileMd5);
        params.put("IsZipped", "0");//0表示不是压缩文件，1表示是压缩文件；
        params.put("upload_chunk", file.getFile());
        uploadFileItem.setFileId(originalFileMd5);
        uploadFileItem.setHash(originalFileMd5);
        final Callback.Cancelable request = FileService.upLoadFile(Urls.uploadFileChunks(), params, "utf-8", new Callback.ProgressCallback<String>() {
            @Override
            public void onWaiting() {
                LogUtil.e("-----onWaiting--upload");
            }

            @Override
            public void onStarted() {
                LogUtil.e("-----onStarted--upload");
                Message msg = handler.obtainMessage();
                msg.what = UploadFileItem.STATE_UPLOAD_NONE;
                uploadFileItem.setUploadSize(currentChunkNo * Const.trunkSize);
                msg.obj = uploadFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Message msg = handler.obtainMessage();
                msg.what = UploadFileItem.STATE_UPLOAD_ING;
                uploadFileItem.setUploadSize(currentChunkNo * Const.trunkSize + current);
                msg.obj = uploadFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onSuccess(String result) {
                LogUtil.e("-----onSuccess--upload");
                try {
                    LogUtil.e("---onSuccess-result----" + result);
                    JSONObject json = new JSONObject(result);
                    if(json.optLong("ErrorCode") == 401 || json.optLong("ErrCode") == 401) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra(LoginActivity.AutoLogin, 1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//主要是这行，否则会报错。
                        startActivity(intent);
                        ScreenManager.getScreenManager().popAllActivityExceptOne(LoginActivity.class);
                        stopSelf();
                    }else if (json.optInt("ErrCode") == 0) {
                        String SuccChunks = json.optString("SuccChunks");
                        if(!TextUtils.isEmpty(SuccChunks)) {
                            String[] temp = SuccChunks.split(";");
                            String successChunks = temp[temp.length-1] + "-" + file.getMd5();
                            String savedChunks = SharedPreferencesUtils.fetchByKey(getApplicationContext(), "SuccessChunks", originalFileMd5);
                            if (!TextUtils.isEmpty(savedChunks)) {
                                successChunks = successChunks + ";" + savedChunks;
                            }
                            SharedPreferencesUtils.saveByKey(getApplicationContext(), "SuccessChunks", originalFileMd5, successChunks);
                            int nextChunkNo = currentChunkNo + 1;
                            if (nextChunkNo < files.size()) {
                                uploadLargeFile(uploadFileItem, nextChunkNo, originalFileMd5, files, originalFile, cloudDirPath);
                            } else {//完成下载
                                finishCreateFile(uploadFileItem, cloudDirPath, originalFile, originalFileMd5);
                            }
                        }
                    }
                }catch (Exception e) {
                    onError(e, true);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("-----onError--upload");
                Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                Message msg = handler.obtainMessage();
                msg.what = UploadFileItem.STATE_UPLOAD_NONE;
                msg.obj = uploadFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("-----onCancelled--upload");
                Message msg = handler.obtainMessage();
                msg.what = UploadFileItem.STATE_UPLOAD_CANCEL;
                msg.obj = uploadFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onFinished() {
                LogUtil.e("-----onFinished--upload");
                if(currentChunkNo == files.size() - 1 && requestMap.containsKey(uploadFileItem.getHash())) {
                    requestMap.remove(uploadFileItem.getHash());
                }
            }
        });
        requestMap.put(uploadFileItem.getHash(), request);
    }

    private void finishCreateFile(final UploadFileItem uploadFileItem, final String cloudDirPath, final File file, String md5) {
        RequestParams params = new RequestParams(Urls.createFile());
        params.addBodyParameter("UserToken", user.getToken());
        //String base64_path = new String(Base64.encode(currentDirPath.getBytes(), Base64.DEFAULT));
        //LogUtil.i("---base64_path:" + base64_path);
        String[] fileParams = FileService.getFileUploadParams(file);
        params.addBodyParameter("FilePath", cloudDirPath + file.getName());
        params.addBodyParameter("FileSize", fileParams[2]);
        params.addBodyParameter("LastModifyTime", String.valueOf(System.currentTimeMillis()));
        params.addBodyParameter("FileMD5", md5);
        params.addBodyParameter("FileType", "0");
        params.addBodyParameter("charset", "utf-8");
        params.setCharset("utf-8");
        for(KeyValue k : params.getQueryStringParams()) {
            LogUtil.e(k.key + ":" + k.value);
        }
        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if(json.optLong("ErrorCode") == 401 || json.optLong("ErrCode") == 401) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.putExtra(LoginActivity.AutoLogin, 1);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//主要是这行，否则会报错。
                            startActivity(intent);
                            ScreenManager.getScreenManager().popAllActivityExceptOne(LoginActivity.class);
                            stopSelf();
                        }else if (json.optLong("ErrCode") == 0) {
                            Toast.makeText(getApplicationContext(), "上传完成", Toast.LENGTH_SHORT).show();
                            uploadFileItem.setUploadPath(file.getAbsolutePath());
                            uploadFileItem.setUploadSize(uploadFileItem.getSize());
                            Message msg = handler.obtainMessage();
                            msg.what = UploadFileItem.STATE_UPLOAD_DONE;
                            msg.obj = uploadFileItem;
                            handler.sendMessage(msg);
                        }else if(json.optLong("ErrCode") == 4294967292L) {
                            Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                            Message msg = handler.obtainMessage();
                            msg.what = UploadFileItem.STATE_UPLOAD_NONE;
                            msg.obj = uploadFileItem;
                            handler.sendMessage(msg);
                        }else {
                            String message = json.optString("ErrMsg", "上传失败");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            Message msg = handler.obtainMessage();
                            msg.what = UploadFileItem.STATE_UPLOAD_NONE;
                            msg.obj = uploadFileItem;
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                Message msg = handler.obtainMessage();
                msg.what = UploadFileItem.STATE_UPLOAD_NONE;
                msg.obj = uploadFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("---onCancelled-----" + "onCancelled");
                Message msg = handler.obtainMessage();
                msg.what = UploadFileItem.STATE_UPLOAD_CANCEL;
                msg.obj = uploadFileItem;
                handler.sendMessage(msg);
            }

            @Override
            public void onFinished() {
                LogUtil.e("---onFinished-----" + "onFinished");
            }
        });
    }

    private String getLargeFileMd5Params(ArrayList<ChunksFile> files) {
        String md5 = "";
        if(files != null && files.size() > 0) {
            for(ChunksFile file : files) {
                LogUtil.e(file.getChunkNo() + "-" + file.getMd5());
                md5 = md5 + file.getMd5();
            }
        }
        return FileService.getMD5(md5);
    }

    protected User getDefaultUser() {
        try {
            user = db.selector(User.class).where("default_acc", "=", 1).findFirst();
        }catch (DbException e) {
        }
        return user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("DemoLog", "UploadService -> onCreate");
        db = x.getDb(((BaseApplication)getApplicationContext()).getDaoConfig());
        user = getDefaultUser();
        Urls = new Urls(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        allStartIdList.add(startId);
        String cloudDirPath = intent.getStringExtra("cloudDirPath");
        int t = intent.getIntExtra("t", 0);
        String RepositoryID = intent.getStringExtra("RepositoryID");
        File fileItem = (File) intent.getSerializableExtra("File");
        Log.i("DemoLog", "UploadService -> onStartCommand, startId: " + startId + ", file: " + fileItem.getName());
        //UploadFileItem downFileItem = UploadFileItem.copyFiled(fileItem);
        if("-1".equals(cloudDirPath)) {
            cloudDirPath = "";
        }
        cloudDirPath = cloudDirPath + "\\";
        int operate = intent.getIntExtra("op", 0);//op 0 上传 1取消 2重新上传
        if(operate == 1) {
            UploadFileItem downFileItem = (UploadFileItem) intent.getSerializableExtra("FileItem");
            cancelUploadFile(downFileItem);
        }else if(operate == 2) {
            uploadFile(cloudDirPath, fileItem, t, RepositoryID);
        }else {
            uploadFile(cloudDirPath, fileItem, t, RepositoryID);
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
        Log.i("DemoLog", "UploadService -> onDestroy");
    }
}
