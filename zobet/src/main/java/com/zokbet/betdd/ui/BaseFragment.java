package com.zokbet.betdd.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zokbet.betdd.app.ZobetApplication;
import com.zokbet.betdd.data.User;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/5/29.
 */
public class BaseFragment extends Fragment {

    protected DbManager db;
    protected User user;

    protected User getDefaultUser() {
        try {
            user = db.selector(User.class).where("default_acc", "=", 1).findFirst();
        }catch (DbException e) {
        }
        return user;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = x.getDb(((ZobetApplication)getActivity().getApplicationContext()).getDaoConfig());
        try {
            user = db.selector(User.class).where("default_acc", "=", 1).findFirst();
        }catch (DbException e) {
        }
    }
}
