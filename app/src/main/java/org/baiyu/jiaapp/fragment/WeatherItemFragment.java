package org.baiyu.jiaapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.baiyu.jiaapp.EnvionmentVariables;
import org.baiyu.jiaapp.R;
import org.baiyu.jiaapp.app.MyApplication;
import org.baiyu.jiaapp.bean.WeatherBean;
import org.baiyu.jiaapp.bean.WeatherInfoBean;
import org.baiyu.jiaapp.service.ConfigService;
import org.baiyu.jiaapp.util.CommonAdapter;
import org.baiyu.jiaapp.util.CommonUtil;
import org.baiyu.jiaapp.util.NetworkUtil;
import org.baiyu.jiaapp.util.ViewHolder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by baiyu on 2015-9-18.
 */
public class WeatherItemFragment extends Fragment {
    private String cityName;

    public WeatherItemFragment(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    private TextView tv_city_name_value;//城市名称
    private TextView tv_temperature_value;//温度
    private TextView tv_humidity_value;//湿度
    private TextView tv_info_value;//天气
    private TextView tv_dataUptime_value;//更新时间
    private List<WeatherBean> weatherBeanList;
    private CommonAdapter<WeatherBean> weatherAdapter;
    private ListView weatherLV;
    private PtrClassicFrameLayout ptrFrameLayout;//下拉刷新
    private boolean isPost = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_info_item, container, false);
        initView(view);
        initData(view);
        initEvent(view, cityName);
        //判断打开APP时是否需要刷新历史气象记录
        if (isPost) {
            if (ConfigService.equalsValue(EnvionmentVariables.IS_UPDATE_WEATHER_INFO, "t", true)) {
                getWeather(cityName);
            }
        }
        return view;
    }

    private void initData(View view) {
        List<WeatherInfoBean> weatherInfoBeanList = DataSupport.where("cityName = ?", cityName).find(WeatherInfoBean.class);
        if (weatherInfoBeanList.size() > 0) {
            WeatherInfoBean weatherInfoBean = weatherInfoBeanList.get(0);

            //上次更新时间大于当前时间减1小时，读取数据库缓存
            tv_city_name_value.setText(weatherInfoBean.getCityName());
            tv_temperature_value.setText(weatherInfoBean.getTemperatur());
            tv_humidity_value.setText(weatherInfoBean.getHumidity());
            tv_info_value.setText(weatherInfoBean.getInfo());
            tv_dataUptime_value.setText(weatherInfoBean.getDataUptime());

            weatherBeanList = DataSupport.where("cityName = ?", cityName).find(WeatherBean.class);
            weatherBeanList.add(0, new WeatherBean("城市", "日期", "天气", "日出日落", "温度"));

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.HOUR, -1);
            if (weatherInfoBean.getUpdateTime().compareTo(cal.getTime()) < 0) {
                //上次更新时间小于当前时间减1小时，请求网络更新
                isPost = true;
            }
        } else {
            isPost = true;
        }
    }

    private void initEvent(View view, final String cityName) {
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getWeather(cityName);
                        ptrFrameLayout.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }
        });

        //7天天气数据
        if (weatherBeanList == null)
            weatherBeanList = new ArrayList<WeatherBean>();

        weatherAdapter = new CommonAdapter<WeatherBean>(WeatherItemFragment.this.getActivity(), weatherBeanList, R.layout.weather_list_item) {
            @Override
            public void convert(ViewHolder holder, WeatherBean weatherBean) {
                holder.setText(R.id.tv_weather_date, weatherBean.getDate());
                holder.setText(R.id.tv_weather_info, weatherBean.getInfo());
                holder.setText(R.id.tv_weather_temperature, weatherBean.getTemperature());
                holder.setText(R.id.tv_weather_sum, weatherBean.getSum());
            }
        };
        weatherLV.setAdapter(weatherAdapter);
    }

    private void initView(View view) {
        ptrFrameLayout = (PtrClassicFrameLayout) view.findViewById(R.id.weather_ptr_frame);
        tv_city_name_value = (TextView) view.findViewById(R.id.tv_city_name_value);
        tv_temperature_value = (TextView) view.findViewById(R.id.tv_temperature_value);
        tv_humidity_value = (TextView) view.findViewById(R.id.tv_humidity_value);
        tv_info_value = (TextView) view.findViewById(R.id.tv_info_value);
        tv_dataUptime_value = (TextView) view.findViewById(R.id.tv_dataUptime_value);
        weatherLV = (ListView) view.findViewById(R.id.lv_weather);
    }

    private void getWeather(final String cityName) {
        if (NetworkUtil.GetNetworkType(WeatherItemFragment.this.getActivity()) == NetworkUtil.NetworkType.NETWORKTYPE_NO) {
            Toast.makeText(WeatherItemFragment.this.getActivity(), "网络未开启", Toast.LENGTH_SHORT).show();
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
                        JSONObject resultJson = response.getJSONObject("result");
                        JSONObject dataJson = resultJson.getJSONObject("data");

                        JSONObject realtimeJson = dataJson.getJSONObject("realtime");

                        JSONObject weatherTodayJson = realtimeJson.getJSONObject("weather");
                        String dataUptime = String.valueOf(realtimeJson.getLong("dataUptime"));

                        String temperature = weatherTodayJson.getString("temperature");
                        String humidity = weatherTodayJson.getString("humidity");
                        String info = weatherTodayJson.getString("info");

                        tv_city_name_value.setText(cityName);
                        tv_temperature_value.setText(temperature + "℃");
                        tv_humidity_value.setText(humidity + "%");
                        tv_info_value.setText(info);
                        tv_dataUptime_value.setText(CommonUtil.getDataTime(dataUptime));

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

                        weatherBeanList.add(0, new WeatherBean("城市", "日期", "天气", "日出日落", "温度"));
                        weatherAdapter.setmDatas(weatherBeanList);
                        weatherAdapter.notifyDataSetChanged();

                        Toast.makeText(WeatherItemFragment.this.getActivity(), "天气更新成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WeatherItemFragment.this.getActivity(), "天气更新失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(WeatherItemFragment.this.getActivity(), "天气数据异常", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WeatherItemFragment.this.getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
            }
        });

        request.setTag("volley_getjson" + cityName);
        MyApplication.getHttpQueues().add(request);
    }
}
