package org.baiyu.jiaapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.baiyu.jiaapp.R;
import org.baiyu.jiaapp.bean.CityBean;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baiyu on 2015-9-18.
 */
public class WeatherFragment extends Fragment {

    private ViewPager viewPager = null;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;
    private ImageView[] img = null;
    //记录当前选中位置
    private int currentIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_activity, container, false);

        mFragments = new ArrayList<Fragment>();
        List<CityBean> cityBeanList = DataSupport.order("time desc").find(CityBean.class);
        if (cityBeanList.size() == 0) {
            CityBean city = new CityBean();
            city.setCityName("嘉兴");
            city.save();
            cityBeanList.add(city);
        }
        for (int i = 0; i < cityBeanList.size(); i++) {
            mFragments.add(new WeatherItemFragment(cityBeanList.get(i).getCityName()));
        }
        mAdapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }
        };

        viewPager = (ViewPager) view.findViewById(R.id.weather_viewPager);
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(cityBeanList.size());
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

        img = new ImageView[mFragments.size()];
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.weather_viewGroup);
        for (int i = 0; i < img.length; i++) {
            img[i] = new ImageView(getActivity());
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

        return view;
    }
}
