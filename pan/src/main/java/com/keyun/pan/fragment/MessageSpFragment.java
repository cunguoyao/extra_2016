package com.keyun.pan.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.keyun.pan.R;
import com.keyun.pan.adapter.PushMessageAdapter;
import com.keyun.pan.data.PushMessage;

import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cunguoyao on 2016/7/10.
 */
public class MessageSpFragment extends BaseFragment {

    private final String TAG = MessageSpFragment.class.getName();
    private View rootView;
    @ViewInject(R.id.empty_data)
    private RelativeLayout emptyLayout;
    @ViewInject(R.id.listView)
    private PullToRefreshListView mListView;
    private List<PushMessage> mData;
    private PushMessageAdapter mAdapter;

    public static MessageSpFragment newInstance() {
        MessageSpFragment f = new MessageSpFragment();
        Bundle args = new Bundle();
        //args.putInt("categoryId", categoryId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mData = db.selector(PushMessage.class).where("type", "=", 1).or("type", "=", 2).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        mAdapter = new PushMessageAdapter(getActivity(), mData);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView = inflater.inflate(R.layout.fragment_message, container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        LogUtil.e(TAG + "------------onCreateView----");
        x.view().inject(this, rootView);   //注入控件

        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        if(mAdapter.getCount() > 0) {
            emptyLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }else {
            emptyLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
