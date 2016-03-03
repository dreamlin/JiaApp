package org.baiyu.jiaapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.baiyu.jiaapp.service.ConfigService;
import org.baiyu.jiaapp.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baiyu on 2015-9-18.
 * 首次进入展示界面
 */
public class GuideActivity extends Activity {

    private ViewPager viewPager = null;
    private List<View> list = null;
    private ImageView[] img = null;
    //记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.guide_activity);

        viewPager = (ViewPager) findViewById(R.id.guide_viewPager);

        list = new ArrayList<View>();
        list.add(getLayoutInflater().inflate(R.layout.splash_activity, null));
        list.add(getLayoutInflater().inflate(R.layout.splash_activity, null));
        list.add(getLayoutInflater().inflate(R.layout.guide_last_pager, null));

        viewPager.setAdapter(new ViewPagerAdapter(list, this));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //当前页面被滑动时调用
            }

            @Override
            public void onPageSelected(int position) {
                //当新的页面被选中时调用
                if (position < 0 || position >= img.length || currentIndex == position) {
                    return;
                }
                img[position].setEnabled(true);
                img[currentIndex].setEnabled(false);
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //当滑动状态改变时调用
            }
        });

        img = new ImageView[list.size()];
        LinearLayout layout = (LinearLayout) findViewById(R.id.guide_viewGroup);
        for (int i = 0; i < img.length; i++) {
            img[i] = new ImageView(this);
            img[i].setImageResource(R.drawable.point);
            img[i].setPadding(20, 0, 20, 100);
            //默认都设为灰色
            img[i].setEnabled(false);
            layout.addView(img[i]);
        }
        //设置当面默认的位置
        currentIndex = 0;
        //设置为白色，即选中状态
        img[currentIndex].setEnabled(true);
    }

    class ViewPagerAdapter extends PagerAdapter {

        private List<View> list = null;
        private Activity activity = null;

        public ViewPagerAdapter(List<View> list, Activity activity) {
            this.list = list;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = list.get(position);
            container.addView(view);

            if (position == list.size() - 1) {
                Button btnStartMain = (Button) container.findViewById(R.id.btn_start_main);
                btnStartMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigService.saveOrUpdate(EnvionmentVariables.IS_FIRST_IN, CommonUtil.getVersion(GuideActivity.this));
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
