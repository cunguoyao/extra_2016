package com.keyun.pan.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.keyun.pan.R;
import com.keyun.pan.adapter.SearchFileListAdapter;
import com.keyun.pan.data.DownFileItem;
import com.keyun.pan.data.FileItem;
import com.keyun.pan.service.DownloadService;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.utils.MyUtils;
import com.keyun.pan.widget.CustomDialog;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/8/18.
 */
public class SearchResultFragment extends BaseFragment implements View.OnClickListener {

    private final String TAG = SearchResultFragment.class.getName();
    private View rootView;

    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;
    @ViewInject(R.id.empty_data)
    private RelativeLayout emptyLayout;
    private SearchFileListAdapter mAdapter;
    private List<FileItem> mData;
    private String searchContent;
    private String currentDirPath;
    private PopupWindow popupWindow;
    private FileItem selectFileItem;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mAdapter.notifyDataSetChanged();
                    if(popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    break;
                case 2:
                    ProgressDialogUtils.dismissProgressBar();
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchContent = getArguments().getString("searchContent");
        currentDirPath = "-1";
        mData = new ArrayList<>();
        mAdapter = new SearchFileListAdapter(this, mData);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_search_result, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        LogUtil.e(TAG + "------------onCreateView----");
        x.view().inject(this, rootView);   //注入控件

        mListView.setAdapter(mAdapter);
        search();
        return rootView;
    }

