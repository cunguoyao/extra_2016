package com.zokbet.betdd.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zokbet.betdd.R;
import com.zokbet.betdd.data.MerchantPhoto;
import com.zokbet.betdd.widget.photoview.HackyViewPager;
import com.zokbet.betdd.widget.photoview.PhotoView;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/5/20.
 */

@ContentView(value = R.layout.photoview_image_pager)
public class ImagePagerActivity extends BaseActivity {

    private static final String STATE_POSITION = "STATE_POSITION";
    private static final String IMAGES = "images";
    private static final String IMAGE_POSITION = "image_index";

    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.pager)
    private HackyViewPager pager;
    @ViewInject(R.id.words)
    private TextView descText;
    @ViewInject(R.id.num)
    private TextView numText;
    @ViewInject(R.id.download)
    private ImageButton downloadBtn;

    private int currentPos;
    private ArrayList<MerchantPhoto> imageUrls;
    private ImageOptions imageOptions;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(160), DensityUtil.dip2px(160))//图片大小
                //.setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER)
                .setLoadingDrawableId(R.drawable.list_thumbnail_none_m)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.list_thumbnail_none_m)//加载失败后默认显示图片
                .build();
        currentPos = getIntent().getIntExtra("position", 0);
        imageUrls = (ArrayList<MerchantPhoto>)getIntent().getSerializableExtra("photo");
        pager.setAdapter(new ImagePagerAdapter(imageUrls, this));
        pager.setCurrentItem(currentPos);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private List<MerchantPhoto> images;
        private LayoutInflater inflater;
        private Context mContext;

        ImagePagerAdapter(List<MerchantPhoto> images, Context context) {
            this.images = images;
            this.mContext=context;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
            descText.setText(images.get(pager.getCurrentItem()).desc);
            numText.setText((pager.getCurrentItem()+1) + "/" + images.size());
        }

        @Override
        public int getCount() {
            return images != null ? images.size() : 0;
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            currentPos = pager.getCurrentItem();
            View imageLayout = inflater.inflate(R.layout.photoview_item_pager_image, view, false);

            PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            x.image().bind(imageView, images.get(position).url, imageOptions, new Callback.ProgressCallback<Drawable>() {
                @Override
                public void onWaiting() {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onStarted() {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(Drawable result) {
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onFinished() {
                    spinner.setVisibility(View.GONE);
                }
            });
            descText.setText(images.get(currentPos).desc);
            numText.setText((currentPos+1) + "/" + images.size());
            ((ViewPager) view).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }
    }
}
