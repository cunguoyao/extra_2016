package com.keyun.pan.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.keyun.pan.R;

/**
 * Created by cunguoyao on 2016/9/21.
 */
public class MusicListAdapter extends BaseAdapter{
    private Context myCon;
    private Cursor myCur;
    private int pos=-1;

    public MusicListAdapter(Context con,Cursor cur){
        this.myCon = con;
        this.myCur = cur;
    }

    @Override
    public int getCount() {

        return this.myCur.getCount();
    }

    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(myCon).inflate(R.layout.item_file, null);
        myCur.moveToPosition(position);
        TextView videoTitle = (TextView)convertView.findViewById(R.id.tv_file_name);
        videoTitle.setText(myCur.getString(0) + "." + myCur.getString(1));
        /*if (myCur.getString(0).length()>24){
            try {
                String musicTitle = bSubstring(myCur.getString(0).trim(),24);
                videoTitle.setText(musicTitle);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }else {
            videoTitle.setText(myCur.getString(0).trim());
        }*/
        /*TextView videoArtist = (TextView)convertView.findViewById(R.id.musicartist);
        if (myCur.getString(2).equals("<unknown>")){
            videoArtist.setText("未知艺术家");
        }else{
            videoArtist.setText(myCur.getString(2));
        }
        TextView videoTime = (TextView)convertView.findViewById(R.id.musictime);
        videoTime.setText(toTime(myCur.getInt(1)));*/
        ImageView videoItem = (ImageView)convertView.findViewById(R.id.iv_file_icon);
        videoItem.setImageResource(R.drawable.netdisk_category_audio_normal);
        return convertView;
    }

    /*时间格式转换*/
    public String toTime(int time) {

        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    /*字符串裁剪*/
    public static String bSubstring(String s, int length) throws Exception
    {

        byte[] bytes = s.getBytes("Unicode");
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++)
        {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1)
            {
                n++; // 在UCS2第二个字节时n加1
            }
            else
            {
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0)
                {
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1)

        {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0)
                i = i - 1;
                // 该UCS2字符是字母或数字，则保留该字符
            else
                i = i + 1;
        }

        return new String(bytes, 0, i, "Unicode");
    }
}