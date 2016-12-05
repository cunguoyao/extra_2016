package com.keyun.pan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.keyun.pan.R;
import com.keyun.pan.data.FileItem;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.utils.MyUtils;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONObject;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/8/21.
 */
@ContentView(value = R.layout.activity_share_to_mail)
public class ShareToMailActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton backBtn;
    @ViewInject(R.id.fav_btn)
    private Button submitBtn;

    @ViewInject(R.id.text_mail_reason)
    private EditText reasonText;
    @ViewInject(R.id.text_mail_target)
    private EditText targetText;
    @ViewInject(R.id.text_mail_address)
    private EditText addressText;
    @ViewInject(R.id.text_mail_content)
    private EditText contentText;

    private FileItem fileItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        fileItem = (FileItem) getIntent().getSerializableExtra("FileItem");
        titleText.setText("邮件外发");
        backBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        submitBtn.setText("提交");
        submitBtn.setVisibility(View.VISIBLE);
    }

    private void submitShareToMail(String reason, String receiver, String address, String content) {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.RequestApproval());
        params.addBodyParameter("UserToken", user.getToken());
        params.addBodyParameter("RequestTitle", fileItem.getFullPath());
        params.addBodyParameter("RuleTypeID", "3");//审批类型ID（1：表示外发审批，2：打印审批，3：邮件外发）
        params.addBodyParameter("RequestReason", reason);
        params.addBodyParameter("IsReceive", "1");//审批完成后客户端是否接收通知（1：表示接收，0：表示不接收）
        params.addBodyParameter("isweb", "0");
        String requestFile = "[{\"FileMD5\":\"" + fileItem.getHash() + "\",\"FileName\":\"" + fileItem.getFileName()
                + "\",\"FilePath\":\"" + fileItem.getFullPath() + "\",\"FileSize\":\"" + fileItem.getSize()
                +"\",\"LastModifyTime\":\"" + fileItem.getModified() + "\",\"DecodeType\":\"" + "" + "\"}]";
        params.addBodyParameter("RequestFiles", requestFile);
        String emailInfo = "{\"address\":\"" + address +"\", \"content\":\"" + content + "\", \"receiver\":\"" + receiver +"\"}";
        params.addBodyParameter("emailInfo", emailInfo);
        params.setCharset("utf-8");
        for(KeyValue k : params.getQueryStringParams()) {
            LogUtil.e(k.key + ":" + k.value);
        }
        x.http().request(HttpMethod.POST, params, new MyHttpCallback(this) {
            @Override
            public void onSucceed(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if (json.optInt("ErrorCode") == 0) {
                            Toast.makeText(ShareToMailActivity.this, "邮件外发成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            String msg = json.optString("ErrorMsg", "请求失败");
                            Toast.makeText(ShareToMailActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("---onError-----" + "onError");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.fav_btn:
                String receiver = targetText.getText().toString();
                String address = addressText.getText().toString();
                String reason = reasonText.getText().toString();
                String content = contentText.getText().toString();
                if(receiver != null) {
                    receiver = receiver.trim();
                }else {
                    receiver = "";
                }
                if(address != null) {
                    address = address.trim();
                }else {
                    address = "";
                }
                if(reason != null) {
                    reason = reason.trim();
                }else {
                    reason = "";
                }
                if(content != null) {
                    content = content.trim();
                }else {
                    content = "";
                }
                if(!MyUtils.isEmail(address)) {
                    Toast.makeText(this, "请填写正确的邮箱地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                if("".equals(receiver) || "".equals(address) || "".equals(reason) || "".equals(content)) {
                    Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                }else {
                    submitShareToMail(reason, receiver, address, content);
                }
                break;
        }
    }
}
