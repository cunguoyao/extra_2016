package com.zokbet.betdd.ui;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zokbet.betdd.activity.ChangePayPwdActivity;
import com.zokbet.betdd.activity.LoginActivity;
import com.zokbet.betdd.activity.MerchantListActivity;
import com.zokbet.betdd.activity.MyPaymentActivity;
import com.zokbet.betdd.R;
import com.zokbet.betdd.activity.RegisterActivity;
import com.zokbet.betdd.app.Urls;
import com.zokbet.betdd.data.User;
import com.zokbet.betdd.widget.CircularImage;
import com.zokbet.betdd.widget.CustomDialog;
import com.zokbet.betdd.widget.ProgressDialogUtils;

import org.json.JSONException;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


/**
 * Created by cunguoyao on 2016/5/15.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    private final String TAG = MineFragment.class.getName();
    private View rootView;

    @ViewInject(R.id.user_avatar)
    private CircularImage userAvatarImage;
    @ViewInject(R.id.user_name)
    private TextView userNameText;
    @ViewInject(R.id.btbAmount)
    private TextView amountText;
    @ViewInject(R.id.my_info_layout)
    private RelativeLayout myInfoLayout;
    @ViewInject(R.id.mine_layout_order)
    private RelativeLayout myOrderLayout;
    @ViewInject(R.id.mine_layout_invite)
    private RelativeLayout myInviteLayout;
    @ViewInject(R.id.mine_layout_paypass)
    private RelativeLayout myPayPasswordLayout;
    @ViewInject(R.id.mine_layout_recommend)
    private RelativeLayout myRecommendLayout;
    @ViewInject(R.id.mine_layout_contact)
    private RelativeLayout myContactLayout;
    @ViewInject(R.id.logout_layout)
    private RelativeLayout myLogoutLayout;

    @ViewInject(R.id.invite_word)
    private TextView inviteWord;

    private ImageOptions imageOptions;

    public MineFragment() {
        LogUtil.e(TAG + "--------------new");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(160), DensityUtil.dip2px(160))//图片大小
                .setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.list_thumbnail_none_m)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.list_thumbnail_none_m)//加载失败后默认显示图片
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        LogUtil.e(TAG + "------------onCreateView----");
        x.view().inject(this, rootView);

        return rootView;
    }

    private void fetchAmount() {
        RequestParams params = new RequestParams(Urls.balance);
        params.addBodyParameter("token", getDefaultUser().getToken());
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
                            String btbAmount = json.optJSONObject("data").optString("btbAmount");
                            amountText.setVisibility(View.VISIBLE);
                            amountText.setText("余额:" + btbAmount);
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
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        user = getDefaultUser();
        if(user != null) {
            inviteWord.setText("邀请注册");
            x.image().bind(userAvatarImage, user.getAvatar(), imageOptions);
            userNameText.setText(user.getUsername());
            myLogoutLayout.setVisibility(View.VISIBLE);
            fetchAmount();
        }else {
            inviteWord.setText("注册用户");
            myLogoutLayout.setVisibility(View.GONE);
        }
        myInfoLayout.setOnClickListener(this);
        myOrderLayout.setOnClickListener(this);
        myInviteLayout.setOnClickListener(this);
        myRecommendLayout.setOnClickListener(this);
        myPayPasswordLayout.setOnClickListener(this);
        myContactLayout.setOnClickListener(this);
        myLogoutLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_info_layout:
                if(getDefaultUser() == null) {
                    Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent1, LoginActivity.LoginRequestCode);
                }else {

                }
                break;
            case R.id.mine_layout_order:
                if(getDefaultUser() != null) {
                    Intent intent2 = new Intent(getActivity(), MyPaymentActivity.class);
                    startActivity(intent2);
                }
                break;
            case R.id.mine_layout_paypass:
                if(getDefaultUser() != null) {
                    Intent intent3 = new Intent(getActivity(), ChangePayPwdActivity.class);
                    startActivity(intent3);
                }
                break;
            case R.id.mine_layout_recommend:
                Intent intent4 = new Intent(getActivity(), MerchantListActivity.class);
                intent4.putExtra("is_rec", 1);
                startActivity(intent4);
                break;
            case R.id.mine_layout_contact:
                Uri uri= Uri.parse("tel:"+ getResources().getString(R.string.dial_telphone));
                Intent intent5 = new Intent();
                intent5.setAction(Intent.ACTION_DIAL);
                intent5.setData(uri);
                startActivity(intent5);
                break;
            case R.id.mine_layout_invite:
                Intent intent6 = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent6);
                break;
            case R.id.logout_layout:
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
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

    private void logout() {
        ProgressDialogUtils.showProgressDialog(getActivity(), true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    user = getDefaultUser();
                    if(user != null) {
                        user.setDefaultAccount(0);
                        user.setToken("");
                    }
                    db.saveOrUpdate(user);
                }catch (DbException e) {
                }
                userNameText.setText("请点击登录");
                inviteWord.setText("注册用户");
                userAvatarImage.setImageResource(R.drawable.avatar_custom_default);
                amountText.setVisibility(View.INVISIBLE);
                myLogoutLayout.setVisibility(View.GONE);

                ProgressDialogUtils.dismissProgressBar();
            }
        }, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LoginActivity.LoginRequestCode:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        user = db.selector(User.class).where("default_acc", "=", 1).findFirst();
                    }catch (DbException e) {
                    }
                    if(user != null) {
                        x.image().bind(userAvatarImage, user.getAvatar(), imageOptions);
                        userNameText.setText(user.getUsername());
                    }
                }
                break;
        }
    }

}
