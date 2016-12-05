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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.keyun.pan.R;
import com.keyun.pan.adapter.ShareRepositoryFileAdapter;
import com.keyun.pan.data.RepositoryFile;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONArray;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cunguoyao on 2016/8/21.
 */
@ContentView(value = R.layout.activity_share_to_repo)
public class ShareToRepoFileActivity extends BaseActivity implements View.OnClickListener {

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

    private String RepositoryID;
    private String currentDirId;
    private List<String> repositoryIdList;
    private Map<String, List<RepositoryFile>> mMap;
    private List<RepositoryFile> mData;
    private ShareRepositoryFileAdapter mAdapter;
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
    private String choosePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        RepositoryID = getIntent().getStringExtra("RepositoryID");
        repositoryIdList = new ArrayList<>();
        mMap = new HashMap<>();
        titleText.setText("请选择路径");
        backBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        submitBtn.setVisibility(View.VISIBLE);
        submitBtn.setText("提交");

        choosePath = "";
        mData = new ArrayList<>();
        mAdapter = new ShareRepositoryFileAdapter(this, mData);
        mListView.setAdapter(mAdapter);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                listUserRepository("");
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RepositoryFile rf = mAdapter.getItem(position-1);
                currentDirId = rf.getId();
                listUserRepository(rf.getId());
            }
        });
        listUserRepository("");
    }

    private void listUserRepository(final String dirId) {
        ProgressDialogUtils.showProgressDialog(this, true);
        String url = Urls.getDirsInRepositoryFiles() + "/UserToken/" + user.getToken()
                + "/RepositoryID/" + RepositoryID;
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("id", dirId);
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
                        JSONArray jsonArray = new JSONArray(result);
                        List<RepositoryFile> repositoryList = RepositoryFile.parseJson(jsonArray);
                        repositoryIdList.add(dirId);
                        if(repositoryList != null && repositoryList.size() > 0) {
                            if("".equals(dirId)) {
                                mMap.put("-1", repositoryList);
                            }else {
                                mMap.put(dirId, repositoryList);
                            }
                            mData.clear();
                            mData.addAll(repositoryList);
                            emptyLayout.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                            handler.sendEmptyMessage(1);
                        }else {
                            if("".equals(dirId)) {
                                mMap.put("-1", new ArrayList<RepositoryFile>());
                            }else {
                                mMap.put(dirId, new ArrayList<RepositoryFile>());
                            }
                            mData.clear();
                            mData.addAll(new ArrayList<RepositoryFile>());
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

    public void check(RepositoryFile repositoryFile) {
        choosePath = repositoryFile.getPath();
        titleText.setText(choosePath);
        List<RepositoryFile> repositoryList = new ArrayList<>();
        if(mData != null && mData.size() > 0) {
            for(RepositoryFile rf : mData) {
                if(rf.getId().equals(repositoryFile.getId())) {
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
                mData.addAll(new ArrayList<RepositoryFile>());
                emptyLayout.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                handler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.cancel_btn:
                finish();
                break;
            case R.id.fav_btn:
                Intent intent = new Intent(this, ShareToRepoActivity.class);
                intent.putExtra("path", choosePath);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(repositoryIdList == null || repositoryIdList.size() < 2) {
            finish();
        }else {
            String previewDirId = repositoryIdList.get(repositoryIdList.size() - 1);
            currentDirId = repositoryIdList.get(repositoryIdList.size() - 2);
            repositoryIdList.remove(repositoryIdList.size() - 1);
            if("".equals(currentDirId)) {
                currentDirId = "-1";
            }
            List<RepositoryFile> repositoryList = mMap.get(currentDirId);
            mMap.remove(previewDirId);
            if(repositoryList != null && repositoryList.size() > 0) {
                mData.clear();
                mData.addAll(repositoryList);
                emptyLayout.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                handler.sendEmptyMessage(1);
            }else {
                mData.clear();
                mData.addAll(new ArrayList<RepositoryFile>());
                emptyLayout.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                handler.sendEmptyMessage(1);
            }
        }
    }
}
