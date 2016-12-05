package com.keyun.pan.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.keyun.pan.R;
import com.keyun.pan.fragment.BaseFragment;
import com.keyun.pan.fragment.FileDownloadFragment;
import com.keyun.pan.fragment.FileUploadFragment;
import com.keyun.pan.utils.BackHandledInterface;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/7/10.
 */
@ContentView(value = R.layout.activity_file_transfer)
public class FileTransferActivity extends BaseActivity implements BackHandledInterface {

    private static final String TAG = FileTransferActivity.class.getName();

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
    @ViewInject(R.id.id_tab_line1)
    private ImageView mTabLine1;
    @ViewInject(R.id.id_tab_line2)
    private ImageView mTabLine2;
    @ViewInject(R.id.id_viewpager)
    private ViewPager mViewPager;

    private List<Fragment> mFragments;
    private TabAdapter mAdapter;
    private BaseFragment mBackHandedFragment;

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleText.setText("文件传输");
        initViewPager();
        initEvent();
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViewPager() {
        mFragments = new ArrayList<Fragment>();
        FileDownloadFragment fragment1 = FileDownloadFragment.newInstance();
        mFragments.add(fragment1);
        FileUploadFragment fragment2 = FileUploadFragment.newInstance();
        mFragments.add(fragment2);

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
    public void onBackPressed() {
        if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
            ScreenManager.getScreenManager().popActivity();
        }
    }
}
