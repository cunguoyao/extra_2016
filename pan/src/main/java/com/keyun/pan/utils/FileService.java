package com.keyun.pan.utils;

/**
 * Created by cunguoyao on 2016/7/10.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.keyun.pan.app.Const;
import com.keyun.pan.app.Urls;
import com.keyun.pan.data.ChunksFile;
import com.keyun.pan.data.FileItem;
import com.keyun.pan.data.User;
import com.keyun.pan.service.UploadService;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.http.app.HttpRetryHandler;
import org.xutils.x;

/**
 * sdcard的存在于上下文无关
 *
 * @author piaodangdehun
 *
 */
public class FileService {

    public void fetchDownload(final Context context, String url, User user) {
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("UserToken", user.getToken());
        params.setCharset("gbk");
        for(KeyValue k : params.getQueryStringParams()) {
            LogUtil.e(k.key + ":" + k.value);
        }
        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("---fetchDownload onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if (json.optInt("ErrorCode") == 0) {
                            SharedPreferencesUtils.saveByKey(context, "FileService", "StorageApiUrl", json.optString("StorageApiUrl"));
                            SharedPreferencesUtils.saveByKey(context, "FileService", "UploadToken", json.optString("UploadToken"));
                        }else {
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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

    /**
     * 发送get请求
     * @param <T>
     */
    public static <T> Callback.Cancelable get(String url, Map<String,String> map, Callback.CommonCallback<T> callback){
        RequestParams params=new RequestParams(url);
        if(null!=map){
            for(Map.Entry<String, String> entry : map.entrySet()){
                params.addQueryStringParameter(entry.getKey(), entry.getValue());
            }
        }
        Callback.Cancelable cancelable = x.http().get(params, callback);
        return cancelable;
    }

    /**
     * 发送post请求
     * @param <T>
     */
    public static <T> Callback.Cancelable post(String url, Map<String,Object> map, Callback.CommonCallback<T> callback){
        RequestParams params=new RequestParams(url);
        if(null!=map){
            for(Map.Entry<String, Object> entry : map.entrySet()){
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        Callback.Cancelable cancelable = x.http().post(params, callback);
        return cancelable;
    }


    /**
     * 上传文件
     * @param <T>
     */
    public static <T> Callback.Cancelable upLoadFile(String url, Map<String,Object> map, String charset, Callback.CommonCallback<T> callback){
        RequestParams params=new RequestParams(url);
        if(null!=map){
            for(Map.Entry<String, Object> entry : map.entrySet()){
                LogUtil.e(entry.getKey() + ":" + entry.getValue());
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        params.setCharset(charset);
        params.setMultipart(true);
        Callback.Cancelable cancelable = x.http().post(params, callback);
        return cancelable;
    }

    /**
     * 下载文件
     * @param <T>
     */
    public static <T> Callback.Cancelable downLoadFile(String url, Map<String,Object> map, String filepath, Callback.CommonCallback<T> callback){
        RequestParams params=new RequestParams(url);
        if(null!=map){
            for(Map.Entry<String, Object> entry : map.entrySet()){
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        //设置断点续传
        params.setAutoResume(true);
        params.setAutoRename(false);
        params.setHttpRetryHandler(new HttpRetryHandler());
        params.setSaveFilePath(filepath);
        Callback.Cancelable cancelable = x.http().get(params, callback);
        return cancelable;
    }

    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     * @param string
     * @return
     */
    public static String encodeGB(String string) {
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

    /*
     * 存放在sdcard的根目录
     */
    public boolean saveFileToSdcardRoot(String fileName, byte[] data) {
        boolean flag = false;
        /*
         * 先判断sdcard的状态，是否存在
         */
        String state = Environment.getExternalStorageState();
        FileOutputStream outputStream = null;
        File rootFile = Environment.getExternalStorageDirectory(); // 获得sdcard的根路径
        /*
         * 表示sdcard挂载在手机上，并且可以读写
         */
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(rootFile, fileName);
            try {
                outputStream = new FileOutputStream(file);
                try {
                    outputStream.write(data, 0, data.length);
                    flag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return flag;
    }

    /*
     * 存放在sdcard下自定义的目录
     */
    public boolean saveFileToSdcardDir(String fileName, byte[] data) {
        boolean flag = false;
        /*
         * 先判断sdcard的状态，是否存在
         */
        String state = Environment.getExternalStorageState();
        FileOutputStream outputStream = null;
        File rootFile = Environment.getExternalStorageDirectory(); // 获得sdcard的根路径
        /*
         * 表示sdcard挂载在手机上，并且可以读写
         */
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(rootFile.getAbsoluteFile() + "/txt");
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                outputStream = new FileOutputStream(new File(file, fileName));
                try {
                    outputStream.write(data, 0, data.length);
                    flag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return flag;
    }

    /*
     * 用于读取sdcard的数据
     */
    public String readContextFromSdcard(String fileName) {

        String state = Environment.getExternalStorageState();
        File rooFile = Environment.getExternalStorageDirectory(); // 获得sdcard的目录

        FileInputStream inputStream = null;// 用于度取数据的流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // 用于存放独处的数据

        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(rooFile.getAbsoluteFile() + "/txt/");// 在sdcard目录下创建一个txt目录
            File file2 = new File(file, fileName);
            int len = 0;
            byte[] data = new byte[1024];
            if (file2.exists()) {
                try {
                    inputStream = new FileInputStream(file2);
                    try {
                        while ((len = inputStream.read(data)) != -1) {
                            outputStream.write(data, 0, data.length);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new String(outputStream.toByteArray());
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 对文件进行分类的保存到固定的文件中去
     *
     * @param fileName
     * @param data
     */
    public void saveFileToSdcardBySuff(String fileName, byte[] data) {
        // File file = Environment.getExternalStoragePublicDirectory();
        // 保存文件的目录
        File file = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            /*
             * 将不同的文件放入到不同的类别中
             */
            if (fileName.endsWith(".mp3")) {
                file = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".png")
            || fileName.endsWith(".gif")) {
                file = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            } else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi")
            || fileName.endsWith(".3gp")) {
                file = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            } else {
                file = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            }
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(new File(file, fileName));
                try {
                    outputStream.write(data, 0, data.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /*
     * 删除一个文件
     */
    public boolean deleteFileFromSdcard(String folder, String fileName) {
        boolean flag = false;
        File file = Environment.getExternalStorageDirectory();
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File exitFile = new File(file.getAbsoluteFile() + "/" + folder);
            if (exitFile.exists()) {
                exitFile.delete();
            }
        }
        return flag;
    }

    public static String getMD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] btInput = s.getBytes("utf-8");
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取单个文件的MD5值！
     * @param file
     * @return
     */
    public static String[] getFileUploadParams(File file) {
        String md5, isZip, fileSize;
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        md5 = bigInt.toString(16);
        String fileName = file.getName();
        String fileSuf = fileName.substring(fileName.lastIndexOf("."));
        if(".zip".equalsIgnoreCase(fileSuf) || ".rar".equalsIgnoreCase(fileSuf) || ".gz".equalsIgnoreCase(fileSuf)) {
            isZip = "1";
        }else {
            isZip = "0";
        }
        fileSize = String.valueOf(file.length());
        return new String[]{md5, isZip, fileSize};
    }

    /**
     * @param sourceFile 待分割文件
     * @throws Exception
     */
    public static ArrayList<ChunksFile> splitFile(File sourceFile) throws Exception {
        ArrayList<ChunksFile> files = new ArrayList<>();

        InputStream ips = new FileInputStream(sourceFile);//找到读取源文件并获取输入流
        File targetFile = null;
        OutputStream ops = null;
        int partNumber = 0;
        byte[] buffer = new byte[Const.trunkSize];//开辟缓存空间
        int tempLength = 0;
        MessageDigest digest = MessageDigest.getInstance("MD5");
        String targetFileDir = Const.getDownloadDir() + File.separator + "cache" + File.separator;//分割后的文件目录路径
        File dir = new File(targetFileDir);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        while((tempLength = ips.read(buffer,0,Const.trunkSize)) != -1) {
            digest.update(buffer, 0, Const.trunkSize);
            targetFile = new File(targetFileDir, "part_" + partNumber);
            if(!targetFile.exists()) {
                targetFile.createNewFile();
            }
            ops = new FileOutputStream(targetFile);//分割后文件
            ops.write(buffer,0,tempLength);//将信息写入碎片文件
            ops.close();//关闭碎片文件
            BigInteger bigInt = new BigInteger(1, digest.digest());
            ChunksFile file = new ChunksFile(String.valueOf(partNumber), targetFile, bigInt.toString(16), null);
            files.add(file);
            partNumber++;
        }
        ips.close();//关闭源文件流
        return files;
    }

}