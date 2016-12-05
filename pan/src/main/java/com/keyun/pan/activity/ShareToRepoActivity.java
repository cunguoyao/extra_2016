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
import com.keyun.pan.adapter.ShareRepositoryAdapter;
import com.keyun.pan.data.FileItem;
import com.keyun.pan.data.Repository;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONArray;
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
@ContentView(value = R.layout.activity_share_to_repo)
public class ShareToRepoActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton backBtn;
    @ViewInject(R.id.fav_btn)
    private Button submitBtn;

    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;
    @ViewInject(R.id.empty_data)
    private RelativeLayout emptyLayout;

    private List<Repository> mData;
    private ShareRepositoryAdapter mAdapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    ProgressDialogUtils.dismissProgressBar();
                    break;
            }
        }
    };
    private String clickRepoId;
    private String path;
    private FileItem fileItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        fileItem = (FileItem) getIntent().getSerializableExtra("FileItem");
        titleText.setText("共享到资料库");
        backBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        submitBtn.setVisibility(View.VISIBLE);
        submitBtn.setText("提交");

        mData = new ArrayList<>();
        mAdapter = new ShareRepositoryAdapter(this, mData);
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                listUserRepository();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Repository repository = (Repository)mAdapter.getItem(position - 1);
                Intent intent = new Intent(ShareToRepoActivity.this, ShareToRepoFileActivity.class);
                intent.putExtra("RepositoryID", repository.getRepositoryID());
                clickRepoId = repository.getRepositoryID();
                startActivityForResult(intent, 1000);
            }
        });
        listUserRepository();
    }

    private void listUserRepository() {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.listUserRepository());
        params.addBodyParameter("UserToken", user.getToken());
        params.addBodyParameter("DeletedStatus", String.valueOf(0));
        params.addBodyParameter("status", "GroupList");
        params.addBodyParameter("DeleteStatus", "0");
        params.addBodyParameter("GroupType", "1");
        params.addBodyParameter("IsMustEditable", "1");
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
                            JSONArray jsonArray = json.optJSONArray("RepositoryInfo");
                            List<Repository> repositoryList = Repository.parseJson(jsonArray);
                            if(repositoryList != null && repositoryList.size() > 0) {
                                for(Repository r : repositoryList) {
                                    r.setCreatorName("\\");
                                }
                                mData.clear();
                                mData.addAll(repositoryList);
                                emptyLayout.setVisibility(View.GONE);
                                mListView.setVisibility(View.VISIBLE);
                                handler.sendEmptyMessage(1);
                            }else {
                                mData.clear();
                                mData.addAll(new ArrayList<Repository>());
                                emptyLayout.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                                handler.sendEmptyMessage(1);
                            }
                        }else {
                            String msg = json.optString("ErrorMsg", "请求失败");
                            mData.clear();
                            mData.addAll(new ArrayList<Repository>());
                            emptyLayout.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.GONE);
                            handler.sendEmptyMessage(1);
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("---onError-----" + "onError");
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

    private void submitShareToRepo(Repository repository) {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.ShareFileIntoRepository());
        params.addBodyParameter("UserToken", user.getToken());
        params.addBodyParameter("FullPathFrom", fileItem.getFullPath());
        params.addBodyParameter("FileType", fileItem.getIsDir() == 0 ? "1" : "0");
        params.addBodyParameter("RepositoryID", repository.getRepositoryID());
        params.addBodyParameter("FullPathTo", repository.getCreatorName() + "\\" + fileItem.getFileName());
        params.addBodyParameter("RemarkInfo", "");
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
                            Toast.makeText(ShareToRepoActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                        }else {
                            String msg = json.optString("ErrorMsg", "请求失败");
                            Toast.makeText(ShareToRepoActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    public void check(Repository repository) {
        List<Repository> repositoryList = new ArrayList<>();
        if(mData != null && mData.size() > 0) {
            for(Repository rf : mData) {
                if(rf.getRepositoryID().equals(repository.getRepositoryID())) {
                    rf.setChecked(true);
                }else {
                    rf.setChecked(false);
                }
                repositoryList.add(rf);
            }
            if(repositoryList != null && repositoryList.size() > 0) {
                mData.clear();
                mData.addAll(repositoryList);
                emptyLayout.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                handler.sendEmptyMessage(1);
            }else {
                mData.clear();
                mData.addAll(new ArrayList<Repository>());
                emptyLayout.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                handler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1000 && resultCode == RESULT_OK) {
            if(data != null) {
                path = data.getStringExtra("path");
                if(!"".equals(path)) {
                    List<Repository> temp = new ArrayList<>();
                    if (mData != null && mData.size() > 0) {
                        for (Repository r : mData) {
                            if (r.getRepositoryID().equals(clickRepoId)) {
                                r.setChecked(true);
                                r.setCreatorName(path);
                            }
                            temp.add(r);
                        }
                    }
                    mData.clear();
                    mData.addAll(temp);
                    handler.sendEmptyMessage(1);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.fav_btn:
                if(mData != null && mData.size() > 0) {
                    int checkedSize = 0;
                    for(Repository repository : mData) {
                        if(repository.isChecked()) {
                            checkedSize ++;
                            submitShareToRepo(repository);
                        }
                    }
                    if(checkedSize == 0) {
                        Toast.makeText(this, "请选择一个资料库分享", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
