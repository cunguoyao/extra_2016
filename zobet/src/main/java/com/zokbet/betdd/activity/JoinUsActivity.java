package com.zokbet.betdd.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zokbet.betdd.R;
import com.zokbet.betdd.widget.MyWebView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_join_us)
public class JoinUsActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.fav_btn)
    private ImageButton favBtn;
    @ViewInject(R.id.webview)
    private MyWebView webView;

    private String url;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleText.setText("加入我们");
        titleBack.setOnClickListener(this);
        favBtn.setVisibility(View.INVISIBLE);

        // ~~~ 获取参数
        //url = getIntent().getStringExtra("url");
        //name = getIntent().getStringExtra("name");
        url = "http://www.zokbet.com/job";
        // ~~~ 设置数据
        //titleText.setText(name);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url != null && url.startsWith("http://"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        webView.loadUrl(url);
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
