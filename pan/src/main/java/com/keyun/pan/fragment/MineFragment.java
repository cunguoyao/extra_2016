package com.keyun.pan.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.keyun.pan.R;
import com.keyun.pan.activity.AdviceActivity;
import com.keyun.pan.activity.LoginActivity;
import com.keyun.pan.activity.MainActivity;
import com.keyun.pan.activity.MyInfoActivity;
import com.keyun.pan.activity.MyMessageActivity;
import com.keyun.pan.activity.SettingActivity;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.widget.CircularImage;
import com.keyun.pan.widget.CustomDialog;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/6/2.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    private final String TAG = MineFragment.class.getName();
    private View rootView;

    @ViewInject(R.id.user_avatar)
    private CircularImage userAvatar;
    @ViewInject(R.id.user_name)
    private TextView userName;

    @ViewInject(R.id.scrollView)
    private PullToRefreshScrollView scrollView;
    @ViewInject(R.id.more_layout_my_message)
    private RelativeLayout layoutMyMessage;
    @ViewInject(R.id.more_layout_clean)
    private RelativeLayout layoutClean;
    @ViewInject(R.id.more_layout_advice)
    private RelativeLayout layoutAdvice;
    @ViewInject(R.id.more_layout_version)
    private RelativeLayout layoutVersion;
    @ViewInject(R.id.more_layout_setting)
    private RelativeLayout layoutSetting;
    @ViewInject(R.id.mine_layout_logout)
    private RelativeLayout layoutLogout;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
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
                ProgressDialogUtils.dismissProgressBar();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                ((MainActivity)getActivity()).exit();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
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
        x.view().inject(this, rootView);   //注入控件

        scrollView.setMode(PullToRefreshBase.Mode.DISABLED);
        if (getDefaultUser() != null) {
            userName.setText(user.getAccount());
        }
        userAvatar.setOnClickListener(this);
        userName.setOnClickListener(this);
        layoutMyMessage.setOnClickListener(this);
        layoutClean.setOnClickListener(this);
        layoutAdvice.setOnClickListener(this);
        layoutVersion.setOnClickListener(this);
        layoutSetting.setOnClickListener(this);
        layoutLogout.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_avatar:
            case R.id.user_name:
                Intent intent1 = new Intent(getActivity(), MyInfoActivity.class);
                startActivity(intent1);
                break;
            case R.id.more_layout_my_message:
                Intent intent2 = new Intent(getActivity(), MyMessageActivity.class);
                startActivity(intent2);
                break;
            case R.id.more_layout_clean:
                ProgressDialogUtils.showProgressDialog(getActivity(), false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        x.image().clearMemCache();
                        x.image().clearCacheFiles();
                        ProgressDialogUtils.dismissProgressBar();
                        Toast.makeText(getActivity(), "缓存已清空", Toast.LENGTH_SHORT).show();
                    }
                }, 2000);
                break;
            case R.id.more_layout_advice:
                Intent intent3 = new Intent(getActivity(), AdviceActivity.class);
                startActivity(intent3);
                break;
            case R.id.more_layout_version:
                break;
            case R.id.more_layout_setting:
                Intent intent4 = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent4);
                break;
            case R.id.mine_layout_logout:
                popExitDialog();
                break;
        }
    }

    private void popExitDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("确认要退出当前账号？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        CustomDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void logout() {
        ProgressDialogUtils.showProgressDialog(getActivity(), true);
        RequestParams params = new RequestParams(Urls.logout());
        params.addBodyParameter("UserToken", user.getToken());
        params.setCharset("gbk");
        for(KeyValue k : params.getQueryStringParams()) {
            LogUtil.e("----key:" + k.key + "--value:" + k.value);
        }
        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        result = new String(result.getBytes("GBK"), "utf-8");
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if(json.optInt("ErrorCode") == 401) {
                            String msg = json.optString("ErrorMsg", "请求失败");
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            exitToLogin();
                        }
                        if (json.optInt("ErrCode") == 0) {
                            handler.sendEmptyMessage(1);
                        }else {
                            handler.sendEmptyMessage(1);
                        }
                    } catch (Exception e) {
                        onError(e, true);
                        handler.sendEmptyMessage(1);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("---onCancelled-----" + "onCancelled");
            }

            @Override
            public void onFinished() {
                LogUtil.e("---onFinished-----" + "onFinished");
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
