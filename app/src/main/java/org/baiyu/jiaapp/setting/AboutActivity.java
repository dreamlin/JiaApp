package org.baiyu.jiaapp.setting;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.baiyu.jiaapp.R;
import org.baiyu.jiaapp.customui.Topbar;
import org.baiyu.jiaapp.util.CommonUtil;

/**
 * Created by baiyu on 2015-9-22.
 * 关于
 */
public class AboutActivity extends Activity {

    private Topbar topbar;
    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        topbar = (Topbar) findViewById(R.id.about_topbar);
        tvVersion = (TextView) findViewById(R.id.about_tv_version);

        tvVersion.setText("v " + CommonUtil.getVersion(this));

        topbar.setOnTopbarClickListener(new Topbar.TopbarClickListener() {

            @Override
            public void leftClick() {
                AboutActivity.this.finish();
            }

            @Override
            public void rightClick() {
            }
        });
    }
}
