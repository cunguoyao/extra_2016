package com.keyun.pan.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.keyun.pan.R;
import com.keyun.pan.activity.FileTransferActivity;
import com.keyun.pan.activity.LocalFileActivity;
import com.keyun.pan.activity.LocalFileFilterActivity;
import com.keyun.pan.activity.MyInfoActivity;
import com.keyun.pan.activity.SearchFileActivity;
import com.keyun.pan.activity.ShareToLinkActivity;
import com.keyun.pan.activity.ShareToMailActivity;
import com.keyun.pan.activity.ShareToRepoActivity;
import com.keyun.pan.activity.ShareToUserActivity;
import com.keyun.pan.adapter.FileListAdapter;
import com.keyun.pan.data.DownFileItem;
import com.keyun.pan.data.FileItem;
import com.keyun.pan.service.DownloadService;
import com.keyun.pan.service.UploadService;
import com.keyun.pan.utils.DensityUtil;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.utils.MyUtils;
import com.keyun.pan.widget.CircularImage;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/6/2.
 */
public class MyPanFragment extends BaseFragment implements View.OnClickListener {

    private final String TAG = MyPanFragment.class.getName();
    private View rootView;

    @ViewInject(R.id.tv_category_icon)
    private TextView categoryText;
    @ViewInject(R.id.layout_category)
    private RelativeLayout layoutCategory;
    @ViewInject(R.id.layout_upload)
    private RelativeLayout layoutUpload;
    @ViewInject(R.id.layout_transferlist)
    private RelativeLayout layoutTransferlist;
    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;
    @ViewInject(R.id.empty_data)
    private RelativeLayout emptyLayout;

