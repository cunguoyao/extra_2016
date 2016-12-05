package com.keyun.pan.activity;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.keyun.pan.R;
import com.keyun.pan.service.UploadService;
import com.keyun.pan.utils.FileService;
import com.keyun.pan.utils.MyHttpCallback;
import com.keyun.pan.widget.CustomDialog;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.json.JSONObject;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(value = R.layout.activity_local_file)
public class LocalFileActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener {

    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.title_text)
    private TextView titleText;

    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;

    private int t;
    private String RepositoryID;
    private String cloudDirPath;
    private String path = "/sdcard";
    //文件路径
    private List<Map<String, Object>> items;
    //内容适配器
    private SimpleAdapter adapter;
    //备份文件父目录
    private File backFile=null;
    //当前文件目录
    private String currentPath="/sdcard";
    //文件是否成功删除
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        t = getIntent().getIntExtra("t", 0);
        RepositoryID = getIntent().getStringExtra("RepositoryID");
        cloudDirPath = getIntent().getStringExtra("cloudDirPath");
        titleText.setText("请选择文件");
        titleBack.setOnClickListener(this);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                listDir(path);
            }
        });
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listDir(path);
    }

    /**
     * 动态绑定文件信息到listview上
     * @param path
     */
    private void listDir(String path){
        items=bindList(path);

//		if(items!=null){
        adapter = new SimpleAdapter(this, items, R.layout.item_file,
                new String[] { "name", "path" ,"img"}, new int[] { R.id.tv_file_name,
                R.id.tv_file_msg ,R.id.iv_file_icon});
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        mListView.getRefreshableView().setSelection(0);
//		}
        mListView.onRefreshComplete();
    }
    /**
     * 返回所有文件目录信息
     * @param path
     * @return
     */
    private List<Map<String, Object>> bindList(String path){
        File[] files = new File(path).listFiles();
//		if(files!=null){
        List<Map<String, Object>> list= new ArrayList<Map<String, Object>>(files.length);
        /*Map<String, Object> root = new HashMap<String, Object>();
        root.put("name", "/sdcard");
        root.put("img", R.drawable.icon_list_folder);
        root.put("path", "root directory");
        list.add(root);*/
        Map<String, Object> pmap = new HashMap<String, Object>();
        pmap.put("name", "..");
        pmap.put("img", R.drawable.icon_list_folder);
        pmap.put("path", "上层文件夹");
        list.add(pmap);
        for (File file : files){
            Map<String, Object> map = new HashMap<String, Object>();
            if(file.isDirectory()){
                map.put("img", R.drawable.icon_list_folder);
            }else{
                map.put("img", R.drawable.icon_list_doc);
            }
            map.put("name", file.getName());
            map.put("path", file.getPath());
            list.add(map);
        }
        return list;
		/*}
		return null;*/
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

        /*if (position == 0) {//返回到/sdcard目录
            path = "/sdcard";
            listDir(path);
        }else */if(position == 1){//返回上一级目录
            toParent();
        } else {
            if(items!=null){
                path = (String) items.get(position-1).get("path");
                File file = new File(path);
                if (file.canRead()&&file.canExecute()&&file.isDirectory()){
                    listDir(path);
                }
                else{
                    popCofirmUploadFileDialog(file);
                }
            }
        }
        backFile=new File(path);
    }

    private void toParent() {//回到父目录
        File file = new File(path);
        File parent = file.getParentFile();
        if(parent == null){
            listDir(path);
        }else{
            path = parent.getAbsolutePath();
            listDir(path);
        }
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param sPath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public boolean DeleteFolder(String sPath) {
        flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }
    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 文件刷新
     * @param file
     */
    private void fileScan(String file){
        Uri data = Uri.parse("file://"+file);

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
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

    private void checkFileExist(final File file) {
        ProgressDialogUtils.showProgressDialog(this, true);
        RequestParams params = new RequestParams(Urls.checkFileExist());
        params.addBodyParameter("CheckSessionID", user.getToken());
        //String base64_path = new String(Base64.encode(currentDirPath.getBytes(), Base64.DEFAULT));
        //LogUtil.i("---base64_path:" + base64_path);
        String[] fileParams = FileService.getFileUploadParams(file);
        params.addBodyParameter("FileMD5", fileParams[0]);
        params.addBodyParameter("NeedJson", "1");
        params.setCharset("utf-8");
        for(KeyValue k : params.getQueryStringParams()) {
            LogUtil.e(k.key + ":" + k.value);
        }
        x.http().request(HttpMethod.POST, params, new MyHttpCallback(this) {
            @Override
            public void onSucceed(String result) {
                ProgressDialogUtils.dismissProgressBar();
                LogUtil.e("---onSuccess-----" + "onSuccess");
                if (result != null) {
                    try {
                        LogUtil.e("---onSuccess-result----" + result);
                        JSONObject json = new JSONObject(result);
                        if (json.optLong("ErrCode") == 0) {//文件已存在
                            Toast.makeText(LocalFileActivity.this, "文件已存在", Toast.LENGTH_SHORT).show();
                        }else if(json.optLong("ErrCode") == 7) {//文件不存在
                            uploadFile(file);
                        }else {
                            String msg = json.optString("ErrMsg", "请求失败");
                            Toast.makeText(LocalFileActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        onError(e, true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ProgressDialogUtils.dismissProgressBar();
                Toast.makeText(LocalFileActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                ProgressDialogUtils.dismissProgressBar();
                LogUtil.e("---onCancelled-----" + "onCancelled");
            }

            @Override
            public void onFinished() {
                ProgressDialogUtils.dismissProgressBar();
                LogUtil.e("---onFinished-----" + "onFinished");
            }
        });
    }

    /**
     * 启动文件打开
     * @param f
     */
    private void openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);

        // 获取文件媒体类型
        String type = getMIMEType(f);
        if(type==null)
            return;
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);
    }
    /**
     * 获取文件类型
     * @param f
     * @return
     */
    private String getMIMEType(File f) {
        String type = "";
        String fileName = f.getName();
        String end = fileName.substring(fileName.indexOf(".") + 1).toLowerCase();
        // 判断文件类型
        if(end.equals("wma") || end.equals("mp3") || end.equals("midi")||end.equals("ape")
                || end.equals("amr") || end.equals("ogg") || end.equals("wav")||end.equals("acc")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")||end.equals("rmvb")||end.equals("flv")
                ||end.equals("avi")||end.equals("wmv")||end.equals("f4v")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else {
            Toast.makeText(getApplicationContext(), "not media file", Toast.LENGTH_LONG).show();
            return null;
        }
        // MIME Type格式是"文件类型/文件扩展名"
        type += "/*";
        return type;
    }


    /**
     * 文件操作选择
     * @author Administrator
     *
     */
    class LongDialog implements DialogInterface.OnClickListener{
        private int pos=0;

        public LongDialog(int pos){
            this.pos=pos;
        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which){
                case 0:
                    currentPath = (String) items.get(pos).get("path");
                    //Mydialog(1);
                    break;
                case 1:
                    currentPath = (String) items.get(pos).get("path");
                    //Mydialog(2);
                    break;
            }
        }
    }

    private void popCofirmUploadFileDialog(final File file) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }
}