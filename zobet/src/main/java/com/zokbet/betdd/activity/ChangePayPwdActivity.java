package com.zokbet.betdd.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zokbet.betdd.R;
import com.zokbet.betdd.app.Urls;
import com.zokbet.betdd.data.User;
import com.zokbet.betdd.widget.ProgressDialogUtils;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_change_pay_pwd)
public class ChangePayPwdActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.fav_btn)
    private ImageButton favBtn;
    @ViewInject(R.id.layout_old_password)
    private RelativeLayout oldPasswordLayout;
    @ViewInject(R.id.old_password)
    private EditText oldPassword;
    @ViewInject(R.id.new_password)
    private EditText newPassword;
    @ViewInject(R.id.renew_password)
    private EditText renewPassword;
    @ViewInject(R.id.submitBtn)
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleText.setText("修改支付密码");
        favBtn.setVisibility(View.INVISIBLE);
        titleBack.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        if(getDefaultUser().getPayPwdBlank() == 1) {
            oldPasswordLayout.setVisibility(View.GONE);
        }else {
            oldPasswordLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.submitBtn:
                String oldPwd = oldPassword.getText().toString();
                String newPwd = newPassword.getText().toString();
                String renewPwd = renewPassword.getText().toString();
                if(getDefaultUser().getPayPwdBlank() == 0 && TextUtils.isEmpty(oldPwd)) {
                    Toast.makeText(ChangePayPwdActivity.this, "请输入原密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(newPwd)) {
                    Toast.makeText(ChangePayPwdActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(renewPwd)) {
                    Toast.makeText(ChangePayPwdActivity.this, "请确认新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isPayPassword(newPwd)) {
                    Toast.makeText(this, "支付密码必须是6-12位数字或字母，特殊字符只能有“~!@#$”", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!newPwd.equals(renewPwd)) {
                    Toast.makeText(ChangePayPwdActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                ProgressDialogUtils.showProgressDialog(this, true);
                RequestParams params = new RequestParams(Urls.changePayPwd);
                params.addBodyParameter("token", getDefaultUser().getToken());
                if(getDefaultUser().getPayPwdBlank() != 1) {//PayPwdBlank!=1 表示设置了密码
                    params.addBodyParameter("payPwd", MD5.md5(oldPwd));
                }
                params.addBodyParameter("newPayPwd", MD5.md5(newPwd));
                x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.e("---onSuccess-----" + "onSuccess");
                        LogUtil.e("---onSuccess-result----" + result);
                        if(result != null) {
                            try {
                                JSONObject json = new JSONObject(result);
                                if(json.optInt("code") == 100) {
                                    Toast.makeText(ChangePayPwdActivity.this, "密码修改成功", Toast.LENGTH_LONG).show();
                                    try {
                                        User user = getDefaultUser();
                                        if(user != null) {
                                            user.setPayPwdBlank(0);
                                        }
                                        db.saveOrUpdate(user);
                                    }catch (DbException e) {
                                    }
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1000);
                                }else {
                                    String msg = json.optString("msg", "请求失败");
                                    Toast.makeText(ChangePayPwdActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e) {
                                onError(e, true);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        LogUtil.e("---onError-----" + "onError");
                        Toast.makeText(ChangePayPwdActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
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

    public static boolean isPayPassword(String password) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^([a-zA-Z0-9~!@#$]{6,12})$");
            Matcher m = p.matcher(password);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

}
