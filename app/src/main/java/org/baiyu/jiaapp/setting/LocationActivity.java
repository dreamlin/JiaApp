package org.baiyu.jiaapp.setting;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import org.baiyu.jiaapp.R;
import org.baiyu.jiaapp.app.MyApplication;
import org.baiyu.jiaapp.customui.Topbar;

import java.util.List;

/**
 * Created by baiyu on 2015-9-25.
 * 百度定位，获取当前所在地
 */
public class LocationActivity extends Activity {

    private Topbar topbar;
    private TextView tvAddress, tvDescribe, tvBuilding;
    private LocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        topbar = (Topbar) findViewById(R.id.location_topbar);
        tvAddress = (TextView) findViewById(R.id.location_tv_location_address);
        tvDescribe = (TextView) findViewById(R.id.location_tv_location_describe);
        tvBuilding = (TextView) findViewById(R.id.location_tv_location_building);

        locationClient = ((MyApplication) getApplication()).locationClient;
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                String address = null;
                String describe = null;
                StringBuffer poiSB = new StringBuffer();
                boolean isSuccess = false;
                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    isSuccess = true;
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    isSuccess = true;
                } else {
                    address = "无法定位";
                    describe = "无";
                    Toast.makeText(LocationActivity.this, "定位失败，请检查gps、网络是否有效", Toast.LENGTH_SHORT).show();
                }
                if (isSuccess) {
                    address = "（所在城市：" + bdLocation.getCity() + "）" + bdLocation.getAddrStr();
                    describe = bdLocation.getLocationDescribe();
                    List<Poi> list = bdLocation.getPoiList();
                    for (int i = 0; i < list.size(); i++) {
                        if (i > 0)
                            poiSB.append("；");
                        poiSB.append(list.get(i).getName());
                    }
                }

                tvAddress.setText("当前地址：" + address);
                tvDescribe.setText("位置描述：" + describe);
                tvBuilding.setText("附近建筑：" + poiSB);
                locationClient.stop();
            }
        });
        initLocation();
        locationClient.start();

        topbar.setOnTopbarClickListener(new Topbar.TopbarClickListener() {
            @Override
            public void leftClick() {
                LocationActivity.this.finish();
            }

            @Override
            public void rightClick() {
            }
        });
    }


    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        locationClient.setLocOption(option);
    }
}
