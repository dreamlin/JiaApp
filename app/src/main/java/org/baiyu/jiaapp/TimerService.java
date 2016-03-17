package org.baiyu.jiaapp;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import org.baiyu.jiaapp.app.MyApplication;
import org.baiyu.jiaapp.bean.WeatherBean;
import org.baiyu.jiaapp.bean.WeatherInfoBean;
import org.baiyu.jiaapp.service.ConfigService;
import org.baiyu.jiaapp.util.CommonUtil;
import org.baiyu.jiaapp.util.NetworkUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by baiyu on 2016-1-7.
 * 桌面时钟小组件定时器
 */
public class TimerService extends Service {

    private Timer timer;
    private SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTimeViews();
            }
        }, 0, 1000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateWeather();
            }
        }, 0, 20 * 60 * 1000);
    }

    private LocationClient locationClient;

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

    private void updateWeather() {
        if (NetworkUtil.GetNetworkType(getApplicationContext()) != NetworkUtil.NetworkType.NETWORKTYPE_NO) {
            locationClient = ((MyApplication) getApplication()).locationClient;
            locationClient.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    boolean isSuccess = false;
                    if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                        isSuccess = true;
                    } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                        isSuccess = true;
                    } else {
                        //定位失败，请检查gps、网络是否有效
                    }
                    if (isSuccess) {
                        if (!ConfigService.equalsValue(EnvionmentVariables.LOCATION_CITY_NAME, bdLocation.getCity(), false)) {
                            ConfigService.saveOrUpdate(EnvionmentVariables.LOCATION_CITY_NAME, bdLocation.getCity());
                            Toast.makeText(getApplicationContext(), "天气更新成功", Toast.LENGTH_SHORT).show();
                        }
                        getWeather(bdLocation.getCity());
                    }
                    locationClient.stop();
                }
            });
            initLocation();
            locationClient.start();
        } else {
            String cityName = ConfigService.getValue(EnvionmentVariables.LOCATION_CITY_NAME);
            if (cityName != null) {
                readWeatherCache(cityName);
            }
        }
    }

    private void getWeather(final String cityName) {
        boolean isPost = readWeatherCache(cityName);
        if (NetworkUtil.GetNetworkType(getApplicationContext()) == NetworkUtil.NetworkType.NETWORKTYPE_NO) {
            return;
        }

        //当上一次更新时间小时当前时间减1小时时，不更新天气
        if (!isPost) {
            return;
        }

        final String cityNameEncode = CommonUtil.encode(cityName, "UTF-8");
        String url = "http://op.juhe.cn/onebox/weather/query?cityname=" + cityNameEncode + "&key=2e2d339eda2c347abf2f15ee7de2c46b";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String errorCode = response.getString("error_code").trim();
                    if ("0".equals(errorCode)) {
                        List<WeatherBean> weatherBeanList = new ArrayList<WeatherBean>();
                        JSONObject resultJson = response.getJSONObject("result");
                        JSONObject dataJson = resultJson.getJSONObject("data");

                        JSONObject realtimeJson = dataJson.getJSONObject("realtime");

                        JSONObject weatherTodayJson = realtimeJson.getJSONObject("weather");
                        String dataUptime = String.valueOf(realtimeJson.getLong("dataUptime"));

                        String temperature = weatherTodayJson.getString("temperature");
                        String humidity = weatherTodayJson.getString("humidity");
                        String info = weatherTodayJson.getString("info");
                        //保存到数据库
                        WeatherInfoBean weatherInfoBean = null;
                        List<WeatherInfoBean> weatherInfoBeanList = DataSupport.where("cityName = ?", cityName).find(WeatherInfoBean.class);
                        if (weatherInfoBeanList.size() > 0) {
                            weatherInfoBean = weatherInfoBeanList.get(0);
                        } else {
                            weatherInfoBean = new WeatherInfoBean();
                        }
                        weatherInfoBean.setCityName(cityName);
                        weatherInfoBean.setTemperatur(temperature + "℃");
                        weatherInfoBean.setHumidity(humidity + "%");
                        weatherInfoBean.setInfo(info);
                        weatherInfoBean.setDataUptime(CommonUtil.getDataTime(dataUptime));
                        weatherInfoBean.setUpdateTime(new Date());
                        weatherInfoBean.save();
                        //获取、保存天气信息
                        weatherBeanList = new ArrayList<WeatherBean>();
                        JSONArray weatherJson = dataJson.getJSONArray("weather");
                        for (int i = 0; i < weatherJson.length(); i++) {
                            String flagDate = weatherJson.getJSONObject(i).getString("date") + "(" + weatherJson.getJSONObject(i).getString("week") + ")";

                            JSONObject listInfoJson = weatherJson.getJSONObject(i).getJSONObject("info");
                            JSONArray dayJson = listInfoJson.getJSONArray("day");
                            JSONArray nightJson = listInfoJson.getJSONArray("night");

                            String dayInfo = dayJson.getString(1);
                            String highTemperature = dayJson.getString(2);
                            String sunrise = dayJson.getString(5);

                            String nightInfo = nightJson.getString(1);
                            String lowTemperature = nightJson.getString(2);
                            String sunset = nightJson.getString(5);

                            String flagInfo, flagTemperature, flagSum;
                            if (dayInfo.equals(nightInfo)) {
                                flagInfo = dayInfo;
                            } else {
                                flagInfo = dayInfo + "转" + nightInfo;
                            }
                            flagTemperature = lowTemperature + "-" + highTemperature;
                            flagSum = sunrise + "-" + sunset;
                            weatherBeanList.add(new WeatherBean(cityName, flagDate, flagInfo, flagSum, flagTemperature));
                        }
                        DataSupport.deleteAll(WeatherBean.class, "cityName = ?", cityName);
                        DataSupport.saveAll(weatherBeanList);

                        String tomorrow = weatherBeanList.get(1).getInfo() + " " + weatherBeanList.get(1).getTemperature() + "℃";
                        //更新界面
                        updateWeatherViews(weatherInfoBean.getCityName(), weatherInfoBean.getTemperatur(), weatherInfoBean.getHumidity(), weatherInfoBean.getInfo(), tomorrow, weatherInfoBean.getDataUptime());

                        //Toast.makeText(getApplicationContext(), "天气更新成功", Toast.LENGTH_SHORT).show();
                    } else {
                        //天气更新失败，天气预报接口有误
                    }
                } catch (Exception e) {
                    //天气数据异常
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //网络异常
            }
        });

        request.setTag("volley_getjson_home" + cityName);
        MyApplication.getHttpQueues().add(request);
    }

    /**
     * 读取天气缓存，并判断是否需要更新天气信息
     *
     * @param cityName
     * @return
     */
    private boolean readWeatherCache(String cityName) {
        List<WeatherInfoBean> weatherInfoBeanList = DataSupport.where("cityName = ?", cityName).find(WeatherInfoBean.class);
        if (weatherInfoBeanList.size() > 0) {
            WeatherInfoBean weatherInfoBean = weatherInfoBeanList.get(0);

            List<WeatherBean> weatherBeanList = DataSupport.where("cityName = ?", cityName).find(WeatherBean.class);
            String tomorrow = weatherBeanList.get(1).getInfo() + " " + weatherBeanList.get(1).getTemperature() + "℃";

            updateWeatherViews(weatherInfoBean.getCityName(), weatherInfoBean.getTemperatur(), weatherInfoBean.getHumidity(), weatherInfoBean.getInfo(), tomorrow, weatherInfoBean.getDataUptime());

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.HOUR, -1);
            if (weatherInfoBean.getUpdateTime().compareTo(cal.getTime()) > 0) {
                //上次更新时间小于当前时间减1小时，不更新天气
                return false;
            }
        }
        return true;
    }

    /**
     * 更新界面天气信息
     *
     * @param cityName
     * @param temperatur
     * @param humidity
     * @param info
     * @param tomorrow
     * @param dataUptime
     */
    private void updateWeatherViews(String cityName, String temperatur, String humidity, String info, String tomorrow, String dataUptime) {
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget);
        rv.setTextViewText(R.id.widget_tv_city_name_value, cityName);
        rv.setTextViewText(R.id.widget_tv_temperature_value, temperatur);
        rv.setTextViewText(R.id.widget_tv_humidity_value, humidity);
        rv.setTextViewText(R.id.widget_tv_info_value, info);
        rv.setTextViewText(R.id.widget_tv_tomorrow_value, tomorrow);
        rv.setTextViewText(R.id.widget_tv_dataUptime_value, dataUptime);
        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName cn = new ComponentName(getApplicationContext(), WidgetProvider.class);
        manager.updateAppWidget(cn, rv);
    }

    private void updateTimeViews() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        if (dayOfWeek < 0)
            dayOfWeek = 0;

        String day = sdfDay.format(new Date()) + " " + weekDays[dayOfWeek];
        String time = sdfTime.format(new Date());
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget);
        rv.setTextViewText(R.id.widget_tv_day, day);
        rv.setTextViewText(R.id.widget_tv_time, time);
        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName cn = new ComponentName(getApplicationContext(), WidgetProvider.class);
        manager.updateAppWidget(cn, rv);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer = null;
    }
}
