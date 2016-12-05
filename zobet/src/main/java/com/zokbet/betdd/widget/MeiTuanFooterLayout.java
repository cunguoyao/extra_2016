package com.zokbet.betdd.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.handmark.pulltorefresh.library.LoadingLayoutBase;

import com.zokbet.betdd.R;

/**
 * Created by cunguoyao on 2016/5/19.
 */
public class MeiTuanFooterLayout extends LoadingLayoutBase  {

    private FrameLayout mInnerLayout;

    public MeiTuanFooterLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.meituan_footer_loadinglayout, this);
        mInnerLayout = (FrameLayout) findViewById(R.id.fl_inner);
    }

    @Override
    public int getContentSize() {
        return mInnerLayout.getHeight();
    }

    @Override
    public void pullToRefresh() {

    }

    @Override
    public void releaseToRefresh() {

    }

    @Override
    public void onPull(float scaleOfLayout) {

    }

    @Override
    public void refreshing() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void setPullLabel(CharSequence pullLabel) {

    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {

    }

    @Override
    public void setReleaseLabel(CharSequence releaseLabel) {

    }
}
