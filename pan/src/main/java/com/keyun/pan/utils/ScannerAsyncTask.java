package com.keyun.pan.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.keyun.pan.data.FilterTypeFile;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/9/27.
 */
public class ScannerAsyncTask extends AsyncTask<Void,Integer,List<FilterTypeFile>> {

    private List<FilterTypeFile> fileList = new ArrayList<FilterTypeFile>();
    private List<String> sufList;
    private ScannerFinishListener listener;

    public ScannerAsyncTask(String[] filterSuf, ScannerFinishListener listener) {
        this.listener = listener;
        sufList = new ArrayList<>();
        if(filterSuf != null && filterSuf.length > 0) {
            for(String suf : filterSuf) {
                sufList.add(suf.toLowerCase());
                sufList.add(suf.toUpperCase());
            }
        }
    }

    @Override
    protected List<FilterTypeFile> doInBackground(Void... params) {
        fileList = getVideoFile(fileList, Environment.getExternalStorageDirectory());
        fileList = filterVideo(fileList);
        Log.i("tga","最后的大小"+fileList.size());
        return fileList;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<FilterTypeFile> videoInfos) {
        listener.onFinish(videoInfos);
    }

    /**
     * 获取视频文件
     * @param list
     * @param file
     * @return
     */
    private List<FilterTypeFile> getVideoFile(final List<FilterTypeFile> list, File file) {
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if(sufList.contains(name)) {
                        FilterTypeFile video = new FilterTypeFile(file.getName(), file.getAbsolutePath());
                        Log.i("tga","name"+video.path);
                        list.add(video);
                        return true;
                    }
                    //判断是不是目录
                } else if (file.isDirectory()) {
                    getVideoFile(list, file);
                }
                return false;
            }
        });

        return list;
    }

    /**10M=10485760 b,小于10m的过滤掉
     * 过滤视频文件
     * @param videoInfos
     * @return
     */
    private List<FilterTypeFile> filterVideo(List<FilterTypeFile> videoInfos){
        List<FilterTypeFile> newVideos=new ArrayList<FilterTypeFile>();
        for(FilterTypeFile videoInfo:videoInfos){
            File f=new File(videoInfo.path);
            if(f.exists()&&f.isFile()/*&&f.length()>10485760*/){
                newVideos.add(videoInfo);
                Log.i("TGA","文件大小"+f.length());
            }else {
                Log.i("TGA","文件太小或者不存在");
            }
        }
        return newVideos;
    }

    public interface ScannerFinishListener {
        void onFinish(List<FilterTypeFile> videoInfos);
    }
}
