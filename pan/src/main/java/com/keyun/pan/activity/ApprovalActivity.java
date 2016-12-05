package com.keyun.pan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.keyun.pan.R;
import com.keyun.pan.adapter.ApprovalListAdapter;
import com.keyun.pan.app.Const;
import com.keyun.pan.data.Approval;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/7/8.
 */
@ContentView(value = R.layout.activity_approval_list)
public class ApprovalActivity extends BaseActivity {

    private static final String TAG = ApprovalActivity.class.getName();

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.fav_btn)
    private Button favBtn;
    @ViewInject(R.id.empty_data)
    private RelativeLayout emptyData;
    @ViewInject(R.id.goRequest)
    private Button goRequest;
    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;
    private ApprovalListAdapter mAdapter;
    private List<Approval> mData;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(mData != null && mData.size() > 0) {
                        mListView.setVisibility(View.VISIBLE);
                        emptyData.setVisibility(View.GONE);
                        if(temp != null && temp.size() >= Const.PAGE_SIZE_10) {
                            mAdapter.notifyDataSetChanged();
                            mListView.onRefreshComplete();
                            mListView.setMode(PullToRefreshBase.Mode.BOTH);
                        }else {
                            mAdapter.notifyDataSetChanged();
                            mListView.onRefreshComplete();
                            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }
                    }else {
                        mListView.setVisibility(View.GONE);
                        emptyData.setVisibility(View.VISIBLE);
                    }
                    break;
                case 2:
                    ProgressDialogUtils.dismissProgressBar();
                    break;
            }
        }
    };

    private int type;
    List<Approval> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        if(getIntent() == null || getDefaultUser() == null) {
            finish();
            return;
        }
        type = getIntent().getIntExtra("type", Approval.TYPE_TODO_APPLY);
        switch (type) {
            case Approval.TYPE_TODO_APPLY:
                titleText.setText("待处理的审批");
                break;
            case Approval.TYPE_FINISH_APPLY:
                titleText.setText("已完成的审批");
                break;
            case Approval.TYPE_TODO_REQ:
                titleText.setText("待处理的请求");
                break;
            case Approval.TYPE_FINISH_REQ:
                titleText.setText("已完成的请求");
                break;
        }

        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        favBtn.setVisibility(View.INVISIBLE);
        goRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData(0);
            }
        });
        mData = new ArrayList<Approval>();
        mAdapter = new ApprovalListAdapter(this, mData);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Approval approval = mAdapter.getItem(position-1);
                Intent intent = new Intent(ApprovalActivity.this, ApprovalDetailActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("approval", approval);
                intent.putExtras(b);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchData(0);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if(mData.size() > 0) {
                    fetchData(mData.size());
                }
            }
        });
        fetchData(0);
    }

    private void fetchData(final int BeginIndex) {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.GetApprovalRequest());
        params.addBodyParameter("UserToken", getDefaultUser().getToken());
        params.addBodyParameter("SelectType", String.valueOf(type));
        params.addBodyParameter("Sort", "2");
        params.addBodyParameter("MaxItemCount", String.valueOf(Const.PAGE_SIZE_10));
        params.addBodyParameter("BeginIndex", String.valueOf(BeginIndex));
        /*params.addBodyParameter("NamespaceID", path);*/
        params.setCharset("gbk");
        x.http().request(HttpMethod.POST, params, new MyHttpCallback(this) {
            @Override
            public void onSucceed(String result) {
                if (result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.optInt("ErrCode") == 0) {
                            JSONArray jsonArray = json.optJSONArray("DataList");
                            temp = Approval.parseJson(jsonArray);
                            if(temp != null && temp.size() > 0) {
                                if(BeginIndex == 0) {
                                    mData.clear();
                                }
                                mData.addAll(temp);
                            }
                            handler.sendEmptyMessage(1);
                        }else {
                            String msg = json.optString("ErrMsg", "请求失败");
                            Toast.makeText(ApprovalActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("---onError-----" + "onError");
                Toast.makeText(ApprovalActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessageDelayed(2, 100);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("---onCancelled-----" + "onCancelled");
                handler.sendEmptyMessageDelayed(2, 100);
            }

            @Override
            public void onFinished() {
                LogUtil.e("---onFinished-----" + "onFinished");
                handler.sendEmptyMessageDelayed(2, 100);
                mListView.onRefreshComplete();
            }
        });
    }
}
