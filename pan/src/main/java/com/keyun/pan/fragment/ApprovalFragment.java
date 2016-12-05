package com.keyun.pan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.keyun.pan.R;
import com.keyun.pan.activity.ApprovalActivity;
import com.keyun.pan.activity.ApprovalWfActivity;
import com.keyun.pan.activity.MyInfoActivity;
import com.keyun.pan.data.Approval;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by cunguoyao on 2016/6/2.
 */
public class ApprovalFragment extends BaseFragment implements View.OnClickListener {

    private final String TAG = ApprovalFragment.class.getName();
    private View rootView;

    @ViewInject(R.id.scrollView)
    private PullToRefreshScrollView scrollView;
    @ViewInject(R.id.vp)
    private ViewPager viewPager;
    @ViewInject(R.id.approval_btn_wf)
    private LinearLayout approvalWf;
    @ViewInject(R.id.approval_btn0)
    private LinearLayout approvalBtn0;
    @ViewInject(R.id.approval_btn1)
    private LinearLayout approvalBtn1;
    @ViewInject(R.id.approval_btn2)
    private LinearLayout approvalBtn2;
    @ViewInject(R.id.approval_btn3)
    private LinearLayout approvalBtn3;
    @ViewInject(R.id.iv_approval_btn_wf)
    private ImageView iv_approvalWf;
    @ViewInject(R.id.iv_approval_btn0)
    private ImageView iv_approvalBtn0;
    @ViewInject(R.id.iv_approval_btn1)
    private ImageView iv_approvalBtn1;
    @ViewInject(R.id.iv_approval_btn2)
    private ImageView iv_approvalBtn2;
    @ViewInject(R.id.iv_approval_btn3)
    private ImageView iv_approvalBtn3;

    private int[] pagerRes;
    private List<ImageView> imageViews;
    private int currentItem = 0; // 当前图片的索引号
    private ScheduledExecutorService scheduledExecutorService;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(currentItem);
        };
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagerRes = new int[]{R.drawable.logo_mysp_1, R.drawable.logo_mysp_2, R.drawable.logo_mysp_3};
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_apply, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        LogUtil.e(TAG + "------------onCreateView----");
        x.view().inject(this, rootView);   //注入控件

        scrollView.setMode(PullToRefreshBase.Mode.DISABLED);
        addDynamicView();
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
        startAd();
        approvalWf.setOnClickListener(this);
        approvalBtn0.setOnClickListener(this);
        approvalBtn1.setOnClickListener(this);
        approvalBtn2.setOnClickListener(this);
        approvalBtn3.setOnClickListener(this);
        iv_approvalWf.setOnClickListener(this);
        iv_approvalBtn0.setOnClickListener(this);
        iv_approvalBtn1.setOnClickListener(this);
        iv_approvalBtn2.setOnClickListener(this);
        iv_approvalBtn3.setOnClickListener(this);
        return rootView;
    }

    private void addDynamicView() {
        imageViews = new ArrayList<>();
        // 初始化图片资源
        for (int i = 0; i < pagerRes.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            // 异步加载图片
            imageView.setBackgroundResource(pagerRes[i]);
            imageViews.add(imageView);
        }
    }

    class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return pagerRes.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = imageViews.get(position);
            ((ViewPager) container).addView(iv);
            final int adDomain = pagerRes[position];
            // 在这个方法里面设置图片的点击事件
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 处理跳转逻辑
                }
            });
            return iv;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            currentItem = position;
        }
    }

    private void startAd() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每两秒切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
    }

    private class ScrollTask implements Runnable {

        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        LogUtil.e("========ApplyFragment==========" + System.currentTimeMillis());
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.user_avatar:
            case R.id.user_name:
                intent = new Intent(getActivity(), MyInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.approval_btn_wf:
                intent = new Intent(getActivity(), ApprovalWfActivity.class);
                startActivity(intent);
                break;
            case R.id.approval_btn0:
                intent = new Intent(getActivity(), ApprovalActivity.class);
                intent.putExtra("type", Approval.TYPE_TODO_APPLY);
                startActivity(intent);
                break;
            case R.id.approval_btn1:
                intent = new Intent(getActivity(), ApprovalActivity.class);
                intent.putExtra("type", Approval.TYPE_FINISH_APPLY);
                startActivity(intent);
                break;
            case R.id.approval_btn2:
                intent = new Intent(getActivity(), ApprovalActivity.class);
                intent.putExtra("type", Approval.TYPE_TODO_REQ);
                startActivity(intent);
                break;
            case R.id.approval_btn3:
                intent = new Intent(getActivity(), ApprovalActivity.class);
                intent.putExtra("type", Approval.TYPE_FINISH_REQ);
                startActivity(intent);
                break;
            case R.id.iv_approval_btn_wf:
                approvalWf.performClick();
                break;
            case R.id.iv_approval_btn0:
                approvalBtn0.performClick();
                break;
            case R.id.iv_approval_btn1:
                approvalBtn1.performClick();
                break;
            case R.id.iv_approval_btn2:
                approvalBtn2.performClick();
                break;
            case R.id.iv_approval_btn3:
                approvalBtn3.performClick();
                break;
        }
    }
}
