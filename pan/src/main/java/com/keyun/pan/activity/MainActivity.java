package com.keyun.pan.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.keyun.pan.R;
import com.keyun.pan.fragment.ApprovalFragment;
import com.keyun.pan.fragment.BaseFragment;
import com.keyun.pan.fragment.GroupPanFragment;
import com.keyun.pan.fragment.MineFragment;
import com.keyun.pan.fragment.MyPanFragment;
import com.keyun.pan.utils.BackHandledInterface;
import com.keyun.pan.utils.FileService;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(value = R.layout.activity_main)
public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, BackHandledInterface {

    @ViewInject(R.id.main_bottom_tabs)
    private RadioGroup group;
    @ViewInject(R.id.main_home)
    private RadioButton main_home;
    private FragmentManager fragmentManager;//管理fragment
    private MyPanFragment myPanFragment;
    private GroupPanFragment groupPanFragment;
    private ApprovalFragment applyFragment;
    private MineFragment mineFragment;
    private long exitTime=0;//两次按返回退出

    private BaseFragment mBackHandedFragment;

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

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

        FileService fileService = new FileService();
        fileService.fetchDownload(this, Urls.getUserDownloadParam(), getDefaultUser());
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
                if(myPanFragment == null) {
                    myPanFragment = new MyPanFragment();
                    beginTransaction.add(R.id.main_content, myPanFragment);
                }else {
                    beginTransaction.show(myPanFragment);
                }
                setSelectedFragment(myPanFragment);
                break;
            case 1:
                if(groupPanFragment == null) {
                    groupPanFragment = new GroupPanFragment();
                    beginTransaction.add(R.id.main_content, groupPanFragment);
                }else {
                    beginTransaction.show(groupPanFragment);
                }
                setSelectedFragment(groupPanFragment);
                break;
            case 2:
                if(applyFragment == null) {
                    applyFragment = new ApprovalFragment();
                    beginTransaction.add(R.id.main_content, applyFragment);
                }else {
                    beginTransaction.show(applyFragment);
                }
                setSelectedFragment(applyFragment);
                break;
            case 3:
                if(mineFragment == null) {
                    mineFragment = new MineFragment();
                    beginTransaction.add(R.id.main_content, mineFragment);
                }else{
                    beginTransaction.show(mineFragment);
                }
                setSelectedFragment(mineFragment);
                break;
            default:
                break;
        }
        beginTransaction.commit();//需要提交事务
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (myPanFragment != null)
            transaction.hide(myPanFragment);
        if (groupPanFragment != null)
            transaction.hide(groupPanFragment);
        if (applyFragment != null)
            transaction.hide(applyFragment);
        if (mineFragment != null)
            transaction.hide(mineFragment);
    }

    @Override
    public void onBackPressed() {
        LogUtil.e("=============Main=====" + System.currentTimeMillis());
        if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
            LogUtil.e("=============Main=1====");
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                ScreenManager.getScreenManager().popActivity();
            }
        }else {
            LogUtil.e("=============Main=2====");
        }
    }

    public void exit() {
        ScreenManager.getScreenManager().popActivity();
    }

}
