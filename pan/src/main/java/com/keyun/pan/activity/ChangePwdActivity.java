package com.keyun.pan.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.keyun.pan.R;
import com.keyun.pan.app.Urls;
import com.keyun.pan.data.MyInfo;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/7/9.
 */
@ContentView(value = R.layout.activity_change_pwd)
public class ChangePwdActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.old_pwd)
    private EditText oldPwdEdit;
    @ViewInject(R.id.new_password)
    private EditText newPwdEdit;
    @ViewInject(R.id.re_password)
    private EditText rePwdEdit;
    @ViewInject(R.id.fav_btn)
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        titleBack.setOnClickListener(this);
        titleText.setText("修改密码");
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.submitBtn:
                String oldPwd = oldPwdEdit.getText().toString();
                String newPwd = newPwdEdit.getText().toString();
                String renewPwd = rePwdEdit.getText().toString();
                if(oldPwd != null) {
                    oldPwd = oldPwd.trim();
                }
                if(newPwd != null) {
                    newPwd = newPwd.trim();
                }
                if(renewPwd != null) {
                    renewPwd = renewPwd.trim();
                }
                if(TextUtils.isEmpty(oldPwd)) {
                    Toast.makeText(ChangePwdActivity.this, "请输入原密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(newPwd)) {
                    Toast.makeText(ChangePwdActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!newPwd.equals(renewPwd)) {
                    Toast.makeText(ChangePwdActivity.this, "两次输入新密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                changePwd(oldPwd, newPwd);
                break;
        }
    }

    private void changePwd(String oldPwd, String newPwd) {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.updateUserPwd());
        params.addBodyParameter("UserToken", getDefaultUser().getToken());
        params.addBodyParameter("CheckPWD", oldPwd);
        params.addBodyParameter("UserPwd", newPwd);
        params.setCharset("utf-8");
        x.http().request(HttpMethod.POST, params, new MyHttpCallback(this) {
            @Override
            public void onSucceed(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if (json.optInt("ErrCode") == 0) {
                            Toast.makeText(ChangePwdActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            String msg = json.optString("ErrDetails", "请求失败");
                            Toast.makeText(ChangePwdActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(ChangePwdActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.dismissProgressBar();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("---onCancelled-----" + "onCancelled");
                Toast.makeText(ChangePwdActivity.this, "您取消了操作", Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.dismissProgressBar();
            }

            @Override
            public void onFinished() {
                LogUtil.e("---onFinished-----" + "onFinished");
                ProgressDialogUtils.dismissProgressBar();
            }
        });
    }
}
