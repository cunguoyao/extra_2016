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
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.keyun.pan.R;
import com.keyun.pan.adapter.DownloadFileAdapter;
import com.keyun.pan.app.Const;
import com.keyun.pan.data.DownFileItem;
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
public class FileDownloadFragment extends BaseFragment {

    private final String TAG = FileDownloadFragment.class.getName();

    private PopupWindow popupWindow;

    private View rootView;
    @ViewInject(R.id.text)
    private TextView textView;

    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;

    private DownloadFileAdapter mAdapter;
    private List<DownFileItem> mData;
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
                List<DownFileItem> temp = db.selector(DownFileItem.class)
                        .where("user_name", "=", user.getUserName()).orderBy("click_down_time").findAll();
                if(temp == null) {
                    temp = new ArrayList<DownFileItem>();
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

    public static FileDownloadFragment newInstance() {
        FileDownloadFragment f = new FileDownloadFragment();
        Bundle args = new Bundle();
        //args.putInt("categoryId", categoryId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();
        mAdapter = new DownloadFileAdapter(this, mData);
        scheduledPool = Executors.newScheduledThreadPool(1);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_file_download, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        LogUtil.e(TAG + "------------onCreateView----");
        x.view().inject(this, rootView);

        textView.setText("文件下载至：" + Const.getDownloadDir());

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DownFileItem downFileItem = mAdapter.getItem(position-1);
                openFile(downFileItem);
            }
        });
        mListView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DownFileItem downFileItem = mAdapter.getItem(position-1);
                if(downFileItem.getDownloadState() != DownFileItem.STATE_DOWN_CANCEL
                        && downFileItem.getDownloadState() != DownFileItem.STATE_DOWN_ING) {
                    popConfirmDeleteRecord(downFileItem);
                }
                return false;
            }
        });
        fetchDataLocal();
        return rootView;
    }

    private void openFile(DownFileItem downFileItem) {
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

    private void deleteRecord(DownFileItem downFileItem) {
        try {
            db.executeUpdateDelete("delete from file_down_item where (user_name = '" + user.getUserName() + "' and hash = '" + downFileItem.getHash() +"');");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void fetchDataLocal() {
        ProgressDialogUtils.showProgressDialog(getActivity(), true);
        scheduledPool.scheduleAtFixedRate(refreshTask, 100, 1000, TimeUnit.MILLISECONDS);
        handler.sendEmptyMessageDelayed(2, 200);
    }

    private void popConfirmDeleteRecord(final DownFileItem downFileItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(getResources().getStringArray(R.array.FileTransferItemLongClick),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    if (arg1 == 0) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                        builder.setTitle("提示");
                        builder.setMessage("确定要删除这条下载记录？");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteRecord(downFileItem);
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
