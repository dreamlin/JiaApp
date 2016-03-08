package org.baiyu.jiaapp.bean;

import org.litepal.crud.DataSupport;

/**
 * 城市N天天气预报
 * Created by baiyu on 2015-9-15.
 */
public class WeatherBean extends DataSupport {

    private String cityName;
    private String info;
    private String temperature;
    private String sum;
    private String date;

    public WeatherBean(String cityName, String date, String info, String sum, String temperature) {
        this.cityName = cityName;
        this.date = date;
        this.info = info;
        this.sum = sum;
        this.temperature = temperature;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
