package com.zokbet.betdd.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zokbet.betdd.R;
import com.zokbet.betdd.app.Urls;
import com.zokbet.betdd.data.Merchant;
import com.zokbet.betdd.widget.CircularImage;
import com.zokbet.betdd.widget.CustomDialog;
import com.zokbet.betdd.widget.ProgressDialogUtils;

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

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_pay_edit)
public class PayActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.fav_btn)
    private Button favBtn;
    @ViewInject(R.id.merch_avatar)
    private CircularImage merchAvatar;
    @ViewInject(R.id.merch_name)
    private TextView merchText;
    @ViewInject(R.id.edit_money)
    private EditText editMoney;
    @ViewInject(R.id.edit_password)
    private EditText editPassword;
    @ViewInject(R.id.edit_note)
    private EditText editNote;

    public static final int REQ_PAY_CODE = 7788;
    private String id;
    private String name;
    private Merchant merchant;

    private String money;
    private String password;
    private String remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        Intent intent = getIntent();
        if(intent == null) {
            finish();
            return;
        }
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        titleText.setText("支付");
        favBtn.setVisibility(View.VISIBLE);
        titleBack.setOnClickListener(this);
        favBtn.setOnClickListener(this);

        merchText.setText(name);
    }

    private void payByScan() {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.payByScan);
        params.addBodyParameter("sid", id);
        params.addBodyParameter("money", money);
        params.addBodyParameter("remark", remark);
        params.addBodyParameter("password", MD5.md5(password));
        params.addBodyParameter("token", getDefaultUser().getToken());
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                LogUtil.e("---onSuccess-result----" + result);
                ProgressDialogUtils.dismissProgressBar();
                if(result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if(json.optInt("code") == 100) {
                            JSONObject jsonObject = json.optJSONObject("data");
                            CustomDialog.Builder builder = new CustomDialog.Builder(PayActivity.this);
                            builder.setTitle(json.optString("msg"));
                            builder.setMessage("支付金额:" + jsonObject.optString("payment") + "  交易价格:" + jsonObject.optString("bargain"));
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                            builder.setNegativeButton(null, null);
                            builder.create().show();
                        }else {
                            CustomDialog.Builder builder = new CustomDialog.Builder(PayActivity.this);
                            builder.setTitle("支付失败");
                            builder.setMessage(json.optString("msg"));
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
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
                Toast.makeText(PayActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.fav_btn:
                money = editMoney.getText().toString();
                password = editPassword.getText().toString();
                remark = editNote.getText().toString();
                if(TextUtils.isEmpty(password)) {
                    password = "";
                }
                String reg = "\\d+(\\.\\d+)?";
                if(!TextUtils.isEmpty(money) && money.matches(reg)) {
                    payByScan();
                }else {
                    Toast.makeText(PayActivity.this, "请正确输入消费金额", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
