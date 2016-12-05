package com.zokbet.betdd.utils;


import org.xutils.common.Callback;

/**
 * Created by cunguoyao on 2016/5/23.
 */
public class HttpCallback<HttpReturn> implements Callback.CommonCallback<HttpReturn> {

    @Override
    public void onSuccess(HttpReturn result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}
