package com.keyun.pan.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.keyun.pan.R;
import com.keyun.pan.utils.GuideTransformer;
import com.keyun.pan.utils.SharedPreferencesUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/9/19.
 */
@ContentView(value = R.layout.activity_guide)
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @ViewInject(R.id.id_viewpager)
    private ViewPager mViewpager;
    @ViewInject(R.id.btn_tiyan)
    private Button mBtnTiyan;
    @ViewInject(R.id.dot_layout)
    private LinearLayout dotLayout;

    private int[] mImgIds = new int[]{ R.drawable.guide1, R.drawable.guide2, R.drawable.guide3 };
    //存储Image
    private List<ImageView> mImages = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        SharedPreferencesUtils.saveByKey(GuideActivity.this, "SERVER", "guide", "1");
        x.view().inject(this);
        initImagesAndDots();
        initView();
    }

    private void initView() {
        mBtnTiyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mViewpager.addOnPageChangeListener(this);
        //为Viewpager添加切换动画效果
        mViewpager.setPageTransformer(true, new GuideTransformer());
        mViewpager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                //有几页
                return mImgIds.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //移除ImageView
                container.removeView(mImages.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                container.addView(mImages.get(position));
                return mImages.get(position);
            }
        });
        /*mImages.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });*/
    }

    private void initImagesAndDots() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_normal);
        for (int i = 0; i < mImgIds.length; i++) {
            ImageView imageView = new ImageView(GuideActivity.this);
            //给ImageView设置图片
            imageView.setImageResource(mImgIds[i]);
            //设置样式
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImages.add(imageView);

            Button bt = new Button(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
            layoutParams.setMargins(10,0,10,0);
            bt.setLayoutParams(layoutParams);
            if(i==0) {
                bt.setBackgroundResource(R.drawable.dot_press);
            }else {
                bt.setBackgroundResource(R.drawable.dot_normal);
            }
            dotLayout.addView(bt);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //判断如果是最后一张图片则显示出立即体验这个按钮
        if((mImgIds.length-1)==position){
            mBtnTiyan.setVisibility(View.VISIBLE);
        }else{
            mBtnTiyan.setVisibility(View.GONE);
        }
        for(int i=0;i<dotLayout.getChildCount();i++) {
            Button dotBtn = (Button)dotLayout.getChildAt(i);
            if(i==position) {
                dotBtn.setBackgroundResource(R.drawable.dot_press);
            }else {
                dotBtn.setBackgroundResource(R.drawable.dot_normal);
            }
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
