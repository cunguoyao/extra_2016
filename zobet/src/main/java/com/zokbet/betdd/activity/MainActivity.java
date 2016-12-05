package com.zokbet.betdd.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import com.zokbet.betdd.R;
import com.zokbet.betdd.ui.HomeFragment;
import com.zokbet.betdd.ui.MerchFragment;
import com.zokbet.betdd.ui.MineFragment;
import com.zokbet.betdd.ui.MoreFragment;

@ContentView(value = R.layout.activity_main)
public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @ViewInject(R.id.main_bottom_tabs)
    private RadioGroup group;
    @ViewInject(R.id.main_home)
    private RadioButton main_home;
    private FragmentManager fragmentManager;//管理fragment
    private HomeFragment homeFragment;
    private MerchFragment merchFragment;
    private MineFragment mineFragment;
    private MoreFragment moreFragment;
    private long exitTime=0;//两次按返回退出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        //初始化fragmentManager
        fragmentManager=getSupportFragmentManager();
        //设置默认选中
        main_home.setChecked(true);
        group.setOnCheckedChangeListener(this);
        //切换不同的fragment
        changeFragment(0);
    }

    @Override
    public void onCheckedChanged(RadioGroup arg0, int checkedId) {
        switch (checkedId) {
            case R.id.main_home:
                changeFragment(0);
                break;
            case R.id.main_tuan:
                changeFragment(1);
                break;
            case R.id.main_mine:
                changeFragment(2);
                break;
            case R.id.main_more:
                changeFragment(3);
                break;
            default:
                break;
        }
    }

    //切换不同的fragment
    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index
     *            每个tab页对应的下标。0表示home，1表示tuan，2表示search，3表示my。
     */
    public void changeFragment(int index)//同时保存每个fragment
    {
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        hideFragments(beginTransaction);
        switch (index) {
            case 0:
                if(homeFragment == null) {
                    homeFragment = new HomeFragment();
                    beginTransaction.add(R.id.main_content, homeFragment);
                }else {
                    beginTransaction.show(homeFragment);
                }
                break;
            case 1:
                if(merchFragment == null) {
                    merchFragment = new MerchFragment();
                    beginTransaction.add(R.id.main_content, merchFragment);
                }else {
                    beginTransaction.show(merchFragment);
                }
                break;
            case 2:
                if(mineFragment == null) {
                    mineFragment = new MineFragment();
                    beginTransaction.add(R.id.main_content, mineFragment);
                }else {
                    beginTransaction.show(mineFragment);
                }
                break;
            case 3:
                if(moreFragment == null) {
                    moreFragment = new MoreFragment();
                    beginTransaction.add(R.id.main_content, moreFragment);
                }else{
                    beginTransaction.show(moreFragment);
                }
                break;
            default:
                break;
        }
        beginTransaction.commit();//需要提交事务
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null)
            transaction.hide(homeFragment);
        if (merchFragment != null)
            transaction.hide(merchFragment);
        if (mineFragment != null)
            transaction.hide(mineFragment);
        if (moreFragment != null)
            transaction.hide(moreFragment);
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            ScreenManager.getScreenManager().popActivity();
        }
    }
}
