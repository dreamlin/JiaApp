package org.baiyu.jiaapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
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
    private FragmentStatePagerAdapter mAdapter;
    private List<WeatherItemFragment> mFragments;
    private ImageView[] img = null;
    //记录当前选中位置
    private int currentIndex;

    private LinearLayout layout = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_activity, container, false);

        mFragments = new ArrayList<WeatherItemFragment>();

        initView(view);
        initData(null);

        mAdapter = new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        };

        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(mFragments.size());
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

        initImgView(mFragments.size());

        return view;
    }

    private void initData(List<CityBean> cityBeanList) {
        if (cityBeanList == null) {
            cityBeanList = DataSupport.order("time desc").find(CityBean.class);
        }
        if (cityBeanList.size() == 0) {
            CityBean city = new CityBean();
            city.setCityName("嘉兴");
            city.save();
            cityBeanList.add(city);
        }
        for (int i = 0; i < cityBeanList.size(); i++) {
            mFragments.add(new WeatherItemFragment(cityBeanList.get(i).getCityName()));
        }
    }

    private void initView(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.weather_viewPager);
        layout = (LinearLayout) view.findViewById(R.id.weather_viewGroup);
    }

    private void initImgView(int size) {
        layout.removeAllViews();
        img = new ImageView[size];
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

        viewPager.setCurrentItem(0);
    }


    @Override
    public void onStart() {
        super.onStart();
        boolean isUpdate = true;
        List<CityBean> cityBeanList = DataSupport.order("time desc").find(CityBean.class);
        if (cityBeanList.size() == mFragments.size()) {
            //是否相同
            boolean isEquals = true;
            for (int i = 0; i < mFragments.size(); i++) {
                if (!mFragments.get(i).getCityName().equals(cityBeanList.get(i).getCityName())) {
                    isEquals = false;
                    break;
                }
            }
            //如果相同，就不需要更新了
            if (isEquals) {
                isUpdate = false;
            }
        } else {
            initImgView(cityBeanList.size());
        }
        if (isUpdate) {
            mFragments.clear();
            initData(cityBeanList);
            mAdapter.notifyDataSetChanged();
        }
    }
}
