package com.zokbet.betdd.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.zokbet.betdd.activity.MerchantDetailActivity;
import com.zokbet.betdd.R;
import com.zokbet.betdd.adapter.MerchListAdapter;
import com.zokbet.betdd.app.Const;
import com.zokbet.betdd.app.Urls;
import com.zokbet.betdd.data.Category;
import com.zokbet.betdd.data.Merchant;
import com.zokbet.betdd.utils.SharedPreferencesUtils;
import com.zokbet.betdd.widget.MeiTuanFooterLayout;
import com.zokbet.betdd.widget.MeiTuanHeaderLayout;
import com.zokbet.betdd.widget.ProgressDialogUtils;
import com.zokbet.betdd.widget.expandpop.ExpandPopTabView;
import com.zokbet.betdd.widget.expandpop.KeyValueBean;
import com.zokbet.betdd.widget.expandpop.PopOneListView;
import com.zokbet.betdd.widget.expandpop.PopTwoListView;

/**
 * Created by cunguoyao on 2016/5/17.
 */
public class MerchListFragment extends Fragment {

    private static final String TAG = MerchListFragment.class.getName();
    private View rootView;

    @ViewInject(R.id.expandtab_view)
    private ExpandPopTabView expandTabView;
    @ViewInject(R.id.locate)
    private TextView locationText;
    @ViewInject(R.id.progress)
    private ProgressBar progressBar;
    @ViewInject(R.id.refresh_btn)
    private ImageButton refreshBtn;
    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;

    private double longitude;
    private double latitude;
    private String address;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private List<KeyValueBean> mParentLists = new ArrayList<>();
    private List<ArrayList<KeyValueBean>> mChildrenListLists = new ArrayList<>();
    private List<KeyValueBean> mAreaLists = new ArrayList<>();
    private List<KeyValueBean> mOrderLists = new ArrayList<>();

