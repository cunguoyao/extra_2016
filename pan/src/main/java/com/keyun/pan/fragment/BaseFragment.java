package com.keyun.pan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.keyun.pan.activity.LoginActivity;
import com.keyun.pan.activity.MainActivity;
import com.keyun.pan.activity.ScreenManager;
import com.keyun.pan.app.BaseApplication;
import com.keyun.pan.app.Urls;
import com.keyun.pan.data.User;
import com.keyun.pan.utils.BackHandledInterface;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/6/2.
 */
public abstract class BaseFragment extends Fragment {

    protected BackHandledInterface mBackHandledInterface;

    protected DbManager db;
    protected User user;
    protected Urls Urls;

    protected User getDefaultUser() {
        try {
            user = db.selector(User.class).where("default_acc", "=", 1).findFirst();
        }catch (DbException e) {
        }
        return user;
    }

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    public abstract boolean onBackPressed();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = x.getDb(((BaseApplication)getActivity().getApplicationContext()).getDaoConfig());
        try {
            user = db.selector(User.class).where("default_acc", "=", 1).findFirst();
        }catch (DbException e) {
        }
        if(!(getActivity() instanceof BackHandledInterface)){
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        }else{
            this.mBackHandledInterface = (BackHandledInterface)getActivity();
        }
        Urls = new Urls(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶
        mBackHandledInterface.setSelectedFragment(this);
    }

    public void exit() {
        ScreenManager.getScreenManager().popAllActivityExceptOne(LoginActivity.class);
    }

    public void exitToLogin() {
        ScreenManager.getScreenManager().popAllActivityExceptOne(MainActivity.class);
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
