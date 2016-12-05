package com.zokbet.betddmerchant.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zokbet.betddmerchant.R;
import com.zokbet.betddmerchant.app.Urls;
import com.zokbet.betddmerchant.data.MoneyNumber;
import com.zokbet.betddmerchant.data.User;
import com.zokbet.betddmerchant.utils.QrUtil;
import com.zokbet.betddmerchant.utils.Unicode;
import com.zokbet.betddmerchant.widget.CircularImage;
import com.zokbet.betddmerchant.widget.CustomDialog;
import com.zokbet.betddmerchant.widget.ProgressDialogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(value = R.layout.activity_main)
public class MainActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.refresh_btn)
    private ImageButton refreshBtn;
    @ViewInject(R.id.progress)
    private ProgressBar progressBar;
    @ViewInject(R.id.my_info_layout)
    private RelativeLayout myInfoLayout;
    @ViewInject(R.id.user_avatar)
    private CircularImage userAvatar;
    @ViewInject(R.id.user_name)
    private TextView userName;
    @ViewInject(R.id.user_phone)
    private TextView userPhone;
    @ViewInject(R.id.user_money)
    private TextView userMoney;
    @ViewInject(R.id.pic_ad0)
    private LinearLayout todayMoneyLayout;
    @ViewInject(R.id.pic_ad1)
    private LinearLayout cjMoneyLayout;
    @ViewInject(R.id.pic_ad2)
    private LinearLayout cjOrderLayout;
    @ViewInject(R.id.cj_money)
    private TextView cjMoney;
    @ViewInject(R.id.cj_order)
    private TextView cjOrder;
    @ViewInject(R.id.qrcodeImage_layout)
    private RelativeLayout qrcodeImageLayout;
    @ViewInject(R.id.qrcodeImage)
    private ImageView qrcodeImage;

    @ViewInject(R.id.mine_layout_invite)
    private RelativeLayout inviteLayout;
    @ViewInject(R.id.mine_layout_about_us)
    private RelativeLayout aboutUsLayout;
    @ViewInject(R.id.invite_word)
    private TextView inviteWord;
    @ViewInject(R.id.logout_layout)
    private RelativeLayout logoutLayout;

    private User user;
    private ImageOptions imageOptions;
    private long exitTime=0;//两次按返回退出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        this.imageOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(160), DensityUtil.dip2px(160))//图片大小
                //.setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.bg_loading_poi_list)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.bg_loading_poi_list)//加载失败后默认显示图片
                .build();

        user = getDefaultUser();
        initView();
        if(user != null) {
            fetchData();
        }
        refreshBtn.setOnClickListener(this);
        myInfoLayout.setOnClickListener(this);
        todayMoneyLayout.setOnClickListener(this);
        cjMoneyLayout.setOnClickListener(this);
        cjOrderLayout.setOnClickListener(this);
        inviteLayout.setOnClickListener(this);
        aboutUsLayout.setOnClickListener(this);
        logoutLayout.setOnClickListener(this);
    }

    private void initView() {
        if(user == null) {
            refreshBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            userName.setText("请点击登录");
            userPhone.setText("");
            userMoney.setText("0.0");
            cjMoney.setText("0.0");
            cjOrder.setText("0");
            qrcodeImageLayout.setVisibility(View.GONE);
            inviteWord.setText("用户注册");
            logoutLayout.setVisibility(View.GONE);
        }else {
            refreshBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            userName.setText(user.getMerchName());
            userPhone.setText(user.getUsername());
            qrcodeImageLayout.setVisibility(View.VISIBLE);
            inviteWord.setText("邀请注册");
            logoutLayout.setVisibility(View.VISIBLE);

            x.image().bind(userAvatar, user.getAvatar(), imageOptions);
            String qrContent = "merch://" + user.getId() + "," + Unicode.string2Unicode(user.getMerchName());
            BitmapDrawable ob = new BitmapDrawable(getResources(), QrUtil.generateQRCode(qrContent));
            qrcodeImage.setBackground(ob);
        }
    }

    private void fetchData() {
        refreshBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.balance);
        params.addBodyParameter("token", getDefaultUser().getToken());
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-result----" + result);
                if(result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if(json.optInt("code") == 100) {
                            MoneyNumber number = new MoneyNumber();
                            JSONObject jsonObj = json.optJSONObject("data");
                            number.setSid(user.getId());
                            number.setTotalMoney(jsonObj.optString("total", ""));
                            number.setCjMoney(jsonObj.optString("day", ""));
                            number.setCjOrder(jsonObj.optString("totalNum", ""));
                            try {
                                db.saveOrUpdate(number);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }else {
                            String msg = json.optString("msg", "请求失败");
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.dismissProgressBar();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
                handler.sendEmptyMessageDelayed(4, 1000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh_btn:
                fetchData();
                break;
            case R.id.my_info_layout:
                if(getDefaultUser() == null) {
                    Intent intent0 = new Intent(this, LoginActivity.class);
                    startActivityForResult(intent0, LoginActivity.LoginRequestCode);
                }
                break;
            case R.id.pic_ad0:
            case R.id.pic_ad1:
            case R.id.pic_ad2:
                if(getDefaultUser() != null) {
                    Intent intent1 = new Intent(this, MyPaymentActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.mine_layout_invite:
                Intent intent2 = new Intent(this, RegisterActivity.class);
                startActivity(intent2);
                break;
            case R.id.mine_layout_about_us:
                Intent intent3 = new Intent(this, AboutUsActivity.class);
                startActivity(intent3);
                break;
            case R.id.logout_layout:
                CustomDialog.Builder builder = new CustomDialog.Builder(this);
                builder.setTitle("注销");
                builder.setMessage("请确认需要注销当前账号吗？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logout();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LoginActivity.LoginRequestCode:
                if (resultCode == Activity.RESULT_OK) {
                    ProgressDialogUtils.showProgressDialog(this, true);
                    handler.sendEmptyMessageDelayed(2, 100);
                    handler.sendEmptyMessageDelayed(3, 500);
                }
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1) {
                try {
                    user = getDefaultUser();
                    if(user != null) {
                        user.setDefaultAccount(0);
                        user.setToken("");
                    }
                    db.saveOrUpdate(user);
                }catch (DbException e) {
                }
                handler.sendEmptyMessageDelayed(2, 1000);
            }else if(msg.what == 2) {
                user = getDefaultUser();
                initView();
                ProgressDialogUtils.dismissProgressBar();
            }else if(msg.what == 3) {
                fetchData();
            }else if(msg.what == 4) {
                MoneyNumber n = null;
                try {
                    n = db.selector(MoneyNumber.class).where("sid", "=", user.getId()).findFirst();
                }catch (DbException e) {
                }
                if(n != null) {
                    userMoney.setText(n.getCjMoney());
                    cjMoney.setText(n.getTotalMoney());
                    cjOrder.setText(n.getCjOrder());
                }
                refreshBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                ProgressDialogUtils.dismissProgressBar();
            }
        }
    };
    private void logout() {
        ProgressDialogUtils.showProgressDialog(this, true);
        handler.sendEmptyMessageDelayed(1, 100);
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
