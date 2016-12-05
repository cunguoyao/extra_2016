package com.keyun.pan.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.keyun.pan.R;
import com.keyun.pan.widget.ProgressDialogUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_advice)
public class AdviceActivity extends BaseActivity {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.fav_btn)
    private Button submit;
    @ViewInject(R.id.editText)
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleText.setText("意见反馈");
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogUtils.showProgressDialog(AdviceActivity.this, true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //submit
                        ProgressDialogUtils.dismissProgressBar();
                        Toast.makeText(AdviceActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
            }
        });

    }


}
