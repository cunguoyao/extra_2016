package com.keyun.pan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.keyun.pan.R;
import com.keyun.pan.app.BaseApplication;
import com.keyun.pan.data.User;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.widget.ProgressDialogUtils;
import com.keyun.pan.widget.SoftKeyboardLinearLayout;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_login)
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    public static final String AutoLogin = "auto_login";

    @ViewInject(R.id.root)
    private SoftKeyboardLinearLayout rootView;
    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.title_right)
    private Button goSetting1;
    @ViewInject(R.id.go_setting)
    private TextView goSetting2;
    @ViewInject(R.id.username)
    private EditText username;
    @ViewInject(R.id.password)
    private EditText password;
    @ViewInject(R.id.loginBtn)
    private Button loginBn;

    private int autoLogin;
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    InputMethodManager inputManager = (InputMethodManager)username.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(get() != null) {
                        password.setFocusable(true);
                        password.setFocusableInTouchMode(true);
                        password.requestFocus();
                        inputManager.showSoftInput(password, 0);
                    }else {
                        username.setFocusable(true);
                        username.setFocusableInTouchMode(true);
                        username.requestFocus();
                        inputManager.showSoftInput(username, 0);
                    }
                    break;
                case 1:
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    ProgressDialogUtils.showProgressDialog(LoginActivity.this, true);
                    int t = msg.arg1;
                    String[] pp1 = (String[])msg.obj;
                    Message message = new Message();
                    message.what = 3;
                    message.obj = pp1;
                    handler.sendMessageDelayed(message, t==0?0:3000);
                    break;
                case 3:
                    String[] pp2 = (String[])msg.obj;
                    doLogin(pp2[0], pp2[1]);
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        autoLogin = getIntent().getIntExtra(AutoLogin, 0);
        titleText.setText("安全云盘");
        if(get() != null) {
            username.setText(get().getAccount());
            password.requestFocus();
        }
        titleBack.setOnClickListener(this);
        goSetting1.setOnClickListener(this);
        goSetting2.setOnClickListener(this);
        loginBn.setOnClickListener(this);
        if(autoLogin == 1) {
            password.setText(get().getPassword());
            String userName = username.getText().toString();
            String passWord = password.getText().toString();
            if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)) {
                ProgressDialogUtils.dismissProgressBar();
                Toast.makeText(LoginActivity.this, "请输入用户名或密码", Toast.LENGTH_SHORT).show();
                return;
            }
            Message message = new Message();
            message.what = 2;
            message.arg1 = 1;
            message.obj = new String[]{userName, passWord};
            handler.sendMessage(message);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.loginBtn:
                String userName = username.getText().toString();
                String passWord = password.getText().toString();
                if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)) {
                    ProgressDialogUtils.dismissProgressBar();
                    Toast.makeText(LoginActivity.this, "请输入用户名或密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                Message message = new Message();
                message.what = 2;
                message.arg1 = 0;
                message.obj = new String[]{userName, passWord};
                handler.sendMessage(message);
                break;
            case R.id.title_right:
            case R.id.go_setting:
                Intent intent = new Intent(this, RequestSettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void doLogin(final String userName, final String passWord) {
        RequestParams params = new RequestParams(Urls.login());
        params.addBodyParameter("UserID", userName);
        params.addBodyParameter("UserPwd", passWord);
        params.setCharset("GB2312");
        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if(result != null) {
                    try {
                        //result = new String(result.getBytes("GB2312"), "utf-8");
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if(json.optInt("ErrCode") == 0) {
                            User user = new User();
                            user.setId(json.optString("UserToken", ""));
                            user.setAccount(userName);
                            user.setPassword(passWord);
                            user.setUserName(json.optString("UserName"));
                            user.setUserGroupName(json.optString("UserGroupName"));
                            user.setFilterType(json.optInt("FilterType"));
                            user.setFilterDocType(json.optString("FilterDocType"));
                            user.setServerID(json.optString("ServerID"));
                            user.setAvatar(json.optString("avatar", ""));
                            user.setToken(json.optString("UserToken", ""));
                            user.setExpiresIn(json.optLong("expiresIn", 0));
                            user.setLoginTime(System.currentTimeMillis());
                            //user.setRmbMon(0.00);
                            //user.setBitMon(100.00);
                            user.setDefaultAccount(1);
                            user.setRemember(1);
                            try {
                                db.saveOrUpdate(user);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            registerPush();
                        }else {
                            String msg = json.optString("ErrMsg", "请求失败");
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("---onError-----" + "onError");
                Toast.makeText(LoginActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.dismissProgressBar();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("---onCancelled-----" + "onCancelled");
            }

            @Override
            public void onFinished() {
                LogUtil.e("---onFinished-----" + "onFinished");
                ProgressDialogUtils.dismissProgressBar();
            }
        });
    }

    private void registerPush() {
        RequestParams params = new RequestParams(Urls.pushregister());
        params.addBodyParameter("token", getDefaultUser().getToken());
        params.addBodyParameter("platformId", BaseApplication.getInstance().getJPushId());
        params.addBodyParameter("platform", "2");//平台，1是ios，2是安卓
        params.setCharset("GB2312");
        x.http().request(HttpMethod.GET, params, new MyHttpCallback(this) {
            @Override
            public void onSucceed(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if (json.optInt("errorcode") == 1) {
                            handler.sendEmptyMessageDelayed(1, 100);
                        } else {
                            handler.sendEmptyMessageDelayed(1, 100);
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.sendEmptyMessageDelayed(1, 100);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                handler.sendEmptyMessageDelayed(1, 100);
            }

            @Override
            public void onFinished() {

            }
        });
    }

    protected User get() {
        User user = null;
        try {
            user = db.selector(User.class).orderBy("login_time", true).findFirst();
        }catch (DbException e) {
        }
        return user;
    }

}
