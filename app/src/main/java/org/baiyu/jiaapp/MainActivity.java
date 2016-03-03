package org.baiyu.jiaapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.baiyu.jiaapp.fragment.SearchFragment;
import org.baiyu.jiaapp.fragment.SettingFragment;
import org.baiyu.jiaapp.fragment.WeatherFragment;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private LinearLayout mTabWeather;
    private LinearLayout mTabSearch;
    private LinearLayout mTabSettings;

    private ImageButton mImgWeather;
    private ImageButton mImgSearch;
    private ImageButton mImgSettings;

    private TextView mTvWeather;
    private TextView mTvSearch;
    private TextView mTvSettings;

    private Fragment mWeatherFragment;
    private Fragment mSearchFragment;
    private Fragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        setSelect(0);
    }

    private void initEvent() {
        mTabWeather.setOnClickListener(this);
        mTabSearch.setOnClickListener(this);
        mTabSettings.setOnClickListener(this);
    }

    private void initView() {
        mTabWeather = (LinearLayout) findViewById(R.id.id_tab_index);
        mTabSearch = (LinearLayout) findViewById(R.id.id_tab_search);
        mTabSettings = (LinearLayout) findViewById(R.id.id_tab_settings);

        mImgWeather = (ImageButton) findViewById(R.id.id_tab_index_img);
        mImgSearch = (ImageButton) findViewById(R.id.id_tab_search_img);
        mImgSettings = (ImageButton) findViewById(R.id.id_tab_settings_img);

        mTvWeather = (TextView) findViewById(R.id.tv_tab_index_text);
        mTvSearch = (TextView) findViewById(R.id.tv_tab_search_text);
        mTvSettings = (TextView) findViewById(R.id.tv_tab_settings_text);
    }

    private void setSelect(int i) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        // 隐藏
        hideFragment(transaction);
        // 把图片设置为亮的
        // 设置内容区域
        switch (i) {
            case 0:
                if (mWeatherFragment == null) {
                    mWeatherFragment = new WeatherFragment();
                    transaction.add(R.id.id_content, mWeatherFragment);
                } else {
                    transaction.show(mWeatherFragment);
                }
                mImgWeather.setBackgroundResource(R.drawable.tab_index_pressed);
                mTvWeather.setTextColor(Color.rgb(255, 116, 24));
                break;
            case 1:
                if (mSearchFragment == null) {
                    mSearchFragment = new SearchFragment();
                    transaction.add(R.id.id_content, mSearchFragment);
                } else {
                    transaction.show(mSearchFragment);
                }
                mImgSearch.setBackgroundResource(R.drawable.tab_index_search_pressed);
                mTvSearch.setTextColor(Color.rgb(255, 116, 24));
                break;
            case 2:
                if (mSettingsFragment == null) {
                    mSettingsFragment = new SettingFragment();
                    transaction.add(R.id.id_content, mSettingsFragment);
                } else {
                    transaction.show(mSettingsFragment);
                }
                mImgSettings.setBackgroundResource(R.drawable.tab_settings_pressed);
                mTvSettings.setTextColor(Color.rgb(255, 116, 24));
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (mWeatherFragment != null) {
            transaction.hide(mWeatherFragment);
        }
        if (mSearchFragment != null) {
            transaction.hide(mSearchFragment);
        }
        if (mSettingsFragment != null) {
            transaction.hide(mSettingsFragment);
        }
    }

    @Override
    public void onClick(View v) {
        resetImgs();
        switch (v.getId()) {
            case R.id.id_tab_index:
                setSelect(0);
                break;
            case R.id.id_tab_search:
                setSelect(1);
                break;
            case R.id.id_tab_settings:
                setSelect(2);
                break;
            default:
                break;
        }
    }

    /**
     * 切换图片至暗色
     */
    private void resetImgs() {
        mImgWeather.setBackgroundResource(R.drawable.tab_index_normal);
        mImgSearch.setBackgroundResource(R.drawable.tab_index_search_normal);
        mImgSettings.setBackgroundResource(R.drawable.tab_settings_normal);
        mTvWeather.setTextColor(Color.rgb(220, 220, 220));
        mTvSearch.setTextColor(Color.rgb(220, 220, 220));
        mTvSettings.setTextColor(Color.rgb(220, 220, 220));
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次，退出气象佳", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
