package com.keyun.pan.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.keyun.pan.R;
import com.keyun.pan.adapter.FilterTypeFileListAdapter;
import com.keyun.pan.data.FilterTypeFile;
import com.keyun.pan.service.UploadService;
import com.keyun.pan.utils.ScannerAsyncTask;
import com.keyun.pan.widget.CustomDialog;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/9/21.
 */
@ContentView(value = R.layout.activity_local_file)
public class LocalFileFilterActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.title_text)
    private TextView titleText;

    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;

    private int t;
    private String RepositoryID;
    private String cloudDirPath;

    private int type;
    private String[] sufFilter;
    private List<FilterTypeFile> mData;
    private FilterTypeFileListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        type = getIntent().getIntExtra("type", 1);
        if(type == 1) {
            sufFilter = new String[]{   ".txt", ".pdf", ".xml",
                                        ".doc", ".xls", ".ppt",
                                        ".docx", ".xlsx", "pptx" };
        }else if(type == 2) {
            sufFilter = new String[]{   ".mp3", ".aac", ".wav", ".wma", ".cda", ".flac", ".m4a",
                                        ".mid", ".mka", ".mp2", ".mpa", ".mpc", ".ape", ".ofr",
                                        ".ogg", ".ra", ".wv", ".tta", ".ac3", ".dts" };
        }else if(type == 3) {
            sufFilter = new String[]{   ".mp4", ".3gp", ".wmv", ".ts", ".rmvb", ".mov", ".flv", ".avs",
                                        ".m4v", ".avi", ".m3u8", ".3gpp", ".3gpp2", ".mkv", ".dat",
                                        ".divx", ".f4v", ".rm", ".asf", ".ram", ".mpg", ".mpeg", ".vob",
                                        ".ogm", ".v8", ".swf", ".m2v", ".asx", ".ra", ".ndivx", ".xvid"};
        }else {
            finish();
            return;
        }
        t = getIntent().getIntExtra("t", 0);
        RepositoryID = getIntent().getStringExtra("RepositoryID");
        cloudDirPath = getIntent().getStringExtra("cloudDirPath");

        mData = new ArrayList<>();
        mAdapter = new FilterTypeFileListAdapter(this, type, mData);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FilterTypeFile item = mAdapter.getItem(position-1);
                if(item != null) {
                    File file = new File(item.path);
                    popCofirmUploadFileDialog(file);
                }
            }
        });
        ScannerAsyncTask.ScannerFinishListener listener = new ScannerAsyncTask.ScannerFinishListener() {
            @Override
            public void onFinish(List<FilterTypeFile> files) {
                ProgressDialogUtils.dismissProgressBar();
                if(files != null) {
                    mData.clear();
                    mData.addAll(files);
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
        ScannerAsyncTask scannerAnsyTask = new ScannerAsyncTask(sufFilter, listener);
        ProgressDialogUtils.showProgressDialog(this, true);
        scannerAnsyTask.execute();
        titleBack.setOnClickListener(this);
        if(type == 1) {
            titleText.setText("选择文档文件");
        }else if(type == 2) {
            titleText.setText("选择音乐文件");
        }else if(type == 3) {
            titleText.setText("选择视频文件");
        }
    }

    private void popCofirmUploadFileDialog(final File file) {
        if(file == null || !file.exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定上传该文件？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadFile(file);
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

    private void uploadFile(File file) {
        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra("t", t);
        intent.putExtra("RepositoryID", RepositoryID);
        intent.putExtra("cloudDirPath", cloudDirPath);
        intent.putExtra("File", file);
        startService(intent);
        Toast.makeText(this, "加入上传队列", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

}
