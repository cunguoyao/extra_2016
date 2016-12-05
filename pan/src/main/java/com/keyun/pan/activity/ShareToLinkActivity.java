package com.keyun.pan.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.keyun.pan.R;
import com.keyun.pan.data.FileItem;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.widget.CustomDialog;
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
@ContentView(value = R.layout.activity_share_to_link)
public class ShareToLinkActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton backBtn;
    @ViewInject(R.id.fav_btn)
    private Button submitBtn;

    @ViewInject(R.id.text_file_name)
    private EditText fileNameText;
    @ViewInject(R.id.text_file_desc)
    private EditText fileDescText;
    @ViewInject(R.id.text_file_times)
    private EditText timesText;
    @ViewInject(R.id.text_file_date)
    private EditText dateText;
    @ViewInject(R.id.text_file_password)
    private EditText passwordText;
    @ViewInject(R.id.checkbox_times)
    private CheckBox timesCheckBox;
    @ViewInject(R.id.checkbox_date)
    private CheckBox dateCheckBox;
    @ViewInject(R.id.checkbox_password)
    private CheckBox passwordCheckBox;

    private FileItem fileItem;
    ClipboardManager myClipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        if(null == getIntent()) {
            finish();
            return;
        }
        fileItem = (FileItem) getIntent().getSerializableExtra("FileItem");

        fileNameText.setText(fileItem.getFileName());
        fileNameText.setEnabled(false);
        fileDescText.requestFocus();
        titleText.setText("创建链接共享");
        backBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        submitBtn.setText("提交");
        submitBtn.setVisibility(View.VISIBLE);

        timesText.setEnabled(false);
        timesText.setBackgroundResource(R.drawable.button_gray);
        dateText.setEnabled(false);
        dateText.setBackgroundResource(R.drawable.button_gray);
        passwordText.setEnabled(false);
        passwordText.setBackgroundResource(R.drawable.button_gray);
        timesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    timesText.setEnabled(true);
                    timesText.setBackgroundResource(R.drawable.common_edit);
                }else {
                    timesText.setEnabled(false);
                    timesText.setText("");
                    timesText.setBackgroundResource(R.drawable.button_gray);
                }
            }
        });
        dateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    dateText.setEnabled(true);
                    dateText.setBackgroundResource(R.drawable.common_edit);
                }else {
                    dateText.setEnabled(false);
                    dateText.setText("");
                    dateText.setBackgroundResource(R.drawable.button_gray);
                }
            }
        });
        passwordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    passwordText.setEnabled(true);
                    passwordText.setBackgroundResource(R.drawable.common_edit);
                }else {
                    passwordText.setEnabled(false);
                    passwordText.setText("");
                    passwordText.setBackgroundResource(R.drawable.button_gray);
                }
            }
        });
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
    }

    private void submitShareToLink(String desc, int times, int date, String password) {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.CreateCloudFileShareLink());
        params.addBodyParameter("UserToken", user.getToken());
        params.addBodyParameter("FilePath", fileItem.getFullPath());
        params.addBodyParameter("ShareLinkDesc", desc);
        params.addBodyParameter("MaxUsedTimes", String.valueOf(times));
        params.addBodyParameter("MaxUsedDays", String.valueOf(date));
        params.addBodyParameter("SharePassword", password);
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
                            popResultLinkDialog(json.optString("FileShareLink"));
                        }else {
                            String msg = json.optString("ErrorMsg", "请求失败");
                            Toast.makeText(ShareToLinkActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    private void popResultLinkDialog(final String netShareLink) {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_link_result_copy, null);
        EditText editText = (EditText)view.findViewById(R.id.netShareLink);
        editText.setText(netShareLink);
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle("复制链接地址");
        builder.setContentView(view);
        builder.setPositiveButton("复制地址", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClipData myClip = ClipData.newPlainText("text", netShareLink);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(ShareToLinkActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        CustomDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.fav_btn:
                String desc = fileDescText.getText().toString();
                int times = -1;
                int date = -1;
                String password = "";
                if(timesCheckBox.isChecked()) {
                    String ttimes = timesText.getText().toString();
                    if(null == ttimes || "".equals(ttimes)) {
                        Toast.makeText(ShareToLinkActivity.this, "你输入限制使用次数", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        times = Integer.parseInt(ttimes);
                    }
                }
                if(dateCheckBox.isChecked()) {
                    String ddate = dateText.getText().toString();
                    if(null == ddate || "".equals(ddate)) {
                        Toast.makeText(ShareToLinkActivity.this, "你输入限制使用天数", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        date = Integer.parseInt(ddate);
                    }
                }
                if(passwordCheckBox.isChecked()) {
                    String pp = passwordText.getText().toString();
                    if(null == pp || "".equals(pp)) {
                        Toast.makeText(ShareToLinkActivity.this, "你输入共享密钥", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        password = pp;
                    }
                }
                submitShareToLink(desc, times, date, password);
                break;
        }
    }
}
