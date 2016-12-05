package com.zokbet.betdd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zokbet.betdd.R;
import com.zokbet.betdd.adapter.MerchAlbumAdapter;
import com.zokbet.betdd.data.MerchantPhoto;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by cunguoyao on 2016/5/19.
 */
@ContentView(value = R.layout.activity_mechant_album)
public class MerchantAlbumActivity extends BaseActivity {

    @ViewInject(R.id.title_text)
    private TextView titleText;
    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.fav_btn)
    private ImageButton favBtn;
    @ViewInject(R.id.gridview)
    private GridView gridView;

    private ArrayList<MerchantPhoto> photos;
    private MerchAlbumAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        photos = (ArrayList<MerchantPhoto>)getIntent().getSerializableExtra("photo");
        if(photos == null) {
            Toast.makeText(this, "参数不正确", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        titleText.setText("商家相册");
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        favBtn.setVisibility(View.INVISIBLE);
        /*photos = new ArrayList<MerchantPhoto>();
        for(int i=0;i < 20;i++) {
            MerchantPhoto p = new MerchantPhoto();
            p.url = "http://www.86kx.com/uploads/allimg/150313/2292_150313170301_1.jpg";
            photos.add(p);
        }*/
        mAdapter = new MerchAlbumAdapter(this, photos);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MerchantAlbumActivity.this, ImagePagerActivity.class);
                intent.putExtra("photo", photos);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }
}
