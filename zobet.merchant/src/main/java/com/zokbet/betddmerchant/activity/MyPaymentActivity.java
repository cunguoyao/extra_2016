package com.zokbet.betddmerchant.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zokbet.betddmerchant.R;
import com.zokbet.betddmerchant.adapter.PaymentListAdapter;
import com.zokbet.betddmerchant.app.Const;
import com.zokbet.betddmerchant.app.Urls;
import com.zokbet.betddmerchant.data.Payment;
import com.zokbet.betddmerchant.widget.MeiTuanFooterLayout;
import com.zokbet.betddmerchant.widget.MeiTuanHeaderLayout;
import com.zokbet.betddmerchant.widget.ProgressDialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_payment_list)
public class MyPaymentActivity extends BaseActivity {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.fav_btn)
    private ImageButton favBtn;
    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;

    private PaymentListAdapter mAdapter;
    private List<Payment> mDatas;
    private int page;
    private int total;
    private final static int REFRESH_COMPLETE = 0;
    private final static int LOADMORE_COMPLETE = 1;
    /**
     * mInterHandler是一个私有静态内部类继承自Handler，内部持有MainActivity的弱引用，
     * 避免内存泄露
     */
    private InterHandler mInterHandler = new InterHandler(this);

    private static class InterHandler extends Handler {
        private WeakReference<MyPaymentActivity> mActivity;
        public InterHandler(MyPaymentActivity activity){
            mActivity = new WeakReference<MyPaymentActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            MyPaymentActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case REFRESH_COMPLETE:
                        activity.mListView.onRefreshComplete();
                        activity.mAdapter.notifyDataSetChanged();
                        if(activity.total > activity.page * Const.PAGE_SIZE_10) {
                            activity.mListView.setMode(PullToRefreshBase.Mode.BOTH);
                        }else {
                            activity.mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }
                        break;
                    case LOADMORE_COMPLETE:
                        activity.mListView.onRefreshComplete();
                        activity.mAdapter.notifyDataSetChanged();
                        if(activity.total > activity.page * Const.PAGE_SIZE_10) {
                            activity.mListView.setMode(PullToRefreshBase.Mode.BOTH);
                        }else {
                            activity.mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }
                        break;
                }
            }else{
                super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleText.setText("我的账单");
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        favBtn.setVisibility(View.INVISIBLE);

        mDatas = new ArrayList<Payment>();
        mAdapter = new PaymentListAdapter(this, mDatas);
        page = 1;
        fetchData(page);

        mListView.setHeaderLayout(new MeiTuanHeaderLayout(this));
        mListView.setFooterLayout(new MeiTuanFooterLayout(this));
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                fetchData(page);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                fetchData(page);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
    }

    private void fetchData(final int page) {
        if(mDatas.size() == 0) {
            ProgressDialogUtils.showRunManProgressDialog(this, true);
        }
        if(page == 1) {
            mDatas.clear();
        }
        RequestParams params = new RequestParams(Urls.tradeList);
        params.addBodyParameter("token", getDefaultUser().getToken());
        params.addBodyParameter("appName", Const.APP_N);
        params.addBodyParameter("page", String.valueOf(page));
        params.addBodyParameter("page_size", String.valueOf(Const.PAGE_SIZE_10));
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {

            private long time = System.currentTimeMillis();
            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess" + time);
                LogUtil.e("---onSuccess-result----" + result);
                if(result != null) {
                    try {
                        org.json.JSONObject json = new org.json.JSONObject(result);
                        if (json.optInt("code") == 100) {
                            org.json.JSONObject jsonObj = json.optJSONObject("data");
                            total = jsonObj.optInt("total");
                            JSONArray jsonArray = jsonObj.optJSONArray("rows");
                            List<Payment> list = Payment.parseJsonForList(jsonArray);
                            if(list != null && list.size() > 0) {
                                mDatas.addAll(list);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("---onCancelled-----" + "onCancelled" + time);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("---onError-----" + "onError" + time);
            }

            @Override
            public void onFinished() {
                LogUtil.e("---onFinished-----" + "onFinished" + time);
                ProgressDialogUtils.dismissRunManProgressBar();
                if(page == 1) {
                    mInterHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
                }else {
                    mInterHandler.sendEmptyMessageDelayed(LOADMORE_COMPLETE, 1000);
                }
            }
        });
    }
}
