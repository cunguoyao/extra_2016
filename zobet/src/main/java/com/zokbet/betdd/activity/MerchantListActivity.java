package com.zokbet.betdd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zokbet.betdd.R;
import com.zokbet.betdd.adapter.MerchListAdapter;
import com.zokbet.betdd.app.Const;
import com.zokbet.betdd.app.Urls;
import com.zokbet.betdd.data.Category;
import com.zokbet.betdd.data.Merchant;
import com.zokbet.betdd.widget.MeiTuanFooterLayout;
import com.zokbet.betdd.widget.MeiTuanHeaderLayout;
import com.zokbet.betdd.widget.ProgressDialogUtils;

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
@ContentView(value = R.layout.activity_merchant_list)
public class MerchantListActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.fav_btn)
    private ImageButton favBtn;
    @ViewInject(R.id.title_search_layout)
    private RelativeLayout titleSearchLayout;
    @ViewInject(R.id.text_search)
    private TextView searchText;
    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;

    private int is_rec;
    private String keyword;
    private Category category;

    private MerchListAdapter mAdapter;
    private List<Merchant> mDatas;
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
        private WeakReference<MerchantListActivity> mActivity;
        public InterHandler(MerchantListActivity activity){
            mActivity = new WeakReference<MerchantListActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            MerchantListActivity activity = mActivity.get();
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

        keyword = getIntent().getStringExtra("keyword");
        is_rec = getIntent().getIntExtra("is_rec", 0);
        category = (Category)getIntent().getSerializableExtra("cate");
        if(!TextUtils.isEmpty(keyword)) {
            is_rec = 0;
            titleText.setVisibility(View.GONE);
            titleSearchLayout.setVisibility(View.VISIBLE);
            searchText.setText(keyword);
        }
        if(is_rec != 0) {
            is_rec = 1;
            titleText.setVisibility(View.VISIBLE);
            titleText.setText("今日推荐");
            titleSearchLayout.setVisibility(View.GONE);
        }
        if(category != null) {
            is_rec = 0;
            titleText.setVisibility(View.VISIBLE);
            titleText.setText(category.getName());
            titleSearchLayout.setVisibility(View.GONE);
        }
        titleBack.setOnClickListener(this);
        titleSearchLayout.setOnClickListener(this);
        favBtn.setVisibility(View.INVISIBLE);

        mDatas = new ArrayList<Merchant>();
        mAdapter = new MerchListAdapter(this, mDatas);
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
                Merchant merchant = mAdapter.getItem(position-1);
                Intent intent = new Intent(MerchantListActivity.this, MerchantDetailActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("body", merchant);
                intent.putExtras(b);
                startActivity(intent);
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
        RequestParams params = new RequestParams(Urls.shopList);
        params.addBodyParameter("page", String.valueOf(page));
        params.addBodyParameter("page_size", String.valueOf(Const.PAGE_SIZE_10));
        params.addBodyParameter("is_index", String.valueOf(0));
        params.addBodyParameter("is_rec", String.valueOf(is_rec));
        if(category != null) {
            params.addBodyParameter("cid", String.valueOf(category.getCid()));
        }else {
            params.addBodyParameter("cid", String.valueOf(0));
        }
        params.addBodyParameter("keyword", keyword);
        params.addBodyParameter("lat", String.valueOf(0));
        params.addBodyParameter("lng", String.valueOf(0));
        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {

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
                            List<Merchant> list = Merchant.parseJsonForList(jsonArray);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_search_layout:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
