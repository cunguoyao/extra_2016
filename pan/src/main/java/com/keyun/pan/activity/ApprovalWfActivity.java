package com.keyun.pan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.keyun.pan.R;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/7/17.
 */
@ContentView(value = R.layout.activity_approval_request_wf)
public class ApprovalWfActivity extends BaseActivity implements View.OnClickListener {

    public static final int REQ_SCAN_WF_CODE = 1;

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.fav_btn)
    private Button favBtn;
    @ViewInject(R.id.description)
    private EditText description;
    @ViewInject(R.id.goCapture)
    private Button goCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleText.setText("外发申请");
        favBtn.setVisibility(View.INVISIBLE);
        titleBack.setOnClickListener(this);
        goCapture.setOnClickListener(this);
    }

    private void fetchData(String url) {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(url);
        params.setCharset("gbk");
        x.http().request(HttpMethod.GET, params, new MyHttpCallback(this) {
            @Override
            public void onSucceed(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                Toast.makeText(ApprovalWfActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
                ProgressDialogUtils.dismissProgressBar();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("---onError-----" + "onError");
                Toast.makeText(ApprovalWfActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                goCapture.setText("扫一扫");
                goCapture.setEnabled(true);
                ProgressDialogUtils.dismissProgressBar();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("---onCancelled-----" + "onCancelled");
                Toast.makeText(ApprovalWfActivity.this, "用户取消", Toast.LENGTH_SHORT).show();
                goCapture.setText("扫一扫");
                goCapture.setEnabled(true);
                ProgressDialogUtils.dismissProgressBar();
            }

            @Override
            public void onFinished() {
                LogUtil.e("---onFinished-----" + "onFinished");
                ProgressDialogUtils.dismissProgressBar();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.goCapture:
                Intent intent = new Intent(this, CaptureActivity.class);
                intent.putExtra("from", CaptureActivity.REQ_FROM_APPROVAL);
                startActivityForResult(intent, REQ_SCAN_WF_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == ApprovalWfActivity.REQ_SCAN_WF_CODE) {
                String result = data.getStringExtra("result");// Scan result Not Match
                goCapture.setText("正在申请...");
                goCapture.setEnabled(false);
                fetchData(result);
            }
        }
    }
}
