package com.keyun.pan.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.keyun.pan.R;
import com.keyun.pan.adapter.UploadFileAdapter;
import com.keyun.pan.data.UploadFileItem;
import com.keyun.pan.utils.MyUtils;
import com.keyun.pan.widget.CustomDialog;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by cunguoyao on 2016/7/10.
 */
public class FileUploadFragment extends BaseFragment {

    private final String TAG = FileUploadFragment.class.getName();

    private PopupWindow popupWindow;

    private View rootView;

    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;

    private UploadFileAdapter mAdapter;
    private List<UploadFileItem> mData;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mAdapter.dataChange(mData);
                    break;
                case 2:
                    ProgressDialogUtils.dismissProgressBar();
                    break;
            }
        }
    };
    private TimerTask refreshTask = new TimerTask() {
        @Override
        public void run() {
            try {
                List<UploadFileItem> temp = db.selector(UploadFileItem.class)
                        .where("user_name", "=", user.getUserName()).orderBy("click_upload_time").findAll();
                if(temp == null) {
                    temp = new ArrayList<UploadFileItem>();
                }
                mData.clear();
                mData.addAll(temp);
                handler.sendEmptyMessage(1);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    };
    private ScheduledExecutorService scheduledPool;


    public static FileUploadFragment newInstance() {
        FileUploadFragment f = new FileUploadFragment();
        Bundle args = new Bundle();
        //args.putInt("categoryId", categoryId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();
        mAdapter = new UploadFileAdapter(this, mData);
        scheduledPool = Executors.newScheduledThreadPool(1);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_file_upload, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        LogUtil.e(TAG + "------------onCreateView----");
        x.view().inject(this, rootView);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UploadFileItem UploadFileItem = mAdapter.getItem(position-1);
                openFile(UploadFileItem);
            }
        });
        mListView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                UploadFileItem uploadFileItem = mAdapter.getItem(position-1);
                if(uploadFileItem.getUploadState() != UploadFileItem.STATE_UPLOAD_CANCEL
                        && uploadFileItem.getUploadState() != UploadFileItem.STATE_UPLOAD_ING) {
                    popConfirmDeleteRecord(uploadFileItem);
                }
                return false;
            }
        });
        fetchDataLocal();
        return rootView;
    }

    private void openFile(UploadFileItem UploadFileItem) {
        if (UploadFileItem.getUploadState() != UploadFileItem.STATE_UPLOAD_DONE) {
            Toast.makeText(getActivity(), "文件正在下载，下载完成后再打开", Toast.LENGTH_SHORT).show();
        } else {
            Intent i = MyUtils.openFile(UploadFileItem.getUploadPath());
            startActivity(i);
        }
        if(popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void deleteRecord(UploadFileItem uploadFileItem) {
        try {
            db.executeUpdateDelete("delete from file_upload_item where (user_name = '" + user.getUserName() + "' and hash = '" + uploadFileItem.getHash() +"');");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void fetchDataLocal() {
        ProgressDialogUtils.showProgressDialog(getActivity(), true);
        scheduledPool.scheduleAtFixedRate(refreshTask, 100, 1000, TimeUnit.MILLISECONDS);
        handler.sendEmptyMessageDelayed(2, 200);
    }

    private void popConfirmDeleteRecord(final UploadFileItem uploadFileItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(getResources().getStringArray(R.array.FileTransferItemLongClick),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (arg1 == 0) {
                            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                            builder.setTitle("提示");
                            builder.setMessage("确定要删除这条上传记录？");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteRecord(uploadFileItem);
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
                    }
                }
        );
        builder.create().show();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroy() {
        if(scheduledPool != null) {
            scheduledPool.shutdown();
        }
        super.onDestroy();
    }
}
