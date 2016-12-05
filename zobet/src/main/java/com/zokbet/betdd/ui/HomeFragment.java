package com.zokbet.betdd.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.zokbet.betdd.activity.CaptureActivity;
import com.zokbet.betdd.activity.MerchantDetailActivity;
import com.zokbet.betdd.R;
import com.zokbet.betdd.activity.MerchantListActivity;
import com.zokbet.betdd.activity.SearchActivity;
import com.zokbet.betdd.adapter.MerchListAdapter;
import com.zokbet.betdd.app.Const;
import com.zokbet.betdd.app.Urls;
import com.zokbet.betdd.data.Category;
import com.zokbet.betdd.data.Merchant;
import com.zokbet.betdd.utils.ResourceHelper;
import com.zokbet.betdd.utils.SharedPreferencesUtils;
import com.zokbet.betdd.widget.InnerListView;
import com.zokbet.betdd.widget.MeiTuanHeaderLayout;
import com.zokbet.betdd.widget.ProgressDialogUtils;
import com.zokbet.betdd.widget.WrapContentHeightViewPager;

/**
 * Created by cunguoyao on 2016/5/15.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private final String TAG = HomeFragment.class.getName();
    private View rootView;
    @ViewInject(R.id.home_top_city)
    private TextView topCity;
    @ViewInject(R.id.title_search_layout)
    private RelativeLayout titleSearchLayout;
    @ViewInject(R.id.index_home_tips_arrow)
    private ImageButton barCodeScan;
    @ViewInject(R.id.index_home_viewpager)
    private WrapContentHeightViewPager viewPager;

    @ViewInject(R.id.index_home_rb1)//radiogroup 1组以及3个radiobutton
    private RadioButton rb1;
    @ViewInject(R.id.index_home_rb2)
    private RadioButton rb2;
    @ViewInject(R.id.index_home_rb3)
    private RadioButton rb3;

    @ViewInject(R.id.guess_you_like)
    private LinearLayout guessYouLike;

    private GridView gridView1;
    private GridView gridView2;
    private GridView gridView3;

    private List<Category> categories;
    @ViewInject(R.id.scrollView)
    private PullToRefreshScrollView mScrollView;
    @ViewInject(R.id.listView)
    private InnerListView mListView;

    private List<Merchant> mDatas;
    private MerchListAdapter mAdapter;

    //接受处理消息
    private Handler handler=new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message arg0) {
           if(arg0.what==1){
                initViewPager();//初始化 viewpager 解决切换不显示的问题
           }else if(arg0.what == 2) {
                mScrollView.onRefreshComplete();
                mAdapter.notifyDataSetChanged();
                mScrollView.getRefreshableView().smoothScrollTo(0, 5);
                mListView.setFocusable(false);
           }else if(arg0.what == 3) {
               initViewPager();//初始化 viewpager 解决切换不显示的问题
               handler.sendEmptyMessageDelayed(4, 1000);
           }else if(arg0.what == 4) {
               fetchShopAdData();
               fetchPicAdData();
               fetchMerchData();
           }
           return false;
        }
    });

    public HomeFragment() {
        LogUtil.e(TAG + "--------------new");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categories = new ArrayList<Category>();
        mDatas = new ArrayList<Merchant>();
        mAdapter = new MerchListAdapter(getActivity(), mDatas);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        LogUtil.e(TAG + "------------onCreateView----");
        x.view().inject(this, rootView);   //注入控件
        topCity.setText("徐州");
        titleSearchLayout.setOnClickListener(this);
        barCodeScan.setOnClickListener(this);

        fetchCateDataLocal();

        mListView.setAdapter(mAdapter);
        mScrollView.setHeaderLayout(new MeiTuanHeaderLayout(getActivity()));
        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                fetchShopAdData();
                fetchPicAdData();
                fetchMerchData();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Merchant item = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MerchantDetailActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("body", item);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        mScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        return rootView;
    }

    //gridview 的适配器
    public class GridViewAdapter extends BaseAdapter {

        //我的数据在utils包下的MyConstant中定义好了
        private LayoutInflater inflater;
        private int page;

        public GridViewAdapter(Context context, int page) {
            super();
            this.inflater = LayoutInflater.from(context);
            this.page=page;
        }

        @Override
        public int getCount() {
            if(page!=-1){
                return Const.CATE_NUM_PER_PAGE;
            }else{
                return (categories.size() / Const.CATE_NUM_PER_PAGE < (page + 1)) ? categories.size() % (page + 1) : Const.CATE_NUM_PER_PAGE;
            }
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            final Category item = categories.get(page*Const.CATE_NUM_PER_PAGE + position);
            ViewHolder vh = null;
            if(convertView==null){
                vh=new ViewHolder();
                convertView=inflater.inflate(R.layout.index_home_grid_item, null);
                x.view().inject(vh, convertView);
                convertView.setTag(vh);
            }
            else{
                vh=(ViewHolder) convertView.getTag();
            }

            vh.iv_navsort.setImageResource(Const.navSortImages[page*Const.CATE_NUM_PER_PAGE + position]);
            vh.tv_navsort.setText(item.getName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), MerchantListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("cate", item);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
            return convertView;
        }
    }

    //gridview 适配器的holder类
    private class ViewHolder{
        @ViewInject(R.id.index_home_iv_navsort)
        ImageView iv_navsort;
        @ViewInject(R.id.index_home_tv_navsort)
        TextView tv_navsort;
    }

    private void initViewPager(){   //初始化viewpager
        List<View> list=new ArrayList<View>();  //以下实现动态添加三组gridview
        if(categories.size() > 0) {
            rb1.setVisibility(View.VISIBLE);
            gridView1=(GridView) LayoutInflater.from(getActivity()).inflate(R.layout.index_home_gridview, null);
            gridView1.setAdapter(new GridViewAdapter(getActivity(), 0));
            list.add(gridView1);
        }
        if(categories.size() > Const.CATE_NUM_PER_PAGE) {
            rb2.setVisibility(View.VISIBLE);
            gridView2=(GridView) LayoutInflater.from(getActivity()).inflate(R.layout.index_home_gridview, null);
            gridView2.setAdapter(new GridViewAdapter(getActivity(), 1));
            list.add(gridView2);
        }
        if(categories.size() > (2 * Const.CATE_NUM_PER_PAGE)) {
            rb3.setVisibility(View.VISIBLE);
            gridView3=(GridView) LayoutInflater.from(getActivity()).inflate(R.layout.index_home_gridview, null);
            gridView3.setAdapter(new GridViewAdapter(getActivity(), 2));
            list.add(gridView3);
        }
        viewPager.setAdapter(new MyViewPagerAdapter(list));
        //viewPager .setOffscreenPageLimit(2);   //meiyong
        rb1.setChecked(true);//设置默认  下面的点选中的是第一个
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {  //实现划到那个页面，那个页面下面的点会被选中
                // TODO Auto-generated method stub
                if(position==0){
                    rb1.setChecked(true);
                }else if(position==1){
                    rb2.setChecked(true);
                }else if(position==2){
                    rb3.setChecked(true);
                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });

    }
    //自定义viewpager的适配器
    private class MyViewPagerAdapter extends PagerAdapter {

        List<View> list;
        //List<String> titles;
        public MyViewPagerAdapter(List<View> list) {
            // TODO Auto-generated constructor stub

            this.list=list;
            //this.titles=titles;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        //  判断  当前的view 是否是  Object 对象
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0==arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(list.get(position));
            LogUtil.e("jhd" + "添加--"+position);

            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub

            container.removeView(list.get(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // TODO Auto-generated method stub
            //return titles.get(position);
            return "1";  //暂时没用的
        }
    }

    private void fetchCateDataLocal() {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(getActivity(), "public");
        categories = sharedPreferencesUtils.getObject("cate", ArrayList.class);
        if(categories == null || categories.size() <= 0) {
            InputStream is = null;
            try {
                is = getActivity().getAssets().open("cate");
                String cate = readStream(is);
                JSONArray jsonArray = new JSONArray(cate);
                categories = Category.parseJson(jsonArray);
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
        if(categories != null && categories.size() > 0) {
            categories.remove(0);
        }
        Message message = new Message();
        message.what = 3;
        handler.sendMessageDelayed(message, 500);
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

    private void fetchCateData() {
        RequestParams params = new RequestParams(Urls.getCate);
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-result----" + result);
                if(result != null) {
                    try {
                        org.json.JSONObject json = new org.json.JSONObject(result);
                        if (json.optInt("code") == 100) {
                            org.json.JSONArray jsonArray = json.optJSONArray("data");
                            ArrayList<Category> list = Category.parseJson(jsonArray);
                            if(list != null && list.size() > 0) {
                                categories.clear();
                                categories.addAll(list);
                                Category all = new Category();
                                all.setCid(0);
                                all.setName("全部分类");
                                list.add(0, all);
                                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(getActivity(), "public");
                                sharedPreferencesUtils.setObject("cate", list);
                            }
                        }
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessageDelayed(message, 500);//发线程 初始化viewpager 解决切换页面导致viewpager中的内容为空
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
            }
        });
    }

    private void fetchShopAdData() {
        RequestParams params = new RequestParams(Urls.shopAd);
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-result----" + result);
                if(result != null) {
                    try {
                        org.json.JSONObject json = new org.json.JSONObject(result);
                        if (json.optInt("code") == 100) {
                            org.json.JSONArray jsonArray = json.optJSONArray("data");
                            if(jsonArray != null && jsonArray.length() > 0) {
                                int size = jsonArray.length() > 3 ? 3 : jsonArray.length();
                                for(int i=0;i<size ;i++) {
                                    //guessYouLike.setVisibility(View.VISIBLE);
                                    final JSONObject j = jsonArray.getJSONObject(i);
                                    ImageView imageView = new ImageView(getActivity());
                                    RelativeLayout.LayoutParams _params = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    _params.setMargins(2, 2, 2, 2);
                                    _params.addRule(RelativeLayout.CENTER_IN_PARENT);
                                    imageView.setLayoutParams(_params);
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    imageView.setPadding(2, 2, 2, 2);
                                    x.image().bind(imageView, j.optString("picPath"));
                                    RelativeLayout relativeLayout = (RelativeLayout)rootView.findViewById(ResourceHelper.getInstance(getActivity()).getId("shop_ad" + (i+1)));
                                    relativeLayout.addView(imageView);
                                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(!TextUtils.isEmpty(j.optString("sid"))) {
                                                Intent intent = new Intent(getActivity(), MerchantDetailActivity.class);
                                                intent.putExtra("id", j.optString("sid"));
                                                startActivity(intent);
                                            }else {

                                            }
                                        }
                                    });
                                }
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
                mScrollView.onRefreshComplete();
            }
        });
    }

    private void fetchPicAdData() {
        RequestParams params = new RequestParams(Urls.picAd);
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-result----" + result);
                if(result != null) {
                    try {
                        org.json.JSONObject json = new org.json.JSONObject(result);
                        if (json.optInt("code") == 100) {
                            org.json.JSONArray jsonArray = json.optJSONArray("data");
                            if(jsonArray != null && jsonArray.length() > 0) {
                                int size = jsonArray.length() > 4 ? 4 : jsonArray.length();
                                for(int i=0;i<size ;i++) {
                                    final JSONObject j = jsonArray.getJSONObject(i);
                                    ImageView imageView = new ImageView(getActivity());
                                    RelativeLayout.LayoutParams _params = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    _params.setMargins(2, 2, 2, 2);
                                    _params.addRule(RelativeLayout.CENTER_IN_PARENT);
                                    imageView.setLayoutParams(_params);
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    imageView.setPadding(2, 2, 2, 2);
                                    x.image().bind(imageView, j.optString("picPath"));
                                    RelativeLayout relativeLayout = (RelativeLayout)rootView.findViewById(ResourceHelper.getInstance(getActivity()).getId("pic_ad" + (i+1)));
                                    relativeLayout.addView(imageView);
                                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(!TextUtils.isEmpty(j.optString("sid"))) {
                                                Intent intent = new Intent(getActivity(), MerchantDetailActivity.class);
                                                intent.putExtra("id", j.optString("sid"));
                                                startActivity(intent);
                                            }else {

                                            }
                                        }
                                    });
                                }
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
                mScrollView.onRefreshComplete();
            }
        });
    }

    private void fetchMerchData() {
        mDatas.clear();
        RequestParams params = new RequestParams(Urls.shopList);
        params.addBodyParameter("page", String.valueOf(1));
        params.addBodyParameter("page_size", String.valueOf(Const.PAGE_SIZE_10));
        params.addBodyParameter("is_index", String.valueOf(1));
        params.addBodyParameter("is_rec", String.valueOf(0));
        params.addBodyParameter("cid", String.valueOf(0));
        params.addBodyParameter("keyword", String.valueOf(""));
        params.addBodyParameter("lat", String.valueOf(0));
        params.addBodyParameter("lng", String.valueOf(0));
        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-result----" + result);
                if(result != null) {
                    try {
                        org.json.JSONObject json = new org.json.JSONObject(result);
                        if (json.optInt("code") == 100) {
                            org.json.JSONObject jsonObj = json.optJSONObject("data");
                            JSONArray jsonArray = jsonObj.optJSONArray("rows");
                            List<Merchant> list = Merchant.parseJsonForList(jsonArray);
                            if(list != null && list.size() > 0) {
                                mDatas.addAll(list);
                            }
                        }
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
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
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_search_layout:
                Intent intent1 = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent1);
                break;
            case R.id.index_home_tips_arrow:
                Intent intent2 = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent2, CaptureActivity.REQ_SCAN_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == CaptureActivity.REQ_SCAN_CODE) {
                String result = data.getStringExtra("result");// Scan result Not Match
                String pay = data.getStringExtra("pay");
                if(!TextUtils.isEmpty(result)) {
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
