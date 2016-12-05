package com.keyun.pan.activity;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.keyun.pan.R;
import com.keyun.pan.adapter.ShareToUserAdapter;
import com.keyun.pan.adapter.ShareToUserSelectAdapter;
import com.keyun.pan.data.FileItem;
import com.keyun.pan.data.ShareToUser;
import com.keyun.pan.utils.DensityUtil;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONObject;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/8/21.
 */
@ContentView(value = R.layout.activity_share_to_user)
public class ShareToUserActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton backBtn;
    @ViewInject(R.id.fav_btn)
    private Button submitBtn;
    @ViewInject(R.id.root)
    private LinearLayout root;

    @ViewInject(R.id.text_file_name)
    private EditText fileNameText;
    @ViewInject(R.id.text_file_desc)
    private EditText fileDescText;
    @ViewInject(R.id.text_file_date)
    private EditText dateText;
    @ViewInject(R.id.checkbox_date)
    private CheckBox dateCheckBox;

    @ViewInject(R.id.mListView)
    private ListView mListView;

    private LinearLayout footerLayout;
    private List<ShareToUser> mData;
    private ShareToUserAdapter mAdapter;

    private PopupWindow popupWindow;
    private PullToRefreshListView mmListView;
    private List<ShareToUser> mmData;
    private ShareToUserSelectAdapter mmAdapter;
    private List<ShareToUser> mmTemp;

    private FileItem fileItem;

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
        titleText.setText("共享文件到用户");
        backBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        submitBtn.setText("提交");
        submitBtn.setVisibility(View.VISIBLE);

        dateText.setEnabled(false);
        dateText.setBackgroundResource(R.drawable.button_gray);
        dateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    dateText.setEnabled(true);
                    dateText.setBackgroundResource(R.drawable.common_edit);
                }else {
                    dateText.setEnabled(false);
                    dateText.setBackgroundResource(R.drawable.button_gray);
                }
            }
        });
        initFooter();
        mData = new ArrayList<>();
        mAdapter = new ShareToUserAdapter(this, mData, this);
        mListView.setAdapter(mAdapter);
    }

    private void initFooter() {
        LinearLayout.LayoutParams FFlayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        footerLayout = new LinearLayout(this);
        // 设置布局 水平方向
        footerLayout.setOrientation(LinearLayout.HORIZONTAL);
        // 文本内容
        TextView textView = new TextView(this);
        textView.setText("添加用户");
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 30, 0, 30);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);
        // 把文本加入到layout中
        footerLayout.addView(textView, FFlayoutParams);
        // 设置layout的重力方向，即对齐方式是
        footerLayout.setGravity(Gravity.CENTER);
        mListView.addFooterView(footerLayout);
        footerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupSelectUserWindow();
            }
        });
    }

    private void popupSelectUserWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.pop_share_to_users, null, false);
        if(popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        popupWindow = new PopupWindow(view, DensityUtil.dip2px(this, 300),
                DensityUtil.dip2px(this, 400), true);
        popupWindow.setAnimationStyle(R.style.MenuAnimationFade);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        backgroundAlpha(0.5f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mAdapter.notifyDataSetChanged();
                backgroundAlpha(1f);
            }
        });
        Button popSubmit = (Button)view.findViewById(R.id.pop_submit_btn);
        Button popCancel = (Button)view.findViewById(R.id.pop_cancel_btn);
        popSubmit.setOnClickListener(this);
        popCancel.setOnClickListener(this);
        mmData = new ArrayList<>();
        mmListView = (PullToRefreshListView)view.findViewById(R.id.listView);
        mmAdapter = new ShareToUserSelectAdapter(this, mmData, this);
        mmListView.setAdapter(mmAdapter);
        listUser();
        mmTemp = new ArrayList<>();
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    public void mChecked(ShareToUser checkUser) {
        if(checkUser.isChecked()) {
            mmTemp.add(checkUser);
        }else {
            if(mmTemp.contains(checkUser)) {
                mmTemp.remove(checkUser);
            }
        }
    }

    private void listUser() {
        ProgressDialogUtils.showProgressDialog(this, true);
        String url = Urls.GetCloudUserInfo();
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("UserToken", user.getToken());
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
                            List<ShareToUser> tempUsers = ShareToUser.parseJson(json.getJSONArray("CloudUser"));
                            if (tempUsers != null && tempUsers.size() > 0) {
                                mmData.clear();
                                mmData.addAll(tempUsers);
                            } else {
                                mmData.clear();
                                mmData.addAll(new ArrayList<ShareToUser>());
                            }
                            mmAdapter.notifyDataSetChanged();
                        }else {
                            String msg = json.optString("ErrorMsg", "请求失败");
                            Toast.makeText(ShareToUserActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                mmListView.onRefreshComplete();
            }
        });
    }

    private void submitShareToUser(String desc, String targetUser, int MaxUsedDays) {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.CreateP2PShareFileLink());
        params.addBodyParameter("UserToken", user.getToken());
        params.addBodyParameter("CloudFilePath", fileItem.getFullPath());
        params.addBodyParameter("ShareLinkDesc", desc);
        params.addBodyParameter("MaxUsedDays", String.valueOf(MaxUsedDays));
        params.addBodyParameter("ShareTargetUser", targetUser);
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
                            Toast.makeText(ShareToUserActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            String msg = json.optString("ErrorMsg", "请求失败");
                            Toast.makeText(ShareToUserActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                if(mData != null && mData.size() > 0) {
                    int date = -1;
                    if(dateCheckBox.isChecked()) {
                        String ddate = dateText.getText().toString();
                        if(null == ddate || "".equals(ddate)) {
                            Toast.makeText(ShareToUserActivity.this, "你输入限制使用天数", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            date = Integer.parseInt(ddate);
                        }
                    }
                    String desc = fileDescText.getText().toString();
                    if(desc == null) {
                        desc = "";
                    }
                    String targetUser = "";
                    for(int i=0;i<mData.size();i++) {
                        ShareToUser u = mData.get(i);
                        targetUser = targetUser + u.getUserid();
                        if(i != mData.size()-1) {
                            targetUser = targetUser + ",";
                        }
                    }
                    submitShareToUser(desc, targetUser, date);
                }else {
                    Toast.makeText(this, "请添加用户", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.user_delete:
                ShareToUser shareToUser = (ShareToUser)v.getTag();
                if(mData != null && mData.contains(shareToUser)) {
                    mData.remove(shareToUser);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.pop_submit_btn:
                if(mmTemp != null && mmTemp.size() > 0) {
                    mData.clear();
                    mData.addAll(mmTemp);
                    mAdapter.notifyDataSetChanged();
                }
            case R.id.pop_cancel_btn:
                if(popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                break;
        }
    }
}
