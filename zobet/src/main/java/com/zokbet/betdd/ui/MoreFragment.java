package com.zokbet.betdd.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zokbet.betdd.activity.AboutUsActivity;
import com.zokbet.betdd.activity.AdviceActivity;
import com.zokbet.betdd.activity.CaptureActivity;
import com.zokbet.betdd.activity.JoinUsActivity;
import com.zokbet.betdd.R;
import com.zokbet.betdd.widget.ProgressDialogUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/5/15.
 */
public class MoreFragment extends BaseFragment implements View.OnClickListener {

    private final String TAG = MoreFragment.class.getName();
    private View rootView;

    @ViewInject(R.id.more_layout_scan)
    private RelativeLayout scanLayout;
    @ViewInject(R.id.more_layout_clean)
    private RelativeLayout cleanLayout;
    @ViewInject(R.id.more_layout_join)
    private RelativeLayout joinLayout;
    @ViewInject(R.id.more_layout_advice)
    private RelativeLayout adviceLayout;
    @ViewInject(R.id.more_layout_about)
    private RelativeLayout aboutLayout;

    public MoreFragment() {
        LogUtil.e(TAG + "--------------new");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_more, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        LogUtil.e(TAG + "------------onCreateView----");
        x.view().inject(this, rootView);

        scanLayout.setOnClickListener(this);
        cleanLayout.setOnClickListener(this);
        joinLayout.setOnClickListener(this);
        adviceLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_layout_scan:
                Intent intent1 = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent1, CaptureActivity.REQ_SCAN_CODE);
                break;
            case R.id.more_layout_clean:
                ProgressDialogUtils.showProgressDialog(getActivity(), false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        x.image().clearMemCache();
                        x.image().clearCacheFiles();
                        ProgressDialogUtils.dismissProgressBar();
                        Toast.makeText(getActivity(), "缓存已清空", Toast.LENGTH_SHORT).show();
                    }
                }, 3000);
                break;
            case R.id.more_layout_join:
                Intent intent3 = new Intent(getActivity(), JoinUsActivity.class);
                startActivity(intent3);
                break;
            case R.id.more_layout_advice:
                Intent intent4 = new Intent(getActivity(), AdviceActivity.class);
                startActivity(intent4);
                break;
            case R.id.more_layout_about:
                Intent intent5 = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(intent5);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == CaptureActivity.REQ_SCAN_CODE) {
                String result = data.getStringExtra("result");// Scan result Not Match
                String pay = data.getStringExtra("pay");
                if(!TextUtils.isEmpty(result)) {
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
