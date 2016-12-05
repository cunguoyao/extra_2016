package com.keyun.pan.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import com.keyun.pan.R;
import com.keyun.pan.fragment.BaseFragment;
import com.keyun.pan.fragment.SearchHistoryFragment;
import com.keyun.pan.fragment.SearchResultFragment;
import com.keyun.pan.utils.BackHandledInterface;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cunguoyao on 2016/8/3.
 */
@ContentView(value = R.layout.activity_search_file)
public class SearchFileActivity extends BaseActivity implements View.OnClickListener,BackHandledInterface {

    private static final String TAG = SearchFileActivity.class.getName();
    public static final String SEARCH_HISTORY = "search_history";

    @ViewInject(R.id.title_back)
    private ImageButton titleBack;
    @ViewInject(R.id.search_text)
    private EditText searchText;
    @ViewInject(R.id.search_btn)
    private ImageButton searchBtn;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    private String searchContent;

    private BaseFragment mBackHandedFragment;

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        titleBack.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        /*searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchContent = searchText.getText().toString();
                    if(searchContent != null) {
                        searchContent = searchContent.trim();
                    }
                    if(searchContent != null && searchContent.length() > 0) {
                        search();
                    }
                }
                return false;
            }
        });*/
        initHistoryFragment();
    }

    /** 初始化显示的fragment **/
    private void initHistoryFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        Fragment fragment = new SearchHistoryFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        transaction.replace(R.id.content_frame_search, fragment, "history").commit();
    }

    /**
     * 切换到产品碎片
     * @param searchContent
     */
    private void changeToFragment(String searchContent) {
        transaction = manager.beginTransaction();
        Fragment fragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString("searchContent", searchContent);
        fragment.setArguments(bundle);
        transaction.replace(R.id.content_frame_search, fragment, "TAG" + searchContent).commit();
    }

    /**** 显示键盘 *******/
    public void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
    }

    /****** 隐藏键盘 ********/
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void goSearch() {
        /*隐藏软键盘*/
        hideSoftKeyboard();
        String searchContent = searchText.getText().toString().trim();
        if (searchContent != null && searchContent.length() > 0) {
            changeToFragment(searchContent);
        }
    }
    /*
     * 保存搜索记录
     */
    private void saveSearchHistory(String searchWord) {
        if(searchWord == null || "".equals(searchWord.trim()))return;
        SharedPreferences sp = getSharedPreferences(SearchFileActivity.SEARCH_HISTORY, 0);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.search_btn:
                searchContent = searchText.getText().toString();
                if(searchContent != null) {
                    searchContent = searchContent.trim();
                }
                if(searchContent != null && searchContent.length() > 0) {
                    saveSearchHistory(searchContent);//保存输入记录
                    goSearch();//进行搜索
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
            ScreenManager.getScreenManager().popActivity();
        }else {
        }
    }
}
