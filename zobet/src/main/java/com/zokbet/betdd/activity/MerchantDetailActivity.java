package com.zokbet.betdd.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.zokbet.betdd.R;
import com.zokbet.betdd.app.Urls;
import com.zokbet.betdd.data.Merchant;
import com.zokbet.betdd.data.MerchantDetail;
import com.zokbet.betdd.data.MerchantProps;
import com.zokbet.betdd.widget.MeiTuanHeaderLayout;
import com.zokbet.betdd.widget.MyWebView;
import com.zokbet.betdd.widget.ProgressDialogUtils;

import org.json.JSONException;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_mechant_detail)
public class MerchantDetailActivity extends BaseActivity {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.fav_btn)
    private ImageButton favBtn;
    @ViewInject(R.id.imageFace)
    private ImageView imageFace;
    @ViewInject(R.id.photo_size_layout)
    private RelativeLayout photoSizeLayout;
    @ViewInject(R.id.photo_size)
    private TextView photoSize;
    @ViewInject(R.id.ri_merchantName)
    private TextView merchantName;
    @ViewInject(R.id.ri_score)
    private RatingBar merchantScore;
    @ViewInject(R.id.ri_score_word)
    private TextView merchantScoreWord;
    @ViewInject(R.id.ri_average)
    private TextView merchantAverage;
    @ViewInject(R.id.ri_cate_area)
    private TextView merchantCateArea;
    @ViewInject(R.id.ri_location)
    private TextView merchantLocation;
    @ViewInject(R.id.ri_telephone)
    private ImageButton merchantTel;
    @ViewInject(R.id.layout_webview)
    private LinearLayout layoutWebView;
    @ViewInject(R.id.webview)
    private MyWebView webView;
    @ViewInject(R.id.layout_more)
    private LinearLayout layoutMore;

    @ViewInject(R.id.scrollView)
    private PullToRefreshScrollView mScrollView;

    private ImageOptions imageOptions;
    private Merchant merchant;
    private String merchantId;
    private MerchantDetail detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        merchant = (Merchant)getIntent().getSerializableExtra("body");
        merchantId = getIntent().getStringExtra("id");
        if(merchant == null && TextUtils.isEmpty(merchantId)) {
            Toast.makeText(this, "商户不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        titleText.setText(merchant == null ? "商家详情" : merchant.getName());
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        favBtn.setVisibility(View.INVISIBLE);

        this.imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(160), DensityUtil.dip2px(160))//图片大小
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.list_thumbnail_none_m)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.list_thumbnail_none_m)//加载失败后默认显示图片
                .build();

        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                fetchData();
            }
        });
        mScrollView.setHeaderLayout(new MeiTuanHeaderLayout(this));
        mScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        fetchData();
    }

    private void fetchData() {
        ProgressDialogUtils.showRunManProgressDialog(this, true);
        if(merchant != null) {
            merchantId = merchant.getId();
        }
        RequestParams params = new RequestParams(Urls.shopDetail);
        params.addBodyParameter("sid", merchantId);
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-result----" + result);
                if(result != null) {
                    try {
                        org.json.JSONObject json = new org.json.JSONObject(result);
                        if (json.optInt("code") == 100) {
                            org.json.JSONObject jsonObj = json.optJSONObject("data");
                            detail = MerchantDetail.parseJson(jsonObj);
                            if(detail != null) {
                                fillDataPage(detail);
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
                ProgressDialogUtils.dismissRunManProgressBar();
            }
        });
    }

    private void fillDataPage(final MerchantDetail merchant) {

        titleText.setText(merchant.getName());
        x.image().bind(imageFace, merchant.getFace(), imageOptions);
        if(merchant.getImages() != null && merchant.getImages().size() > 0) {
            photoSizeLayout.setVisibility(View.VISIBLE);
            photoSize.setText(merchant.getImages().size() + "张");
            imageFace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MerchantDetailActivity.this, MerchantAlbumActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("photo", merchant.getImages());
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
        }else {
            photoSizeLayout.setVisibility(View.GONE);
        }
        merchantName.setText(merchant.getName());
        merchantScore.setRating((float) (merchant.getStar() / 20.0));
        merchantScoreWord.setText(merchant.getStar() + "分");
        merchantAverage.setText("人均:" + merchant.getAverage());
        if(merchant.getMerchantArea() != null) {
            merchantCateArea.setText(merchant.getArea().city + " " + merchant.getMerchantArea().area);
        }
        merchantLocation.setText(merchant.getAddress());
        merchantTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri= Uri.parse("tel:"+ merchant.getMobile());
                Intent intent5 = new Intent();
                intent5.setAction(Intent.ACTION_DIAL);
                intent5.setData(uri);
                startActivity(intent5);
            }
        });
        if(TextUtils.isEmpty(merchant.getDetail())) {
            layoutWebView.setVisibility(View.GONE);
        }else {
            layoutWebView.setVisibility(View.VISIBLE);
            webView.getSettings().setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);  //就是这句
            webView.loadData(merchant.getDetail(), "text/html; charset=UTF-8", null);//这种写法可以正确解码
        }
        if(merchant.getProps() != null && merchant.getProps().size() > 0) {
            layoutMore.setVisibility(View.VISIBLE);
            TextView textMore = new TextView(this);
            RelativeLayout.LayoutParams _paramsMore = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            _paramsMore.setMargins(10, 0, 0, 0);
            _paramsMore.addRule(RelativeLayout.CENTER_VERTICAL);
            textMore.setLayoutParams(_paramsMore);
            textMore.setText("更多服务");
            RelativeLayout linearLayoutMore = new RelativeLayout(this);
            linearLayoutMore.setBackgroundResource(R.drawable.normal_item_bg);
            linearLayoutMore.addView(textMore);
            layoutMore.addView(linearLayoutMore);
            for(MerchantProps prop : merchant.getProps()) {
                TextView text = new TextView(this);
                RelativeLayout.LayoutParams _params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                _params.setMargins(10, 0, 0, 0);
                _params.addRule(RelativeLayout.CENTER_VERTICAL);
                text.setLayoutParams(_params);
                text.setText(prop.toString());
                RelativeLayout linearLayout = new RelativeLayout(this);
                linearLayout.setBackgroundResource(R.drawable.normal_item_bg);
                linearLayout.addView(text);
                layoutMore.addView(linearLayout);
            }
        }else {
            layoutMore.setVisibility(View.GONE);
        }
    }
}
