package org.baiyu.jiaapp.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.baiyu.jiaapp.R;
import org.baiyu.jiaapp.customui.Topbar;

/**
 * Created by baiyu on 2015-9-22.
 * 关于
 */
public class BaiduActivity extends Activity {

    private Topbar topbar;
    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baidu_activity);

        topbar = (Topbar) findViewById(R.id.baidu_topbar);
        wv = (WebView) findViewById(R.id.baidu_wv_img);

        topbar.setOnTopbarClickListener(new Topbar.TopbarClickListener() {

            @Override
            public void leftClick() {
                BaiduActivity.this.finish();
            }

            @Override
            public void rightClick() {
            }
        });

        wv.loadUrl("http://m.baidu.com/?pu=sz%401321_480&wpo=btmfast");
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                wv.loadUrl(url);
                return true;
            }
        });
        // 启用支持JavaScript
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        // WebView加载页面优先使用缓存加载
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 判断是否可以返回上一页面
            if (wv.canGoBack()) {
                wv.goBack();// 返回上一页面
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
