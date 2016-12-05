package com.zokbet.betddmerchant.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zokbet.betddmerchant.R;
import com.zokbet.betddmerchant.app.Urls;
import com.zokbet.betddmerchant.widget.CustomDialog;
import com.zokbet.betddmerchant.widget.ProgressDialogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.common.util.MD5;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_register)
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    public static final int LoginRequestCode = 1357;

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;

    @ViewInject(R.id.invite)
    private EditText inviter;
    @ViewInject(R.id.username)
    private EditText username;
    @ViewInject(R.id.password)
    private EditText password;
    @ViewInject(R.id.repassword)
    private EditText repassword;
    @ViewInject(R.id.code)
    private EditText codeEdit;
    @ViewInject(R.id.send_code)
    private Button sendCodeBtn;
    @ViewInject(R.id.registerBtn)
    private Button registerBtn;

    private String inviterName;
    private String userName;
    private String passWord;
    private String rePassword;
    private String code;

    private TimeCount timeCount;
    private int sendCodeTimes = 0;

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish() {//计时完毕时触发
            sendCodeBtn.setText("点击重新发送");
            sendCodeBtn.setClickable(true);
            sendCodeBtn.setBackgroundResource(R.drawable.send_code_btn);
        }
        @Override
        public void onTick(long millisUntilFinished){//计时过程显示
            sendCodeBtn.setClickable(false);
            sendCodeBtn.setText(millisUntilFinished /1000+" s");
            sendCodeBtn.setBackgroundResource(R.drawable.send_code_press);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleText.setText("注册");

        if(getDefaultUser() != null) {
            inviter.setText(user.getUsername());
            inviter.setEnabled(false);
            username.requestFocus();
        }else {
            inviter.setEnabled(true);
            inviter.requestFocus();
        }
        timeCount = new TimeCount(60000, 1000);//构造CountDownTimer对象
        titleBack.setOnClickListener(this);
        sendCodeBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.send_code:
                givenValue();
                if(TextUtils.isEmpty(userName)) {
                    Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isMobileNO(userName)) {
                    Toast.makeText(this, "请正确输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendSms();
                break;
            case R.id.registerBtn:
                givenValue();
                if(!isMobileNO(userName)) {
                    Toast.makeText(this, "请正确输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(passWord)) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isPassword(passWord)) {
                    Toast.makeText(this, "密码必须是6-12位数字或字母", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!passWord.equals(rePassword)) {
                    Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(code)) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sendCodeTimes < 1) {
                    Toast.makeText(this, "请发送验证码后再提交", Toast.LENGTH_SHORT).show();
                    return;
                }
                register();
                break;
        }
    }

    private void givenValue() {
        inviterName = inviter.getText().toString();
        userName = username.getText().toString();
        passWord = password.getText().toString();
        rePassword = repassword.getText().toString();
        code = codeEdit.getText().toString();
        if(!TextUtils.isEmpty(inviterName)) {
            inviterName = inviterName.trim();
        }else {
            inviterName = "";
        }
        if(!TextUtils.isEmpty(userName)) {
            userName = userName.trim();
        }
        if(!TextUtils.isEmpty(passWord)) {
            passWord = passWord.trim();
        }
        if(!TextUtils.isEmpty(rePassword)) {
            rePassword = rePassword.trim();
        }
        if(!TextUtils.isEmpty(code)) {
            code = code.trim();
        }
    }

    private void sendSms() {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.sendSms);
        params.addBodyParameter("mobile", userName);
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                LogUtil.e("---onSuccess-result----" + result);
                if(result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if(json.optInt("code") == 100) {
                            String msg = json.optString("msg", "发送成功");
                            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }else {
                            String msg = json.optString("msg", "发送失败");
                            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("---onError-----" + "onError");
                Toast.makeText(RegisterActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.dismissProgressBar();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("---onCancelled-----" + "onCancelled");
            }

            @Override
            public void onFinished() {
                LogUtil.e("---onFinished-----" + "onFinished");
                sendCodeTimes ++;
                timeCount.start();
                ProgressDialogUtils.dismissProgressBar();
            }
        });
    }

    private void register() {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.register);
        params.addBodyParameter("inviter", inviterName);
        params.addBodyParameter("loginName", userName);
        params.addBodyParameter("password", MD5.md5(passWord));
        params.addBodyParameter("registerValicode", code);
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                LogUtil.e("---onSuccess-result----" + result);
                if(result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if(json.optInt("code") == 100) {
                            String msg = json.optString("msg", "注册成功！");
                            CustomDialog.Builder builder = new CustomDialog.Builder(RegisterActivity.this);
                            builder.setTitle("注册");
                            builder.setMessage(msg);
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            builder.setNegativeButton(null, null);
                            builder.create().show();
                        }else {
                            String msg = json.optString("msg", "注册失败");
                            CustomDialog.Builder builder = new CustomDialog.Builder(RegisterActivity.this);
                            builder.setTitle("注册");
                            builder.setMessage(msg);
                            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton(null, null);
                            builder.create().show();
                        }
                    }catch (JSONException e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("---onError-----" + "onError");
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
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

    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        try {
//			Pattern p = Pattern
//					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
            Pattern p = Pattern.compile("^0?1\\d{10}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean isPassword(String password) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^([a-zA-Z0-9]{6,12})$");
            Matcher m = p.matcher(password);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
}
