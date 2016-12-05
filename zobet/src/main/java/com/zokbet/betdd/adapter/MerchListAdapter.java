package com.zokbet.betdd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import com.zokbet.betdd.R;
import com.zokbet.betdd.data.Merchant;

import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/5/18.
 */
public class MerchListAdapter extends BaseAdapter {

    private Context context;
    private List<Merchant> merchantList;
    private ImageOptions imageOptions;

    public MerchListAdapter (Context context, List<Merchant> data) {
        this.context = context;
        this.merchantList = data;
        this.imageOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(160), DensityUtil.dip2px(160))//图片大小
                //.setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.bg_loading_poi_list)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.bg_loading_poi_list)//加载失败后默认显示图片
                .build();
    }

    @Override
    public int getCount() {
        return merchantList== null ? 0 : merchantList.size();
    }

    @Override
    public Merchant getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.adapter_merch_list_item, null);
            viewHolder.face = (ImageView) convertView.findViewById(R.id.ri_merchantImage);
            viewHolder.name = (TextView) convertView.findViewById(R.id.ri_merchantName);
            viewHolder.average = (TextView) convertView.findViewById(R.id.ri_average);
            viewHolder.cate_area = (TextView) convertView.findViewById(R.id.ri_cate_area);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.ri_distance);
            viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ri_score);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Merchant item = getItem(position);
        if (null != item){
            viewHolder.name.setText(item.getName());
            viewHolder.average.setText("人均￥" + item.getAverage());
            //viewHolder.ratingBar.setMax(100);
            //viewHolder.ratingBar.setStepSize(0.1f);
            //viewHolder.ratingBar.setProgress((int)(20 * item.getStar()));
            viewHolder.distance.setVisibility(View.GONE);
            viewHolder.cate_area.setText(item.getCate().name + " " + item.getMerchantArea().area);
            x.image().bind(viewHolder.face, item.getFace(), imageOptions);
            viewHolder.ratingBar.setRating((float) (item.getStar() / 20.0));
        }
        return convertView;
    }

    class ViewHolder {
        ImageView face;
        TextView name;
        TextView average;
        TextView cate_area;
        TextView distance;
        RatingBar ratingBar;
    }
}
