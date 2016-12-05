package com.keyun.pan.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cunguoyao on 2016/7/10.
 */
public class MyUtils {

    public static Cursor getCursorFilterByType(Context context, String[] extension) {
        //从外存中获取
        Uri fileUri = MediaStore.Files.getContentUri("external");
        //筛选列，这里只筛选了：文件路径和不含后缀的文件名
        String[] projection=new String[]{
                MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.DATA
        };
        //构造筛选语句
        String selection="";
        for(int i=0;i<extension.length;i++)
        {
            if(i!=0)
            {
                selection=selection+" OR ";
            }
            selection=selection+ MediaStore.Files.FileColumns.DATA+" LIKE '%"+extension[i]+"'";
        }
        //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
        String sortOrder= MediaStore.Files.FileColumns.DATE_MODIFIED;
        //获取内容解析器对象
        ContentResolver resolver=context.getContentResolver();
        //获取游标
        Cursor cursor=resolver.query(fileUri, projection, selection, null, sortOrder);
        return cursor;
        /*if(cursor==null)
            return filterTypeFileList;
        //游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
        if(cursor.moveToLast())
        {
            do{
                //输出文件的完整路径
                filterTypeFileList.add(new FilterTypeFile(cursor.getString(1), cursor.getString(0)));
            }while(cursor.moveToPrevious());
        }
        cursor.close();
        return filterTypeFileList;*/
    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.2f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format("%.2f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format("%.2f KB", f);
        } else
            return String.format("%d B", size);
    }

    public static String getIpAddress(Context context) {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return intToIp(wifiInfo.getIpAddress());
    }

    private static String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    public static Intent openFile(String filePath){

        File file = new File(filePath);
        if(!file.exists()) return null;
        /* 取得扩展名 */
        String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if(end.equalsIgnoreCase("m4a")||end.equalsIgnoreCase("mp3")||end.equalsIgnoreCase("mid")||
                end.equalsIgnoreCase("xmf")||end.equalsIgnoreCase("ogg")||end.equalsIgnoreCase("wav")){
            return getAudioFileIntent(filePath);
        }else if(end.equalsIgnoreCase("3gp")||end.equalsIgnoreCase("mp4")){
            return getAudioFileIntent(filePath);
        }else if(end.equalsIgnoreCase("jpg")||end.equalsIgnoreCase("gif")||end.equalsIgnoreCase("png")||
                end.equalsIgnoreCase("jpeg")||end.equalsIgnoreCase("bmp")){
            return getImageFileIntent(filePath);
        }else if(end.equalsIgnoreCase("apk")){
            return getApkFileIntent(filePath);
        }else if(end.equalsIgnoreCase("ppt")){
            return getPptFileIntent(filePath);
        }else if(end.equalsIgnoreCase("xls")){
            return getExcelFileIntent(filePath);
        }else if(end.equalsIgnoreCase("doc")){
            return getWordFileIntent(filePath);
        }else if(end.equalsIgnoreCase("pdf")){
            return getPdfFileIntent(filePath);
        }else if(end.equalsIgnoreCase("chm")){
            return getChmFileIntent(filePath);
        }else if(end.equalsIgnoreCase("txt")){
            return getTextFileIntent(filePath,false);
        }else if(end.equalsIgnoreCase("htm") || end.equalsIgnoreCase("html")){
            return getTextFileIntent(filePath,false);
        }else{
            return getAllIntent(filePath);
        }
    }

    //Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent( String param ) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri,"*/*");
        return intent;
    }
    //Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent( String param ) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri,"application/vnd.android.package-archive");
        return intent;
    }

    //Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent( String param ) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    //Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    //Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent( String param ){

        Uri uri = Uri.parse(param ).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param ).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    //Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent( String param ) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    //Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    //Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent( String param, boolean paramBoolean){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean){
            Uri uri1 = Uri.parse(param );
            intent.setDataAndType(uri1, "text/plain");
        }else{
            Uri uri2 = Uri.fromFile(new File(param ));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }
    //Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    public static String cutDate(String dateStr) {
        if(dateStr != null && dateStr.contains(" ")) {
            return dateStr.substring(0, dateStr.indexOf(" "));
        }
        return dateStr;
    }

    public static String format(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }


    //判断手机格式是否正确
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);

        return m.matches();
    }
    //判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }
    //判断是否全是数字
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
