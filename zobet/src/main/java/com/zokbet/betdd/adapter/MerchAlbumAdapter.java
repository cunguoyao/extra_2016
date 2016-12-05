package com.zokbet.betdd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zokbet.betdd.R;
import com.zokbet.betdd.data.MerchantPhoto;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by cunguoyao on 2016/5/18.
 */
public class MerchAlbumAdapter extends BaseAdapter {

    private Context context;
    private List<MerchantPhoto> merchantList;
    private ImageOptions imageOptions;

    public MerchAlbumAdapter(Context context, List<MerchantPhoto> data) {
        this.context = context;
        this.merchantList = data;
        this.imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(160), DensityUtil.dip2px(160))//图片大小
                .setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.list_thumbnail_none_m)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.list_thumbnail_none_m)//加载失败后默认显示图片
                .build();
    }

    @Override
    public int getCount() {
        return merchantList== null ? 0 : merchantList.size();
    }

    @Override
    public MerchantPhoto getItem(int position) {
        return merchantList== null ? null : merchantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.adapter_mechant_album_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MerchantPhoto item = getItem(position);
        if (null != item){
            x.image().bind(viewHolder.imageView, item.url, imageOptions);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
    }
}