    private List<Merchant> mDatas;
    private MerchListAdapter mAdapter;
    private int categoryId;
    private String area;
    private int orderId;
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
        private WeakReference<MerchListFragment> mActivity;
        public InterHandler(MerchListFragment activity){
            mActivity = new WeakReference<MerchListFragment>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            MerchListFragment activity = mActivity.get();
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

    public static MerchListFragment newInstance() {
        MerchListFragment f = new MerchListFragment();
        Bundle args = new Bundle();
        //args.putInt("categoryId", categoryId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCateData();
        setAreaData();
        setOrderData();

        mLocationClient = new LocationClient(getActivity().getApplicationContext());//声明LocationClient类
        mLocationClient.registerLocationListener(myListener);//注册监听函数
        initLocation();

        mDatas = new ArrayList<Merchant>();
        mAdapter = new MerchListAdapter(getActivity(), mDatas);
        page = 1;
        Bundle bundle = getArguments();
        if (bundle != null) {
            categoryId = bundle.getInt("categoryId", 0);
            orderId = bundle.getInt("orderId", 0);
            area = bundle.getString("area", "");
        }else {
            categoryId = 0;
            orderId = 0;
            area = "";
        }
        fetchData(categoryId, area, orderId, page);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_merch_list, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        LogUtil.e(TAG + "------------onCreateView----");
        x.view().inject(this, rootView);

        //addItem(expandTabView, mParentLists, mChildrenListLists, mParentLists.get(0).getValue(), mChildrenListLists.get(0).get(0).getValue(), "分类");
        //addItem(expandTabView, mOrderLists, mOrderLists.get(0).getValue(), "排序");
        addItem(expandTabView, mParentLists, mParentLists.get(0).getValue(), "分类");
        addItem(expandTabView, mAreaLists, mAreaLists.get(0).getValue(), "区域");
        addItem(expandTabView, mOrderLists, mOrderLists.get(0).getValue(), "排序");

        progressBar.setVisibility(View.VISIBLE);
        refreshBtn.setVisibility(View.GONE);

        mListView.setHeaderLayout(new MeiTuanHeaderLayout(getActivity()));
        mListView.setFooterLayout(new MeiTuanFooterLayout(getActivity()));
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                fetchData(categoryId, area, orderId, page);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                fetchData(categoryId, area, orderId, page);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Merchant merchant = mAdapter.getItem(position-1);
                Intent intent = new Intent(getActivity(), MerchantDetailActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("body", merchant);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLocationClient.start();
    }

    public void addItem(ExpandPopTabView expandTabView, List<KeyValueBean> lists, String defaultSelect, final String defaultShowText) {
        PopOneListView popOneListView = new PopOneListView(getActivity());
        popOneListView.setDefaultSelectByValue(defaultSelect);
        //popViewOne.setDefaultSelectByKey(defaultSelect);
        popOneListView.setCallBackAndData(lists, expandTabView, new PopOneListView.OnSelectListener() {
            @Override
            public void getValue(String key, String value) {
                LogUtil.e("tag" + "key :" + key + " ,value :" + value);
            }
        });
        expandTabView.addItemToExpandTab(defaultShowText, popOneListView);
        popOneListView.setOnSelectListener(expandTabView, new PopOneListView.OnSelectListener(){
            @Override
            public void getValue(String key, String value) {
                if("分类".equals(defaultShowText)) {
                    categoryId = Integer.parseInt(key);
                }else if("区域".equals(defaultShowText)) {
                    if("0".equals(key)) {
                        area = "";
                    }else {
                        area = value;
                    }
                }else {
                    orderId = Integer.parseInt(key);
                }
                fetchData(categoryId, area, orderId, 1);
            }
        });
    }

    public void addItem(ExpandPopTabView expandTabView, List<KeyValueBean> parentLists,
                        List<ArrayList<KeyValueBean>> childrenListLists, String defaultParentSelect, String defaultChildSelect, String defaultShowText) {
        PopTwoListView popTwoListView = new PopTwoListView(getActivity());
        popTwoListView.setDefaultSelectByValue(defaultParentSelect, defaultChildSelect);
        //distanceView.setDefaultSelectByKey(defaultParent, defaultChild);
        popTwoListView.setCallBackAndData(expandTabView, parentLists, childrenListLists, new PopTwoListView.OnSelectListener() {
            @Override
            public void getValue(String showText, String parentKey, String childrenKey) {
                LogUtil.e("tag" + "showText :" + showText + " ,parentKey :" + parentKey + " ,childrenKey :" + childrenKey);
            }
        });
        expandTabView.addItemToExpandTab(defaultShowText, popTwoListView);
        popTwoListView.setOnParentSelectListener(new PopTwoListView.OnSelectListener(){
            @Override
            public void getValue(String showText, String parentKey, String childrenKey) {
                fetchData(Integer.parseInt(parentKey), area, orderId, 1);
            }
        });
    }

    private void setCateData() {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(getActivity(), "public");
        ArrayList<Category> categoryList = sharedPreferencesUtils.getObject("cate", ArrayList.class);
        if(categoryList != null && categoryList.size() > 0) {
            for (Category category : categoryList) {
                KeyValueBean keyValueBean = new KeyValueBean();
                keyValueBean.setKey(String.valueOf(category.getCid()));
                keyValueBean.setValue(category.getName());
                mParentLists.add(keyValueBean);

                /*ArrayList<KeyValueBean> childrenLists = new ArrayList<>();
                for (int i=0;i<mParentLists.size();i++) {
                    KeyValueBean keyValueBean1 = new KeyValueBean();
                    keyValueBean1.setKey("");
                    keyValueBean1.setValue("");
                    childrenLists.add(keyValueBean1);
                }
                mChildrenListLists.add(childrenLists);*/
            }

        }else {
            InputStream is = null;
            try {
                is = getActivity().getAssets().open("cate");
                String cate = readStream(is);
                JSONArray jsonArray = new JSONArray(cate);
                categoryList = Category.parseJson(jsonArray);
                for (Category category : categoryList) {
                    KeyValueBean keyValueBean = new KeyValueBean();
                    keyValueBean.setKey(String.valueOf(category.getCid()));
                    keyValueBean.setValue(category.getName());
                    mParentLists.add(keyValueBean);

                /*ArrayList<KeyValueBean> childrenLists = new ArrayList<>();
                for (int i=0;i<mParentLists.size();i++) {
                    KeyValueBean keyValueBean1 = new KeyValueBean();
                    keyValueBean1.setKey("");
                    keyValueBean1.setValue("");
                    childrenLists.add(keyValueBean1);
                }
                mChildrenListLists.add(childrenLists);*/
                }

            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void setAreaData() {
        InputStream is = null;
        try {
            is = getActivity().getAssets().open("area");
            String cate = readStream(is);
            JSONArray jsonArray = new JSONArray(cate);
            List<Category> categoryList = Category.parseJson(jsonArray);
            for (Category category : categoryList) {
                KeyValueBean keyValueBean = new KeyValueBean();
                keyValueBean.setKey(String.valueOf(category.getCid()));
                keyValueBean.setValue(category.getName());
                mAreaLists.add(keyValueBean);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setOrderData() {
        InputStream is = null;
        try {
            is = getActivity().getAssets().open("order");
            String cate = readStream(is);
            JSONArray jsonArray = new JSONArray(cate);
            List<Category> categoryList = Category.parseJson(jsonArray);
            for (Category category : categoryList) {
                KeyValueBean keyValueBean = new KeyValueBean();
                keyValueBean.setKey(String.valueOf(category.getCid()));
                keyValueBean.setValue(category.getName());
                mOrderLists.add(keyValueBean);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private void fetchData(final int categoryId, final String area, final int orderId, final int page) {
        if(page == 1) {
            mDatas.clear();
            mAdapter.notifyDataSetChanged();
        }
        if(mDatas.size() == 0) {
            ProgressDialogUtils.showRunManProgressDialog(getActivity(), true);
        }
        RequestParams params = new RequestParams(Urls.shopList);
        params.addBodyParameter("page", String.valueOf(page));
        params.addBodyParameter("page_size", String.valueOf(Const.PAGE_SIZE_10));
        params.addBodyParameter("is_index", String.valueOf(0));
        params.addBodyParameter("is_rec", String.valueOf(0));
        params.addBodyParameter("cid", String.valueOf(categoryId));
        params.addBodyParameter("keyword", "");
        params.addBodyParameter("area", area);
        params.addBodyParameter("lat", String.valueOf(latitude));
        params.addBodyParameter("lng", String.valueOf(longitude));
        LogUtil.i("page:" + String.valueOf(page));
        LogUtil.i("page_size:" + String.valueOf(Const.PAGE_SIZE_10));
        LogUtil.i("is_index:" + String.valueOf(0));
        LogUtil.i("is_rec:" + String.valueOf(0));
        LogUtil.i("cid:" + String.valueOf(categoryId));
        LogUtil.i("keyword:" + "");
        LogUtil.i("area:" + area);
        LogUtil.i("lat:" + String.valueOf(latitude));
        LogUtil.i("lng:" + String.valueOf(longitude));
        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
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
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onFinished() {
                ProgressDialogUtils.dismissRunManProgressBar();
                if(page == 1) {
                    mInterHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
                }else {
                    mInterHandler.sendEmptyMessageDelayed(LOADMORE_COMPLETE, 1000);
                }
            }
        });
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span=1000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
//        option.setOpenGps(false);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            address = location.getAddrStr();
            LogUtil.e("---getLocType---" + "" + location.getLocType());
            LogUtil.e("---location---" + "lat:" + location.getLatitude() + ",lon:" + location.getLongitude()
                + location.getDistrict()+",cityCode:" + location.getCityCode() + ",city:" + location.getCity() + ",addr:" + location.getAddress().address
                );
            locationText.setText(address);
            progressBar.setVisibility(View.GONE);
            refreshBtn.setVisibility(View.VISIBLE);
            refreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    refreshBtn.setVisibility(View.GONE);
                    mLocationClient.requestLocation();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        if (expandTabView != null) {
            expandTabView.onExpandPopView();
        }
    }

}
