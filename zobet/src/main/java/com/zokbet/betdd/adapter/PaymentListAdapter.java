package com.zokbet.betdd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zokbet.betdd.R;
import com.zokbet.betdd.data.Payment;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by cunguoyao on 2016/5/18.
 */
public class PaymentListAdapter extends BaseAdapter {

    private Context context;
    private List<Payment> merchantList;
    private ImageOptions imageOptions;

    public PaymentListAdapter(Context context, List<Payment> data) {
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
    public Payment getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.adapter_payment_list_item, null);
            viewHolder.face = (ImageView) convertView.findViewById(R.id.ri_merchantImage);
            viewHolder.name = (TextView) convertView.findViewById(R.id.ri_merchantName);
            viewHolder.payMoney = (TextView) convertView.findViewById(R.id.ri_money);
            viewHolder.payNote = (TextView) convertView.findViewById(R.id.ri_note);
            viewHolder.payDate = (TextView) convertView.findViewById(R.id.ri_date);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Payment item = getItem(position);
        if (null != item){
            viewHolder.name.setText(item.getMerchName());
            if(item.getType() == 10) {
                viewHolder.payMoney.setText("+" + item.getPayMoney());
                viewHolder.payMoney.setTextColor(context.getResources().getColor(R.color.normal_orange));
            }else if(item.getType() == 20) {
                viewHolder.payMoney.setText("-" + item.getPayMoney());
                viewHolder.payMoney.setTextColor(context.getResources().getColor(R.color.common_green_color));
            }
            viewHolder.payNote.setText(item.getPayNote());
            viewHolder.payDate.setText(item.getPayDate());
            x.image().bind(viewHolder.face, item.getMerchAvatar(), imageOptions);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView face;
        TextView name;
        TextView payMoney;
        TextView payNote;
        TextView payDate;
    }
}
