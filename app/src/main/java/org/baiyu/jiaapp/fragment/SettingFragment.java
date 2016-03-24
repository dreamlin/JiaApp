package org.baiyu.jiaapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.baiyu.jiaapp.EnvionmentVariables;
import org.baiyu.jiaapp.R;
import org.baiyu.jiaapp.customui.CycleImageView;
import org.baiyu.jiaapp.customui.SwitchButton;
import org.baiyu.jiaapp.service.ConfigService;
import org.baiyu.jiaapp.setting.AboutActivity;
import org.baiyu.jiaapp.setting.BaiduActivity;
import org.baiyu.jiaapp.setting.LocationActivity;
import org.baiyu.jiaapp.setting.ManageCityActiviy;
import org.baiyu.jiaapp.util.SingletonImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baiyu on 2015-9-18.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {

    private TextView tv_manage_city, tv_find_location, tv_baidu_search, tv_check_version, tv_about;
    private SwitchButton sbtn_update_weather_info;

    private CycleImageView cycleImageView;
    private List<String> imageUrlList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_activity, container, false);

        //轮播图
        imageUrlList = new ArrayList<String>();
        imageUrlList.add("http://img.ivsky.com/img/tupian/pre/201507/22/meilidecaoyuanfengjingtupian.jpg");
        imageUrlList.add("http://img.ivsky.com/img/tupian/pre/201507/22/meilidecaoyuanfengjingtupian-002.jpg");
        imageUrlList.add("http://img.ivsky.com/img/tupian/pre/201507/22/meilidecaoyuanfengjingtupian-003.jpg");

        initView(view);
        initEvent();
        initData();

        return view;
    }

    private void initData() {
        sbtn_update_weather_info.setChecked(ConfigService.equalsValue(EnvionmentVariables.IS_UPDATE_WEATHER_INFO, "t", true));
    }

    private void initEvent() {
        tv_manage_city.setOnClickListener(this);
        tv_find_location.setOnClickListener(this);
        tv_baidu_search.setOnClickListener(this);
        tv_check_version.setOnClickListener(this);
        tv_about.setOnClickListener(this);

        sbtn_update_weather_info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value = isChecked ? "t" : "f";
                ConfigService.saveOrUpdate(EnvionmentVariables.IS_UPDATE_WEATHER_INFO, value);
                String flag = isChecked ? "自动更新天气开启" : "自动更新天气关闭";
                Toast.makeText(getActivity(), flag, Toast.LENGTH_SHORT).show();
            }
        });

        cycleImageView.setImageResources(imageUrlList, new CycleImageView.CycleImageViewListener() {
            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                SingletonImageLoader.getInstance().addImageListener(imageURL, imageView, R.drawable.image_stub, R.drawable.image_error);
            }

            @Override
            public void onImageClick(int position, View imageView) {
                Toast.makeText(getActivity(), "当前位置" + position, Toast.LENGTH_SHORT).show();
            }
        });
        cycleImageView.startImageCycle();
    }

    private void initView(View view) {
        tv_manage_city = (TextView) view.findViewById(R.id.setting_tv_manage_city);
        tv_find_location = (TextView) view.findViewById(R.id.setting_tv_find_location);
        tv_baidu_search = (TextView) view.findViewById(R.id.setting_tv_baidu_search);
        tv_check_version = (TextView) view.findViewById(R.id.setting_tv_check_version);
        tv_about = (TextView) view.findViewById(R.id.setting_tv_about);
        sbtn_update_weather_info = (SwitchButton) view.findViewById(R.id.setting_sbtn_update_weather_info);

        cycleImageView = (CycleImageView) view.findViewById(R.id.setting_ad);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_tv_manage_city:
                Intent manageCityIntent = new Intent(getActivity(), ManageCityActiviy.class);
                getActivity().startActivity(manageCityIntent);
                break;
            case R.id.setting_tv_find_location:
                Intent locationIntent = new Intent(getActivity(), LocationActivity.class);
                getActivity().startActivity(locationIntent);
                break;
            case R.id.setting_tv_check_version:
                Toast.makeText(getActivity(), "您当前版本为最新版", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting_tv_baidu_search:
                Intent baiduIntent = new Intent(getActivity(), BaiduActivity.class);
                getActivity().startActivity(baiduIntent);
                break;
            case R.id.setting_tv_about:
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                getActivity().startActivity(intent);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        cycleImageView.startImageTimerTask();
    }

    @Override
    public void onPause() {
        super.onPause();
        cycleImageView.stopImageTimerTask();
    }
}
