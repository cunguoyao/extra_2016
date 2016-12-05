package com.keyun.pan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.keyun.pan.R;
import com.keyun.pan.fragment.BaseFragment;
import com.keyun.pan.fragment.MessageRepoFragment;
import com.keyun.pan.fragment.MessageShareFragment;
import com.keyun.pan.fragment.MessageSpFragment;
import com.keyun.pan.utils.BackHandledInterface;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/8/28.
 */
@ContentView(value = R.layout.activity_my_message)
public class MyMessageActivity extends BaseActivity implements BackHandledInterface {

    private static final String TAG = MyMessageActivity.class.getName();

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.id_radioGroup)
    private RadioGroup mRadioGroup;
    @ViewInject(R.id.id_tab1)
    private RadioButton mRadio01;
    @ViewInject(R.id.id_tab2)
    private RadioButton mRadio02;
    @ViewInject(R.id.id_tab3)
    private RadioButton mRadio03;
    @ViewInject(R.id.id_viewpager)
    private ViewPager mViewPager;

    private List<Fragment> mFragments;
    private TabAdapter mAdapter;
    private BaseFragment mBackHandedFragment;

    public static final String EXTRA_TYPE = "extra_type";
    private int type;

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleText.setText("我的消息");
        type = getIntent().getIntExtra(EXTRA_TYPE, 0);
        initViewPager();
        initEvent();
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        switch (type) {
            case 1:
                ((RadioButton)mRadioGroup.getChildAt(0)).setChecked(true);
            case 2:
                ((RadioButton)mRadioGroup.getChildAt(0)).setChecked(true);
                break;
            case 3:
                ((RadioButton)mRadioGroup.getChildAt(1)).setChecked(true);
                break;
            case 4:
                ((RadioButton)mRadioGroup.getChildAt(2)).setChecked(true);
            case 5:
                ((RadioButton)mRadioGroup.getChildAt(2)).setChecked(true);
                break;
        }
    }

    private void initViewPager() {
        mFragments = new ArrayList<Fragment>();
        MessageSpFragment fragment1 = MessageSpFragment.newInstance();
        mFragments.add(fragment1);
        MessageRepoFragment fragment2 = MessageRepoFragment.newInstance();
        mFragments.add(fragment2);
        MessageShareFragment fragment3 = MessageShareFragment.newInstance();
        mFragments.add(fragment3);

        mAdapter = new TabAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
    }

    private void initEvent() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.id_tab1:
                        mViewPager.setCurrentItem(0);// 选择某一页
                        break;
                    case R.id.id_tab2:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.id_tab3:
                        mViewPager.setCurrentItem(2);
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
                    break;
                case 1:
                    mRadio02.setChecked(true);
                    break;
                case 2:
                    mRadio03.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
            ScreenManager.getScreenManager().popActivity();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        type = getIntent().getIntExtra(EXTRA_TYPE, 0);
        switch (type) {
            case 1:
                ((RadioButton)mRadioGroup.getChildAt(0)).setChecked(true);
            case 2:
                ((RadioButton)mRadioGroup.getChildAt(0)).setChecked(true);
                break;
            case 3:
                ((RadioButton)mRadioGroup.getChildAt(1)).setChecked(true);
                break;
            case 4:
                ((RadioButton)mRadioGroup.getChildAt(2)).setChecked(true);
            case 5:
                ((RadioButton)mRadioGroup.getChildAt(2)).setChecked(true);
                break;
        }
    }
}
