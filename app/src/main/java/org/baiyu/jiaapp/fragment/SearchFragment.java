package org.baiyu.jiaapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import org.baiyu.jiaapp.R;
import org.baiyu.jiaapp.search.SearchCityFrag;
import org.baiyu.jiaapp.search.SearchImgFrag;

/**
 * Created by baiyu on 2015-9-28.
 */
public class SearchFragment extends Fragment {

    PagerSlidingTabStrip tabs;
    ViewPager pager;
    DisplayMetrics dm;

    SearchCityFrag searchCityFrag;
    SearchImgFrag searchImgFrag;
    String[] titles = {"搜索城市", "搜索图片"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_activity, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        dm = getResources().getDisplayMetrics();
        pager = (ViewPager) view.findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabs.setTextColorStateListResource(R.drawable.tab_selector);
        pager.setAdapter(new MyAdapter(getFragmentManager(), titles));
        tabs.setViewPager(pager);
    }

    public class MyAdapter extends FragmentPagerAdapter {
        String[] _titles;

        public MyAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            _titles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _titles[position];
        }

        @Override
        public int getCount() {
            return _titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (searchCityFrag == null) {
                        searchCityFrag = new SearchCityFrag();
                    }
                    return searchCityFrag;
                case 1:
                    if (searchImgFrag == null) {
                        searchImgFrag = new SearchImgFrag();
                    }
                    return searchImgFrag;
                default:
                    return null;
            }
        }
    }
}