    @ViewInject(R.id.layout_background_info)
    private RelativeLayout accountInfo;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.title_dir)
    private TextView titleDir;
    @ViewInject(R.id.layout_background_guide)
    private RelativeLayout dirInfo;
    @ViewInject(R.id.user_avatar)
    private CircularImage userAvatar;
    @ViewInject(R.id.user_name)
    private TextView userName;

    private LayoutInflater inflater;
    private PopupWindow popupWindow;

    private View headerView;
    private ImageButton headerRankBtn;
    private ImageButton headerSearchBtn;
    private ImageButton headerNewFolderBtn;
    private FileListAdapter mAdapter;
    private List<FileItem> mData;
    private String previewDirPath;
    private String currentDirPath;
    private List<String> dirPathList;

    private int fileSequence;
    private int fileTypeSequence;
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
                    showTitle();
                    break;
                case 2:
                    ProgressDialogUtils.dismissProgressBar();
                    break;
            }
        }
    };

    private LinearLayout popBtnShareLink;
    private LinearLayout popBtnShareRepo;
    private LinearLayout popBtnShareUser;
    private LinearLayout popBtnShareMail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(getActivity());
        previewDirPath = "-1";
        currentDirPath = "-1";
        dirPathList = new ArrayList<>();
        fileSequence = 2;//排序的值，-1名称升序,0名称降序,1时间升序,2时间倒序,3文件大到小,4文件小到大,默认为2
        fileTypeSequence = 0;//指定类型的文档，0所有，5文档，6图片，7音乐，8视频，9压缩包
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mypan, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        LogUtil.e(TAG + "------------onCreateView----");
        x.view().inject(this, rootView);   //注入控件

        init();
        userAvatar.setOnClickListener(this);
        userName.setOnClickListener(this);
        layoutCategory.setOnClickListener(this);
        layoutUpload.setOnClickListener(this);
        layoutTransferlist.setOnClickListener(this);
        titleBack.setOnClickListener(this);
        if (getDefaultUser() != null) {
            userName.setText(user.getAccount());
        }
        fetchDataLocal(true);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchData();
            }
        });
        return rootView;
    }

    private void init() {
        headerView = inflater.inflate(R.layout.header_file_list, null);
        mListView.getRefreshableView().addHeaderView(headerView);
        mData = new ArrayList<FileItem>();
        mAdapter = new FileListAdapter(this, mData);

        mListView.setAdapter(mAdapter);

        headerRankBtn = (ImageButton) headerView.findViewById(R.id.imagebtn_rank);
        headerSearchBtn = (ImageButton) headerView.findViewById(R.id.imagebtn_search);
        headerNewFolderBtn = (ImageButton) headerView.findViewById(R.id.imagebtn_new_folder);
        headerRankBtn.setOnClickListener(this);
        headerSearchBtn.setOnClickListener(this);
        headerNewFolderBtn.setOnClickListener(this);
    }

    public void listViewOnItemClick(FileItem fileItem) {
        if (fileItem != null) {
            if (fileItem.getIsDir() == 0) {
                currentDirPath = fileItem.getFullPath();
                fetchDataLocal(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_avatar:
            case R.id.user_name:
                Intent intent1 = new Intent(getActivity(), MyInfoActivity.class);
                startActivity(intent1);
                break;
            case R.id.title_back:
                if (!currentDirPath.equals("-1") && dirPathList.size() > 1) {
                    dirPathList.remove(dirPathList.size() - 1);
                    currentDirPath = dirPathList.get(dirPathList.size() - 1);
                    fetchDataLocal(false);
                }
                break;
            case R.id.layout_category:
                popCategoryWindow();
                break;
            case R.id.layout_upload:
                popUploadCategoryWindow();
                break;
            case R.id.layout_transferlist:
                Intent intent3 = new Intent(getActivity(), FileTransferActivity.class);
                startActivity(intent3);
                break;
            case R.id.imagebtn_rank:
                popRankWindow();
                break;
            case R.id.imagebtn_new_folder:
                popCreateDialog();
                break;
            case R.id.imagebtn_search:
                Intent intent4 = new Intent(getActivity(), SearchFileActivity.class);
                intent4.putExtra("t", 0);//0为自己的网盘 1为资料库
                startActivity(intent4);
                break;
            case R.id.pop_btn_open:
                openFile(selectFileItem);
                break;
            case R.id.pop_btn_download:
                downFile(selectFileItem);
                break;
            case R.id.pop_btn_share:
                popShareWindow();
                break;
            case R.id.pop_btn_delete:
                popCofirmDeleteFileDialog();
                break;
            case R.id.pop_btn_rename:
                popRenameDialog();
                break;
            case R.id.btn_share_link:
                Intent intent5 = new Intent(getActivity(), ShareToLinkActivity.class);
                intent5.putExtra("FileItem", selectFileItem);
                startActivity(intent5);
                break;
            case R.id.btn_share_repo:
                Intent intent6 = new Intent(getActivity(), ShareToRepoActivity.class);
                intent6.putExtra("FileItem", selectFileItem);
                startActivity(intent6);
                break;
            case R.id.btn_share_user:
                Intent intent7 = new Intent(getActivity(), ShareToUserActivity.class);
                intent7.putExtra("FileItem", selectFileItem);
                startActivity(intent7);
                break;
            case R.id.btn_share_mail:
                Intent intent8 = new Intent(getActivity(), ShareToMailActivity.class);
                intent8.putExtra("FileItem", selectFileItem);
                startActivity(intent8);
                break;
            case R.id.iv_btn_share_link:
                popBtnShareLink.performClick();
                break;
            case R.id.iv_btn_share_repo:
                popBtnShareRepo.performClick();
                break;
            case R.id.iv_btn_share_user:
                popBtnShareUser.performClick();
                break;
            case R.id.iv_btn_share_mail:
                popBtnShareMail.performClick();
                break;
        }
    }

    private void fetchDataLocal(final boolean fetchNet) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    previewDirPath = currentDirPath;
                    if(fetchNet) {
                        dirPathList.add(currentDirPath);
                    }
                    List<FileItem> temp = db.selector(FileItem.class)
                            .where("current_dir", "=", currentDirPath)
                            .and("t", "=", "0").and("user_name", "=", user.getUserName()).findAll();
                    if(temp != null && temp.size() > 0) {
                        mData.clear();
                        mData.addAll(temp);
                        emptyLayout.setVisibility(View.GONE);
                        handler.sendEmptyMessage(1);
                    }else {
                        mData.clear();
                        mData.addAll(new ArrayList<FileItem>());
                        emptyLayout.setVisibility(View.VISIBLE);
                        handler.sendEmptyMessage(1);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if(fetchNet) {
                    fetchData();
                }
            }
        });
    }

    private void fetchData() {
        ProgressDialogUtils.showProgressDialog(getActivity(), true);
        RequestParams params = new RequestParams(Urls.listDir());
        params.addBodyParameter("UserToken", user.getToken());
        String base64_path = new String(Base64.encode(currentDirPath.getBytes(), Base64.DEFAULT));
        LogUtil.i("---base64_path:" + base64_path);
        params.addBodyParameter("RelativePath_Base64", base64_path);
        params.addBodyParameter("isdelect", "0");
        params.addBodyParameter("BeginIdx", "1");
        params.addBodyParameter("MaxItemCount", "-1");
        params.addBodyParameter("fileSequence", String.valueOf(fileSequence));
        params.addBodyParameter("fileTypeSequence", String.valueOf(fileTypeSequence));
        /*params.addBodyParameter("NamespaceID", path);*/
        params.addBodyParameter("NSID", "");
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
                            List<FileItem> dbL = db.selector(FileItem.class).where("user_name", "=", user.getUserName()).and("current_dir", "=", currentDirPath).and("t", "=", "0").findAll();
                            if(dbL != null && dbL.size() > 0) {
                                db.executeUpdateDelete("delete from file_item where (user_name = '" + user.getUserName() + "' and current_dir = '" + currentDirPath + "' and t = 0);");
                            }
                            if(temp != null && temp.size() > 0) {
                                for(FileItem fi : temp) {
                                    fi.setCurrentDirPath(currentDirPath);
                                    fi.setT(0);
                                    fi.setUserName(user.getUserName());
                                    db.saveOrUpdate(fi);
                                }
                                mData.clear();
                                mData.addAll(temp);
                                emptyLayout.setVisibility(View.GONE);
                                handler.sendEmptyMessage(1);
                            }else {
                                mData.clear();
                                mData.addAll(new ArrayList<FileItem>());
                                emptyLayout.setVisibility(View.VISIBLE);
                                handler.sendEmptyMessage(1);
                            }
                        }else {
                            currentDirPath = previewDirPath;
                            String msg = json.optString("ErrMsg", "请求失败");
                            mData.clear();
                            mData.addAll(new ArrayList<FileItem>());
                            emptyLayout.setVisibility(View.VISIBLE);
                            handler.sendEmptyMessage(1);
                        }
                    } catch (Exception e) {
                        onError(e, true);
                        currentDirPath = previewDirPath;
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("---onError-----" + "onError");
                currentDirPath = previewDirPath;
                handler.sendEmptyMessageDelayed(2, 100);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("---onCancelled-----" + "onCancelled");
                currentDirPath = previewDirPath;
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

    private void createDir(String dirName) {
        ProgressDialogUtils.showProgressDialog(getActivity(), true);
        RequestParams params = new RequestParams(Urls.createDir());
        params.addBodyParameter("UserToken", user.getToken());
        String base64_path = new String(Base64.encode(currentDirPath.getBytes(), Base64.DEFAULT));
        LogUtil.i("---base64_path:" + base64_path);
        params.addBodyParameter("RelativePath_Base64", base64_path);
        params.addBodyParameter("filename", dirName);
        params.addBodyParameter("NSID", "");
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
                            Toast.makeText(getActivity(), "创建成功", Toast.LENGTH_SHORT).show();
                            fetchData();
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
                            fetchData();
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
                            fetchData();
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

    private void popCategoryWindow() {
        View view = inflater.inflate(R.layout.popup_file_category, null);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(layoutCategory, 0, 0);
        backgroundAlpha(0.5f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        ImageView cateDoc = (ImageView)view.findViewById(R.id.iv_category_document);
        ImageView cateImage = (ImageView)view.findViewById(R.id.iv_category_image);
        ImageView cateAudio = (ImageView)view.findViewById(R.id.iv_category_audio);
        ImageView cateVideo = (ImageView)view.findViewById(R.id.iv_category_video);
        ImageView cateZip = (ImageView)view.findViewById(R.id.iv_category_zip);
        ImageView cateAll = (ImageView)view.findViewById(R.id.iv_category_all);
        cateDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeSequence = 5;
                categoryText.setText("文档");
                popupWindow.dismiss();
                fetchData();
            }
        });
        cateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeSequence = 6;
                categoryText.setText("图片");
                popupWindow.dismiss();
                fetchData();
            }
        });
        cateAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeSequence = 7;
                categoryText.setText("音乐");
                popupWindow.dismiss();
                fetchData();
            }
        });
        cateVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeSequence = 8;
                categoryText.setText("视频");
                popupWindow.dismiss();
                fetchData();
            }
        });
        cateZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeSequence = 9;
                categoryText.setText("压缩包");
                popupWindow.dismiss();
                fetchData();
            }
        });
        cateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileTypeSequence = 0;
                categoryText.setText("分类");
                popupWindow.dismiss();
                fetchData();
            }
        });
    }

    private void popUploadCategoryWindow() {
        View view = inflater.inflate(R.layout.popup_upload_file_category, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.show();
        mAlertDialog.getWindow().setContentView(view);
        mAlertDialog.getWindow().setLayout((int)(0.83*DensityUtil.getScreenWidth(getActivity())), (int)(0.4*DensityUtil.getScreenHeight(getActivity())));
        ImageView cateDoc = (ImageView)view.findViewById(R.id.iv_category_document);
        ImageView cateImage = (ImageView)view.findViewById(R.id.iv_category_image);
        ImageView cateAudio = (ImageView)view.findViewById(R.id.iv_category_audio);
        ImageView cateVideo = (ImageView)view.findViewById(R.id.iv_category_video);
        ImageView cateAll = (ImageView)view.findViewById(R.id.iv_category_all);
        cateDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
                Intent intent = new Intent(getActivity(), LocalFileFilterActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("t", 0);
                intent.putExtra("RepositoryID", "");
                intent.putExtra("cloudDirPath", currentDirPath);
                startActivityForResult(intent, 2);
            }
        });
        cateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        cateAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
                Intent intent = new Intent(getActivity(), LocalFileFilterActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra("t", 0);
                intent.putExtra("RepositoryID", "");
                intent.putExtra("cloudDirPath", currentDirPath);
                startActivityForResult(intent, 2);
            }
        });
        cateVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
                Intent intent = new Intent(getActivity(), LocalFileFilterActivity.class);
                intent.putExtra("type", 3);
                intent.putExtra("t", 0);
                intent.putExtra("RepositoryID", "");
                intent.putExtra("cloudDirPath", currentDirPath);
                startActivityForResult(intent, 2);
            }
        });
        cateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
                Intent intent = new Intent(getActivity(), LocalFileActivity.class);
                intent.putExtra("t", 0);
                intent.putExtra("RepositoryID", "");
                intent.putExtra("cloudDirPath", currentDirPath);
                startActivityForResult(intent, 2);
            }
        });
    }

    private void popRankWindow() {
        View view = inflater.inflate(R.layout.pop_rank_view, null);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(headerView, 0, 0);
        RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.rgp_rank);
        RadioButton rankName = (RadioButton)view.findViewById(R.id.rdo_rank_name);
        RadioButton rankTime = (RadioButton)view.findViewById(R.id.rdo_rank_time);
        if(fileSequence == 2) {
            rankTime.setChecked(true);
        }else {
            rankName.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdo_rank_name:
                        if(popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        fileSequence = -1;
                        fetchData();
                        break;
                    case R.id.rdo_rank_time:
                        if(popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        fileSequence = 2;
                        fetchData();
                        break;
                }
            }
        });
    }

    public void popMenuWindow(LayoutInflater inflater, ViewGroup parent, FileItem fileItem) {
        this.selectFileItem = fileItem;
        View view = inflater.inflate(R.layout.pop_menu_view, parent, false);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.MenuAnimationFade);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
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
        if(selectFileItem.getIsDir() != 1) {
            popBtnDown.setEnabled(false);
            popBtnShare.setEnabled(false);
        }else {
            popBtnDown.setEnabled(true);
            popBtnShare.setEnabled(true);
        }
        popBtnOpen.setOnClickListener(this);
        popBtnDown.setOnClickListener(this);
        popBtnShare.setOnClickListener(this);
        popBtnDelete.setOnClickListener(this);
        popBtnRename.setOnClickListener(this);
    }

    public void popShareWindow() {
        View view = inflater.inflate(R.layout.pop_share_dialog, null, false);
        if(popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        popupWindow = new PopupWindow(view, DensityUtil.dip2px(getActivity(), 300),
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.MenuAnimationFade);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        backgroundAlpha(0.5f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mAdapter.checkItem(null);
                mAdapter.notifyDataSetChanged();
                backgroundAlpha(1f);
            }
        });
        popBtnShareLink = (LinearLayout)view.findViewById(R.id.btn_share_link);
        popBtnShareRepo = (LinearLayout)view.findViewById(R.id.btn_share_repo);
        popBtnShareUser = (LinearLayout)view.findViewById(R.id.btn_share_user);
        popBtnShareMail = (LinearLayout)view.findViewById(R.id.btn_share_mail);
        ImageView popIvShareLink = (ImageView)view.findViewById(R.id.iv_btn_share_link);
        ImageView popIvShareRepo = (ImageView)view.findViewById(R.id.iv_btn_share_repo);
        ImageView popIvShareUser = (ImageView)view.findViewById(R.id.iv_btn_share_user);
        ImageView popIvShareMail = (ImageView)view.findViewById(R.id.iv_btn_share_mail);
        popBtnShareLink.setOnClickListener(this);
        popBtnShareRepo.setOnClickListener(this);
        popBtnShareUser.setOnClickListener(this);
        popBtnShareMail.setOnClickListener(this);
        popIvShareLink.setOnClickListener(this);
        popIvShareRepo.setOnClickListener(this);
        popIvShareUser.setOnClickListener(this);
        popIvShareMail.setOnClickListener(this);
    }

    private void popCreateDialog() {
        final EditText editText = new EditText(getActivity());
        editText.setBackgroundResource(R.drawable.edit_background_pressed);
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle("新建文件夹");
        builder.setContentView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String dirName = editText.getText().toString();
                if(dirName == null || dirName.trim().length() < 1) {
                    Toast.makeText(getActivity(), "请输入新文件夹名称", Toast.LENGTH_SHORT).show();
                }else {
                    createDir(dirName);
                    dialog.dismiss();
                }
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

    private void showTitle() {
        if(!"-1".equals(currentDirPath)) {
            accountInfo.setVisibility(View.GONE);
            dirInfo.setVisibility(View.VISIBLE);
            String title = "";
            if(currentDirPath.contains("\\")) {
                title = currentDirPath.substring(currentDirPath.lastIndexOf("\\") + 1);
            }
            titleDir.setText(title);
        }else {
            accountInfo.setVisibility(View.VISIBLE);
            dirInfo.setVisibility(View.GONE);
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public boolean onBackPressed() {
        LogUtil.e("========MyPanFragment==========" + System.currentTimeMillis());
        if (!currentDirPath.equals("-1") && dirPathList.size() > 1) {
            dirPathList.remove(dirPathList.size() - 1);
            currentDirPath = dirPathList.get(dirPathList.size() - 1);
            fetchDataLocal(false);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Uri uri = data.getData();
                CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                cursor.moveToFirst();
                String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                if (path != null && path.length() > 0) {
                    uploadFile(new File(path));
                }
            }
        }else if(requestCode == 2) {

        }
    }

    private void uploadFile(File file) {
        Intent intent = new Intent(getActivity(), UploadService.class);
        intent.putExtra("t", 0);
        intent.putExtra("RepositoryID", "");
        intent.putExtra("cloudDirPath", currentDirPath);
        intent.putExtra("File", file);
        getActivity().startService(intent);
        Toast.makeText(getActivity(), "加入上传队列", Toast.LENGTH_SHORT).show();
    }
}