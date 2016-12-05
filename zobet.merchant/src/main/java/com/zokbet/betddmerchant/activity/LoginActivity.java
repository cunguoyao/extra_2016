package com.zokbet.betddmerchant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zokbet.betddmerchant.R;
import com.zokbet.betddmerchant.app.Urls;
import com.zokbet.betddmerchant.data.User;
import com.zokbet.betddmerchant.widget.ProgressDialogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.common.util.MD5;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_login)
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    public static final int LoginRequestCode = 1357;

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.title_right)
    private Button goRegister;
    @ViewInject(R.id.username)
    private EditText username;
    @ViewInject(R.id.password)
    private EditText password;
    @ViewInject(R.id.loginBtn)
    private Button loginBn;

    private String userName;
    private String passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleText.setText("登录");
        username.requestFocus();
        goRegister.setVisibility(View.INVISIBLE);
        titleBack.setOnClickListener(this);
        goRegister.setOnClickListener(this);
        loginBn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_right:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.loginBtn:
                ProgressDialogUtils.showProgressDialog(this, true);
                userName = username.getText().toString();
                passWord = password.getText().toString();
                RequestParams params = new RequestParams(Urls.login);
                params.addBodyParameter("username", userName);
                params.addBodyParameter("password", MD5.md5(passWord));
                x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.e("---onSuccess-----" + "onSuccess");
                        LogUtil.e("---onSuccess-result----" + result);
                        if(result != null) {
                            try {
                                JSONObject json = new JSONObject(result);
                                if(json.optInt("code") == 100) {
                                    User user = new User();
                                    JSONObject jsonObj = json.optJSONObject("data");
                                    user.setId(jsonObj.optString("sid", ""));
                                    user.setUsername(jsonObj.optString("loginName", ""));
                                    user.setPassword(MD5.md5(passWord));
                                    user.setMerchName(jsonObj.optString("title"));
                                    user.setAvatar(jsonObj.optString("avatar", ""));
                                    user.setToken(jsonObj.optString("token", ""));
                                    user.setPayPwdBlank(jsonObj.optInt("payPwdBlank"));
                                    user.setExpiresIn(jsonObj.optLong("expiresIn", 0));
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
                                    setResult(RESULT_OK);
                                    finish();
                                }else {
                                    String msg = json.optString("msg", "请求失败");
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e) {
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
                break;
        }
    }

}