    private void search() {
        ProgressDialogUtils.showProgressDialog(getActivity(), true);
        /*ArrayList<String> history = sharedPreferencesUtils.getObject("history", ArrayList.class);
        if(history == null) {
            history = new ArrayList<>();
        }
        if(history.size() > 4) {
            history.remove(4);
        }
        history.add(searchContent);
        sharedPreferencesUtils.setObject("history", history);*/
        //saveSearchHistory(searchContent);
        RequestParams params = new RequestParams(Urls.searchFileDir());
        params.addBodyParameter("type", "searchFileDir");
        params.addBodyParameter("UserToken", user.getToken());
        String base64_path = new String(Base64.encode(currentDirPath.getBytes(), Base64.DEFAULT));
        LogUtil.i("---base64_path:" + base64_path);
        params.addBodyParameter("RelativePath_Base64", base64_path);
        params.addBodyParameter("isdelect", "0");
        params.addBodyParameter("BeginIdx", "15");
        params.addBodyParameter("MaxItemCount", "1");
        params.addBodyParameter("fileSequence", String.valueOf(2));
        params.addBodyParameter("fileTypeSequence", "-1");
        params.addBodyParameter("searchFileName", searchContent);
        /*params.addBodyParameter("NamespaceID", path);*/
        params.addBodyParameter("NSID", "39C9D3B7-E1A4-5206-E16D-1E76F8F87947");
        params.setCharset("utf-8");
        for(KeyValue k : params.getQueryStringParams()) {
            LogUtil.e(k.key + ":" + k.value);
        }
        x.http().request(HttpMethod.POST, params, new MyHttpCallback(getActivity()) {
            @Override
            public void onSucceed(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if (json.optInt("ErrCode") == 0) {
                            JSONArray jsonArray = json.optJSONArray("str");
                            List<FileItem> temp = FileItem.parseJson(jsonArray);
                            if(temp != null && temp.size() > 0) {
                                for(FileItem fi : temp) {
                                    fi.setCurrentDirPath("-1");
                                    fi.setT(0);
                                    fi.setUserName(user.getUserName());
                                }
                                mData.clear();
                                mData.addAll(temp);
                                mListView.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);
                                handler.sendEmptyMessage(1);
                            }else {
                                mData.clear();
                                mData.addAll(new ArrayList<FileItem>());
                                mListView.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                                handler.sendEmptyMessage(1);
                            }
                        }else {
                            String msg = json.optString("ErrMsg", "请求失败");
                            mData.clear();
                            mData.addAll(new ArrayList<FileItem>());
                            emptyLayout.setVisibility(View.VISIBLE);
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

    private void rename(FileItem selectFileItem, String newName) {
        ProgressDialogUtils.showProgressDialog(getActivity(), true);
        RequestParams params = new RequestParams(Urls.updateMoveFileDir());
        params.addBodyParameter("UserToken", user.getToken());
        String base64_path = new String(Base64.encode(currentDirPath.getBytes(), Base64.DEFAULT));
        LogUtil.i("---base64_path:" + base64_path);
        params.addBodyParameter("RelativePath_Base64", base64_path);
        params.addBodyParameter("filename", newName);
        params.addBodyParameter("fileNameBefore", selectFileItem.getFileName());
        params.setCharset("utf-8");
        for(KeyValue k : params.getQueryStringParams()) {
            LogUtil.e(k.key + ":" + k.value);
        }
        x.http().request(HttpMethod.POST, params, new MyHttpCallback(getActivity()) {
            @Override
            public void onSucceed(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if (json.optInt("ErrCode") == 0) {
                            Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                            search();
                        }else {
                            String msg = json.optString("ErrDetails", "请求失败");
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    private void deleteFile(FileItem fileItem) {
        ProgressDialogUtils.showProgressDialog(getActivity(), true);
        RequestParams params = new RequestParams(Urls.delete());
        params.addBodyParameter("UserToken", user.getToken());
        String base64_path = new String(Base64.encode(fileItem.getFullPath().getBytes(), Base64.DEFAULT));
        LogUtil.i("---base64_path:" + base64_path);
        params.addBodyParameter("RelativePath_Base64", base64_path);
        params.setCharset("utf-8");
        for(KeyValue k : params.getQueryStringParams()) {
            LogUtil.e(k.key + ":" + k.value);
        }
        x.http().request(HttpMethod.POST, params, new MyHttpCallback(getActivity()) {
            @Override
            public void onSucceed(String result) {
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if (json.optInt("ErrCode") == 0) {
                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                            search();
                        }else {
                            String msg = json.optString("ErrMsg", "请求失败");
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    public void listViewOnItemClick(FileItem fileItem) {
        if (fileItem != null) {
            if (fileItem.getIsDir() == 0) {
                currentDirPath = fileItem.getFullPath();
                search();
            }
        }
    }

    private void openFile(FileItem fileItem) {
        if(fileItem.getIsDir() == 0) {
            if(popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            listViewOnItemClick(fileItem);
        }else {
            DownFileItem downFileItem = null;
            try {
                downFileItem = db.selector(DownFileItem.class)
                        .where("hash", "=", fileItem.getHash())
                        .and("t", "=", "0").and("user_name", "=", user.getUserName()).findFirst();
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (downFileItem == null) {
                downFile(fileItem);
            } else {
                if (downFileItem.getDownloadState() != DownFileItem.STATE_DOWN_DONE) {
                    Toast.makeText(getActivity(), "文件正在下载，下载完成后再打开", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = MyUtils.openFile(downFileItem.getDownloadPath());
                    startActivity(i);
                }
                if(popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        }
    }

    private void downFile(FileItem fileItem) {
        Intent intent = new Intent(getActivity(), DownloadService.class);
        intent.putExtra("FileItem", fileItem);
        getActivity().startService(intent);
        Toast.makeText(getActivity(), "加入下载队列", Toast.LENGTH_SHORT).show();
        handler.sendEmptyMessageDelayed(1, 500);
    }

    public void pop(LayoutInflater inflater, ViewGroup parent, FileItem fileItem) {
        this.selectFileItem = fileItem;
        View view = inflater.inflate(R.layout.pop_menu_view, parent, false);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        /**
         * PopupWindow 设置
         */
        // popMenuWindow.setFocusable(true); //设置PopupWindow可获得焦点
        // popMenuWindow.setTouchable(true); //设置PopupWindow可触摸
        // popMenuWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
        // 设置PopupWindow显示和隐藏时的动画
        popupWindow.setAnimationStyle(R.style.MenuAnimationFade);
        /**
         * 改变背景可拉的弹出窗口。后台可以设置为null。 这句话必须有，否则按返回键popwindow不能消失 或者加入这句话
         * ColorDrawable dw = new
         * ColorDrawable(-00000);popMenuWindow.setBackgroundDrawable(dw);
         */
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mAdapter.checkItem(null);
                mAdapter.notifyDataSetChanged();
            }
        });
        Button popBtnOpen = (Button)view.findViewById(R.id.pop_btn_open);
        Button popBtnDown = (Button)view.findViewById(R.id.pop_btn_download);
        Button popBtnShare = (Button)view.findViewById(R.id.pop_btn_share);
        Button popBtnDelete = (Button)view.findViewById(R.id.pop_btn_delete);
        Button popBtnRename = (Button)view.findViewById(R.id.pop_btn_rename);
        popBtnOpen.setOnClickListener(this);
        popBtnDown.setOnClickListener(this);
        popBtnShare.setOnClickListener(this);
        popBtnDelete.setOnClickListener(this);
        popBtnRename.setOnClickListener(this);
    }

    private void popRenameDialog() {
        final EditText editText = new EditText(getActivity());
        editText.setBackgroundResource(R.drawable.edit_background_pressed);
        editText.setText(selectFileItem.getFileName());
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle("修改文件名称");
        builder.setContentView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectFileItem != null) {
                    if(TextUtils.isEmpty(editText.getText())) {
                        Toast.makeText(getActivity(), "请输入新文件名称", Toast.LENGTH_SHORT).show();
                    }
                    rename(selectFileItem, editText.getText().toString());
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        CustomDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void popCofirmDeleteFileDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("确认要删除该文件吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectFileItem != null)
                    deleteFile(selectFileItem);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        CustomDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_btn_open:
                openFile(selectFileItem);
                break;
            case R.id.pop_btn_download:
                downFile(selectFileItem);
                break;
            case R.id.pop_btn_share:
                Toast.makeText(getActivity(), "分享", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pop_btn_delete:
                popCofirmDeleteFileDialog();
                break;
            case R.id.pop_btn_rename:
                popRenameDialog();
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
