package com.keyun.pan.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.keyun.pan.R;
import com.keyun.pan.activity.SearchFileActivity;
import com.keyun.pan.adapter.SearchAutoAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cunguoyao on 2016/8/18.
 */
public class SearchHistoryFragment extends BaseFragment implements View.OnClickListener {

    private Activity activity;
    private EditText edit_search;
    private SearchAutoAdapter mSearchAutoAdapter;
    private ListView mAutoListView;
    private ImageView image_search;

    private List<String> searchHistory;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchHistory = getSearchHistory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /***** fragment所在的activity的控件 *****/
        //搜索框
        //搜索框内容变化时，显示历史内容也发生变化
        edit_search = (EditText) activity.findViewById(R.id.search_text);
        edit_search.addTextChangedListener(mTextChangeListener);
        edit_search.setOnKeyListener(onKeyListener);
        //搜索按钮
        image_search = (ImageView) activity.findViewById(R.id.search_btn);

        /****** fragment中的控件 *****/
        View view = inflater.inflate(R.layout.fragment_search_history, null);
        mAutoListView = (ListView) view.findViewById(R.id.auto_listview);
        mSearchAutoAdapter = new SearchAutoAdapter(activity, searchHistory, this);
        mAutoListView.setAdapter(mSearchAutoAdapter);
        mAutoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                /**
                 * 点击列表时，根据position取出对应的文字，并赋值给文本框，同时响应搜索按钮的点击事件
                 */
                String data = (String) mSearchAutoAdapter.getItem(position);
                edit_search.setText(data);
                image_search.performClick();
            }
        });
        return view;
    }

    /**** 文本监听器 ******/
    private TextWatcher mTextChangeListener = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /**
             * 根据输入的内容对显示的列表示数据进行过滤
             */
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            /**
             * 根据文本款的内容是否为空，隐藏和显示删除按钮
             */
           /* if (s.length() == 0) {
                ivDeleteText.setVisibility(View.GONE);
            } else {
                ivDeleteText.setVisibility(View.VISIBLE);
            }*/
        }
    };

    /**** EditText按键监听 ****/
    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            /**
             * 如果用户点击了手机键盘的enter键，响应搜索按钮的点击事件
             */
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                image_search.performClick();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 点击的如果是列表项目中的加号按钮，将加号按钮所在行的文字赋值给输入框
             */
            case R.id.auto_image:
                String data = (String) v.getTag();
                edit_search.setText(data);
                break;
            case R.id.auto_delete:
                String data2 = (String) v.getTag();
                deleteSearchHistory(data2);
                searchHistory.clear();
                searchHistory.addAll(getSearchHistory());
                mSearchAutoAdapter.notifyDataSetChanged();
                break;
        }
    }

    private List<String> getSearchHistory() {
        SharedPreferences sp = getActivity().getSharedPreferences(SearchFileActivity.SEARCH_HISTORY, 0);
        String longhistory = sp.getString(SearchFileActivity.SEARCH_HISTORY, "");
        if("".equals(longhistory)) {
            return new ArrayList<>();
        }
        String[] hisArrays = longhistory.split(",");
        List<String> historyList = new ArrayList<String>();
        if(hisArrays != null && hisArrays.length > 0) {
            for (int i = 0; i < hisArrays.length; i++) {
                historyList.add(hisArrays[i]);
            }
        }
        return historyList;
    }

    private void saveSearchHistory(String searchWord) {
        if(searchWord == null || "".equals(searchWord.trim()))return;
        SharedPreferences sp = getActivity().getSharedPreferences(SearchFileActivity.SEARCH_HISTORY, 0);
        String longhistory = sp.getString(SearchFileActivity.SEARCH_HISTORY, "");
        if("".equals(longhistory)) {
            sp.edit().putString(SearchFileActivity.SEARCH_HISTORY, searchWord).commit();
        }else {
            String[] hisArrays = longhistory.split(",");
            List<String> historyList = Arrays.asList(hisArrays);
            if(historyList != null && historyList.size() > 0) {
                if(historyList.size() > 3) {
                    List<String> newList = new ArrayList<>();
                    for(int i=historyList.size()-3;i<historyList.size();i++) {
                        newList.add(historyList.get(i));
                    }
                    if (newList.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < newList.size(); i++) {
                            sb.append(newList.get(i));
                            if(i != newList.size() - 1) {
                                sb.append(",");
                            }
                        }
                        sp.edit().putString(SearchFileActivity.SEARCH_HISTORY, sb.toString()).commit();
                    }else {
                        sp.edit().putString(SearchFileActivity.SEARCH_HISTORY, "").commit();
                    }
                }else {
                    sp.edit().putString(SearchFileActivity.SEARCH_HISTORY, longhistory + "," + searchWord).commit();
                }
            }
        }
    }

    private void deleteSearchHistory(String searchWord) {
        if(searchWord == null || "".equals(searchWord.trim()))return;
        SharedPreferences sp = getActivity().getSharedPreferences(SearchFileActivity.SEARCH_HISTORY, 0);
        String longhistory = sp.getString(SearchFileActivity.SEARCH_HISTORY, "");
        if("".equals(longhistory)) {
            return;
        }
        List<String> historyList = new ArrayList<String>();
        String[] hisArrays = longhistory.split(",");
        if(hisArrays != null && hisArrays.length > 0) {
            for (int i = 0; i < hisArrays.length; i++) {
                if(!hisArrays[i].equals(searchWord)) {
                    historyList.add(hisArrays[i]);
                }
            }
        }
        if (historyList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < historyList.size(); i++) {
                sb.append(historyList.get(i));
                if(i != historyList.size() - 1) {
                    sb.append(",");
                }
            }
            sp.edit().putString(SearchFileActivity.SEARCH_HISTORY, sb.toString()).commit();
        }else {
            sp.edit().putString(SearchFileActivity.SEARCH_HISTORY, "").commit();
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
