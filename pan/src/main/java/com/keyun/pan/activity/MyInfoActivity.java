package com.keyun.pan.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.keyun.pan.R;
import com.keyun.pan.data.MyInfo;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.utils.MyUtils;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONObject;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/7/9.
 */
@ContentView(value = R.layout.activity_myinfo)
public class MyInfoActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.progress)
    private ProgressBar progressBar;
    @ViewInject(R.id.space)
    private TextView userSpaceText;
    @ViewInject(R.id.user_name)
    private TextView userNameText;
    @ViewInject(R.id.user_email)
    private TextView userEmailText;
    @ViewInject(R.id.user_phone)
    private TextView userPhoneText;
    @ViewInject(R.id.user_pwd)
    private TextView userPwdText;
    @ViewInject(R.id.change_pwd)
    private TextView changePwdText;
    @ViewInject(R.id.user_depart)
    private TextView userDepartText;
    @ViewInject(R.id.user_position)
    private TextView userPositionText;

    private MyInfo myInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        x.view().inject(this);
        titleBack.setOnClickListener(this);
        fetchData();
    }

    private void fetchData() {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.accountInfo());
        params.addBodyParameter("UserToken", getDefaultUser().getToken());
        params.setCharset("utf-8");
        x.http().request(HttpMethod.POST, params, new MyHttpCallback(this) {
            @Override
            public void onSucceed(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        result = new String(result.getBytes("GB2312"), "utf-8");
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if (json.optInt("ErrCode") == 0) {
                            myInfo = MyInfo.parseJson(json);
                            fillData();
                        }else {
                            String msg = json.optString("ErrDetails", "请求失败");
                            Toast.makeText(MyInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MyInfoActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.dismissProgressBar();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("---onCancelled-----" + "onCancelled");
                ProgressDialogUtils.dismissProgressBar();
            }

            @Override
            public void onFinished() {
                LogUtil.e("---onFinished-----" + "onFinished");
                ProgressDialogUtils.dismissProgressBar();
            }
        });
    }

    private void fillData() {
        if(myInfo != null) {
            progressBar.setMax(100);
            progressBar.setProgress((int)(100*myInfo.getUserd_space()/myInfo.getSpace()));
            userSpaceText.setText("容量：" + MyUtils.convertFileSize(myInfo.getUserd_space()) + "/" + MyUtils.convertFileSize(myInfo.getSpace()));
            userNameText.setText(myInfo.getUserName());
            userEmailText.setText(myInfo.getEmail());
            userPhoneText.setText(myInfo.getPhone());
            userPwdText.setText("******");
            changePwdText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
            changePwdText.getPaint().setAntiAlias(true);//抗锯齿
            changePwdText.setOnClickListener(this);
            userDepartText.setText(myInfo.getMachineDept());
            userPositionText.setText(myInfo.getPosition());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.change_pwd:
                Intent intent = new Intent(this, ChangePwdActivity.class);
                startActivity(intent);
                break;
        }
    }
}
