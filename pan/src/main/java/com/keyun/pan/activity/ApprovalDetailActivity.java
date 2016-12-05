package com.keyun.pan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keyun.pan.R;
import com.keyun.pan.app.Const;
import com.keyun.pan.app.Urls;
import com.keyun.pan.data.Approval;
import com.keyun.pan.data.ApprovalDetail;
import com.keyun.pan.data.ApprovalFile;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.utils.MyUtils;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/7/17.
 */
@ContentView(value = R.layout.activity_approval_detail)
public class ApprovalDetailActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;

    @ViewInject(R.id.approval_detail_title)
    private TextView approvalTitle;
    @ViewInject(R.id.approval_detail_sqlx)
    private TextView approvalType;

    @ViewInject(R.id.layout_spfs)
    private LinearLayout layoutSpfs;//审批方式
    @ViewInject(R.id.approval_detail_spfs)
    private TextView textSpfs;

    @ViewInject(R.id.layout_sqzm)
    private LinearLayout layoutSqzm;//申请者名
    @ViewInject(R.id.approval_detail_sqzm)
    private TextView textSqzm;

    @ViewInject(R.id.layout_sqzh)
    private LinearLayout layoutSpzh;//审批账号
    @ViewInject(R.id.approval_detail_sqzh)
    private TextView textSqzh;

    @ViewInject(R.id.layout_sqsj)
    private LinearLayout layoutSqsj;//申请时间
    @ViewInject(R.id.approval_detail_sqsj)
    private TextView textSpsj;

    @ViewInject(R.id.layout_sqsy)
    private LinearLayout layoutSqsy;//申请事由
    @ViewInject(R.id.approval_detail_sqsy)
    private TextView textSqsy;

    @ViewInject(R.id.layout_zzspz)
    private LinearLayout layoutZzspz;//最终审批者
    @ViewInject(R.id.approval_detail_zzspz)
    private TextView textZzspz;

    @ViewInject(R.id.layout_spwcskj)
    private LinearLayout layoutSpwcskj;//审批完成时间
    @ViewInject(R.id.approval_detail_spwcsj)
    private TextView textSpwcsj;

    @ViewInject(R.id.layout_spm)
    private LinearLayout layoutSpm;//审批码
    @ViewInject(R.id.approval_detail_spm)
    private TextView textSpm;

    @ViewInject(R.id.layout_spry)
    private LinearLayout layoutSpry;//审批人员
    @ViewInject(R.id.approval_detail_spm)
    private TextView textSpry;
    @ViewInject(R.id.approval_detail_sp_state)
    private TextView textSpState;

    @ViewInject(R.id.layout_spwj)
    private LinearLayout layoutSpwj;//审批文件
    @ViewInject(R.id.approval_detail_spwj)
    private TextView textSpwj;
    @ViewInject(R.id.approval_detail_spwj_size)
    private TextView textSpwjSize;

    @ViewInject(R.id.layout_sp_operate)
    private RelativeLayout layoutSpcz;//审批操作
    @ViewInject(R.id.approval_detail_btn_accept)
    private Button acceptBtn;
    @ViewInject(R.id.approval_detail_btn_reject)
    private Button rejectBtn;

    @ViewInject(R.id.layout_email)
    private LinearLayout layoutEmail;//邮箱
    @ViewInject(R.id.approval_email_sender)
    private TextView textEmailSender;
    @ViewInject(R.id.approval_email_receiver)
    private TextView textEmailReceiver;
    @ViewInject(R.id.approval_email_address)
    private TextView textEmailAddress;
    @ViewInject(R.id.approval_email_content)
    private TextView textEmailContent;

    private Approval approval;
    private int type;
    private ApprovalDetail detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        approval = (Approval) getIntent().getExtras().getSerializable("approval");
        type = getIntent().getIntExtra("type", Approval.TYPE_TODO_APPLY);
        if(approval == null) {
            finish();
            return;
        }
        titleText.setText(approval.getRequestTitle());
        titleBack.setOnClickListener(this);
        acceptBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);
        fetchData();
    }

    private void fetchData() {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.GetApprovalRequestInfo());
        params.addBodyParameter("UserToken", getDefaultUser().getToken());
        params.addBodyParameter("RequestID", approval.getRequestID());
        params.setCharset("gbk");
        x.http().request(HttpMethod.POST, params, new MyHttpCallback(this) {
            @Override
            public void onSucceed(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    LogUtil.e("---onSuccess-result----" + result);
                    JSONObject json = null;
                    try {
                        json = new JSONObject(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(json.optInt("ErrCode") == 0) {
                        detail = ApprovalDetail.parseJson(json);
                        fillData();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("---onError-----" + "onError");
                Toast.makeText(ApprovalDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
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

    private void acceptOrReject(int ApprovalStatus) {//1：表示同意，-1：表示拒绝
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.ApproverApprove());
        params.addBodyParameter("UserToken", getDefaultUser().getToken());
        params.addBodyParameter("RequestID", approval.getRequestID());
        params.addBodyParameter("ApprovalStatus", String.valueOf(ApprovalStatus));
        params.setCharset("gbk");
        x.http().request(HttpMethod.POST, params, new MyHttpCallback(this) {
            @Override
            public void onSucceed(String result) {
                if (result != null) {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(json.optInt("ErrCode") == 0) {
                        Toast.makeText(ApprovalDetailActivity.this, "审批成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("---onError-----" + "onError");
                Toast.makeText(ApprovalDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
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
        approvalTitle.setText(detail.getRequestTitle());
        approvalType.setText(detail.getRuleTypeID() == 1 ? "外发审批" : "外发审批");
        switch (type) {
            case  Approval.TYPE_TODO_APPLY:
                layoutSpcz.setVisibility(View.VISIBLE);
                layoutEmail.setVisibility(View.GONE);
                textSpfs.setText(detail.getRuleModeID() == 1 ? "任一审批" : "逐级审批");
                layoutSqzm.setVisibility(View.GONE);
                layoutSpzh.setVisibility(View.GONE);
                layoutSqsj.setVisibility(View.GONE);
                layoutSqsy.setVisibility(View.GONE);
                layoutZzspz.setVisibility(View.GONE);
                layoutSpwcskj.setVisibility(View.GONE);
                layoutSpm.setVisibility(View.GONE);

                layoutSpry.setVisibility(View.VISIBLE);
                textSpry.setText(detail.getUserName());
                if(detail.getApprovalStatus() == 0) {
                    textSpState.setText("审批中");
                }else if(detail.getApprovalStatus() == 1) {
                    textSpState.setText("批准");
                }else if(detail.getApprovalStatus() == -1) {
                    textSpState.setText("拒绝");
                }
                if(detail.getFileList() != null && detail.getFileList().size() > 0) {
                    layoutSpwj.setVisibility(View.VISIBLE);
                    ApprovalFile f = detail.getFileList().get(0);
                    textSpwj.setText(f.getFileName());
                    textSpwjSize.setText(MyUtils.convertFileSize(f.getFileSize()));
                }else {
                    layoutSpwj.setVisibility(View.GONE);
                }
                break;
            case Approval.TYPE_FINISH_APPLY:
                layoutSpcz.setVisibility(View.GONE);
                layoutEmail.setVisibility(View.GONE);
                textSpfs.setText(detail.getRuleModeID() == 1 ? "任一审批" : "逐级审批");textSpry.setText(detail.getUserName());
                textSqzm.setText(detail.getUserName());
                textSqzh.setText(detail.getUserID());
                textSpsj.setText(detail.getRequestTime());
                textSqsy.setText((detail.getRequestReason() == null || "".equals(detail.getRequestReason())) ? "(无)":detail.getRequestReason());
                textZzspz.setText(detail.getFinalApprover());
                textSpwcsj.setText(detail.getApprovalTime());
                textSpm.setText(detail.getMd5());

                layoutSpry.setVisibility(View.GONE);
                if(detail.getFileList() != null && detail.getFileList().size() > 0) {
                    layoutSpwj.setVisibility(View.VISIBLE);
                    ApprovalFile f = detail.getFileList().get(0);
                    textSpwj.setText(f.getFileName());
                    textSpwjSize.setText(MyUtils.convertFileSize(f.getFileSize()));
                }else {
                    layoutSpwj.setVisibility(View.GONE);
                }
                break;
            case Approval.TYPE_TODO_REQ:
                layoutSpcz.setVisibility(View.GONE);
                layoutEmail.setVisibility(View.GONE);
                textSpfs.setText(detail.getRuleModeID() == 1 ? "任一审批" : "逐级审批");textSpry.setText(detail.getUserName());
                textSqzm.setText(detail.getUserName());
                textSqzh.setText(detail.getUserID());
                textSpsj.setText(detail.getRequestTime());
                textSqsy.setText((detail.getRequestReason() == null || "".equals(detail.getRequestReason())) ? "(无)":detail.getRequestReason());
                layoutZzspz.setVisibility(View.GONE);
                layoutSpwcskj.setVisibility(View.GONE);
                layoutSpm.setVisibility(View.GONE);

                layoutSpry.setVisibility(View.VISIBLE);
                textSpry.setText(detail.getUserName());
                if(detail.getApprovalStatus() == 0) {
                    textSpState.setText("审批中");
                }else if(detail.getApprovalStatus() == 1) {
                    textSpState.setText("批准");
                }else if(detail.getApprovalStatus() == -1) {
                    textSpState.setText("拒绝");
                }
                if(detail.getFileList() != null && detail.getFileList().size() > 0) {
                    layoutSpwj.setVisibility(View.VISIBLE);
                    ApprovalFile f = detail.getFileList().get(0);
                    textSpwj.setText(f.getFileName());
                    textSpwjSize.setText(MyUtils.convertFileSize(f.getFileSize()));
                }else {
                    layoutSpwj.setVisibility(View.GONE);
                }
                break;
            case Approval.TYPE_FINISH_REQ:
                layoutSpcz.setVisibility(View.GONE);
                layoutEmail.setVisibility(View.GONE);
                textSpfs.setText(detail.getRuleModeID() == 1 ? "任一审批" : "逐级审批");textSpry.setText(detail.getUserName());
                textSqzm.setText(detail.getUserName());
                textSqzh.setText(detail.getUserID());
                textSpsj.setText(detail.getRequestTime());
                textSqsy.setText((detail.getRequestReason() == null || "".equals(detail.getRequestReason())) ? "(无)":detail.getRequestReason());
                textZzspz.setText(detail.getFinalApprover());
                textSpwcsj.setText(detail.getApprovalTime());
                textSpm.setText(detail.getMd5());

                layoutSpry.setVisibility(View.GONE);
                if(detail.getFileList() != null && detail.getFileList().size() > 0) {
                    layoutSpwj.setVisibility(View.VISIBLE);
                    ApprovalFile f = detail.getFileList().get(0);
                    textSpwj.setText(f.getFileName());
                    textSpwjSize.setText(MyUtils.convertFileSize(f.getFileSize()));
                }else {
                    layoutSpwj.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.approval_detail_btn_accept:
                acceptOrReject(1);
                break;
            case R.id.approval_detail_btn_reject:
                acceptOrReject(-1);
                break;
        }
    }

}
