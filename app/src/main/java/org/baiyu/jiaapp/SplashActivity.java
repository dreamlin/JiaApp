package org.baiyu.jiaapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import org.baiyu.jiaapp.service.ConfigService;
import org.baiyu.jiaapp.util.CommonUtil;

/**
 * Created by baiyu on 2015-9-18.
 * 欢迎页
 */
public class SplashActivity extends Activity {

    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    //跳转到下一个界面的时间
    private static final long SPLASH_DELAY_MILLIS = 1000;

    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
            }
            switch (msg.what) {
                case GO_GUIDE:
                    goGuide();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        String version = CommonUtil.getVersion(this);

        TextView tvSplashVersion = (TextView) findViewById(R.id.tv_splash_version);
        tvSplashVersion.setText("v " + version);

        if (ConfigService.equalsValue(EnvionmentVariables.IS_FIRST_IN, version, false)) {
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
        }
    }

    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private void goGuide() {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
}