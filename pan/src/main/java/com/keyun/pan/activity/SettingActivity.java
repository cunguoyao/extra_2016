package com.keyun.pan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keyun.pan.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/8/28.
 */
@ContentView(value = R.layout.activity_setting)
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton backBtn;

    @ViewInject(R.id.setting_change_pwd)
    private RelativeLayout changePwd;
    @ViewInject(R.id.setting_change_ip)
    private RelativeLayout ipSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleText.setText("设置");
        backBtn.setOnClickListener(this);
        changePwd.setOnClickListener(this);
        ipSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.setting_change_pwd:
                Intent intent1 = new Intent(this, ChangePwdActivity.class);
                startActivity(intent1);
                break;
            case R.id.setting_change_ip:
                Intent intent2 = new Intent(this, RequestSettingActivity.class);
                startActivity(intent2);
                break;
        }
    }

}
