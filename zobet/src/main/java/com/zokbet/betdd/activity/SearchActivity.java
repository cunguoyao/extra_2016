package com.zokbet.betdd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zokbet.betdd.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_search)
public class SearchActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.fav_btn)
    private Button favBtn;
    @ViewInject(R.id.title_search_layout)
    private RelativeLayout titleSearchLayout;
    @ViewInject(R.id.text_search)
    private EditText searchText;

    private String keyword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleText.setVisibility(View.GONE);
        titleSearchLayout.setVisibility(View.VISIBLE);

        titleBack.setOnClickListener(this);
        favBtn.setOnClickListener(this);
        favBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.fav_btn:
                keyword = searchText.getText().toString();
                Intent intent = new Intent(this, MerchantListActivity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
                finish();
                break;
        }
    }
}
