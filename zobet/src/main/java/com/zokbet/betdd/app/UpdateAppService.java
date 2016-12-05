package com.zokbet.betdd.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import com.zokbet.betdd.R;

/**
 * Created by cunguoyao on 2016/6/1.
 */
public class UpdateAppService extends Service{

    private Context context;
    private Notification notification;
    private NotificationManager nManager;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        context = getApplicationContext();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        CreateInform();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    //创建通知
    public void CreateInform() {
        //定义一个PendingIntent，当用户点击通知时，跳转到某个Activity(也可以发送广播等)
        /*Intent intent = new Intent(context,MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);*/
        //创建一个通知
        notification = new Notification(R.drawable.ic_launcher, "开始下载~~", System.currentTimeMillis());
        //notification.setLatestEventInfo(context, "正在下载传宇通讯录~", "点击查看详细内容", pendingIntent);
        //用NotificationManager的notify方法通知用户生成标题栏消息通知
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nManager.notify(100, notification);//id是应用中通知的唯一标识
        //如果拥有相同id的通知已经被提交而且没有被移除，该方法会用更新的信息来替换之前的通知。
        new Thread(new updateRunnable()).start();//这个是下载的重点，是下载的过程
    }
    class updateRunnable implements Runnable{
        int downnum = 0;//已下载的大小
        int downcount= 0;//下载百分比
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                DownLoadApp("http://10.0.2.2:8888/android/XiaoJue.apk");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        public void DownLoadApp(String urlString) throws Exception{
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int length = urlConnection.getContentLength();
            InputStream inputStream = urlConnection.getInputStream();
            OutputStream outputStream = new FileOutputStream(getFile());
//          OutputStream outputStream = new FileOutputStream(new File("/mnt/sdcard/App/hello.apk"));
            byte buffer[] = new byte[1024*3];
            int readsize = 0;
            while((readsize = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, readsize);
                downnum += readsize;
                if((downcount == 0)||(int) (downnum*100/length)-1>downcount){
                    downcount += 1;
                    //notification.setLatestEventInfo(context, "正在下载传宇通讯录~", "已下载了"+(int)downnum*100/length+"%", pendingIntent);
                    nManager.notify(100, notification);
                }
                if (downnum==length) {
                    //notification.setLatestEventInfo(context, "已下载完成传宇通讯录~", "点击安装", pendingIntent);
                    nManager.notify(100, notification);
                }
            }
            inputStream.close();
            outputStream.close();
        }
        //获取文件的保存路径
        public File getFile() throws Exception{
            String SavePath = getSDCardPath() + "/App";
            File path = new File(SavePath);
            File file = new File(SavePath + "/XiaoJue.apk");
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        }
        //获取SDCard的目录路径功能
        private String getSDCardPath() {
            File sdcardDir = null;
            // 判断SDCard是否存在
            boolean sdcardExist = Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED);
            if (sdcardExist) {
                sdcardDir = Environment.getExternalStorageDirectory();
            }
            return sdcardDir.toString();
        }
    }
}
