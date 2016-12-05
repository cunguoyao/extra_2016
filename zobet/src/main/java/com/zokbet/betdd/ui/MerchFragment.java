package com.zokbet.betdd.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

import com.zokbet.betdd.R;
import com.zokbet.betdd.activity.SearchActivity;

/**
 * Created by cunguoyao on 2016/5/15.
 */
public class MerchFragment extends Fragment implements View.OnClickListener {

    private final String TAG = MerchFragment.class.getName();
    private View rootView;

    @ViewInject(R.id.btn_search)
    private ImageButton searchButton;
    @ViewInject(R.id.id_radioGroup)
    private RadioGroup mRadioGroup;
    @ViewInject(R.id.id_tab1)
    private RadioButton mRadio01;
    @ViewInject(R.id.id_tab2)
    private RadioButton mRadio02;
    @ViewInject(R.id.id_tab_line1)
    private ImageView mTabLine1;
    @ViewInject(R.id.id_tab_line2)
    private ImageView mTabLine2;
    @ViewInject(R.id.id_viewpager)
    private ViewPager mViewPager;

    private List<Fragment> mFragments;
    private TabAdapter mAdapter;
    private int mTabLineLength;

    public MerchFragment() {
        LogUtil.e(TAG + "--------------new");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_merch, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        LogUtil.e(TAG + "------------onCreateView----");
        x.view().inject(this, rootView);

        searchButton.setOnClickListener(this);
        initViewPager();
        initEvent();

        return rootView;
    }

    private void initViewPager() {
        mFragments = new ArrayList<Fragment>();
        MerchListFragment fragment1 = MerchListFragment.newInstance();
        mFragments.add(fragment1);

        mAdapter = new TabAdapter(getChildFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
    }

    private void initEvent() {
        mRadio02.setVisibility(View.GONE);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.id_tab1:
                        mViewPager.setCurrentItem(0);// 选择某一页
                        break;
                    case R.id.id_tab2:
                        mViewPager.setCurrentItem(1);
                        break;
                }
            }
        });
        mViewPager.setOnPageChangeListener(new TabOnPageChangeListener());
    }
    /**
     * 功能：主页引导栏的三个Fragment页面设置适配器
     */
    public class TabAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;

        public TabAdapter(FragmentManager fm, List<Fragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;
        }

        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        public int getCount() {
            return mFragments.size();
        }
    }

    /**
     * 页卡滑动改变事件
     */
    public class TabOnPageChangeListener implements ViewPager.OnPageChangeListener {

        /**
         * 当滑动状态改变时调用
         * state=0的时候表示什么都没做，就是停在那
         * state=1的时候表示正在滑动
         * state==2的时候表示滑动完毕了
         */
        public void onPageScrollStateChanged(int state) {

        }

        /**
         * 当前页面被滑动时调用
         * position:当前页面
         * positionOffset:当前页面偏移的百分比
         * positionOffsetPixels:当前页面偏移的像素位置
         */
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
           /* LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams) mTabLine.getLayoutParams();
            //获取组件距离左侧组件的距离
            int l = (int) ((positionOffset+position)*mTabLineLength);
            Log.e("llllllllllllll", ""+l);
            lp.leftMargin = l;
            mTabLine.setLayoutParams(lp);*/
        }

        //当新的页面被选中时调用
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    mRadio01.setChecked(true);
                    mTabLine1.setVisibility(View.VISIBLE);
                    mTabLine2.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    mRadio02.setChecked(true);
                    mTabLine1.setVisibility(View.INVISIBLE);
                    mTabLine2.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                Intent intent1 = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
