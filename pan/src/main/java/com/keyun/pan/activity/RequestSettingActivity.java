package com.keyun.pan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.keyun.pan.R;
import com.keyun.pan.utils.SharedPreferencesUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/8/23.
 */
@ContentView(value = R.layout.activity_request_setting)
public class RequestSettingActivity extends BaseActivity implements View.OnClickListener {

    public static final int REQ_SCAN_SET_CODE = 0;

    @ViewInject(R.id.title_back)
    private ImageButton barcodeScan;
    @ViewInject(R.id.title_right)
    private Button submitBtn;
    @ViewInject(R.id.username)
    private EditText yumingEdit;
    @ViewInject(R.id.password)
    private EditText ipEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        barcodeScan.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                Intent intent = new Intent(this, CaptureActivity.class);
                intent.putExtra("from", CaptureActivity.REQ_FROM_IP);
                startActivityForResult(intent, REQ_SCAN_SET_CODE);
                break;
            case R.id.title_right:
                String yumingStr = yumingEdit.getText().toString();
                String ipStr = ipEdit.getText().toString();
                if(TextUtils.isEmpty(yumingStr)) {
                    Toast.makeText(this, "域名配置不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!yumingStr.startsWith("http://")) {
                    Toast.makeText(this, "域名配置格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferencesUtils.saveByKey(this, "SERVER", "BASE_URL", yumingStr);
                if(!TextUtils.isEmpty(ipStr)) {
                    if(!ipStr.startsWith("http://")) {
                        Toast.makeText(this, "IP配置格式不正确", Toast.LENGTH_SHORT).show();
                    }else {
                        SharedPreferencesUtils.saveByKey(this, "SERVER", "BASE_URL_IP", ipStr);
                        Toast.makeText(this, "服务器配置配置成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == REQ_SCAN_SET_CODE) {
                String result = data.getStringExtra("result");// Scan result Not Match
                if(result == null || !result.startsWith("kx://")) {
                    Toast.makeText(this, "服务器配置扫描格式验证失败", Toast.LENGTH_SHORT).show();
                }else {
                    String ip = result.substring(4);
                    if(ip != null) {
                        if(ip.contains(";")) {
                            String[] iip = ip.split(";");
                            yumingEdit.setText(iip[0]);
                            ipEdit.setText(iip[1]);
                        }else {
                            ipEdit.setText(ip);
                        }
                    }
                }
            }
        }
    }
}
