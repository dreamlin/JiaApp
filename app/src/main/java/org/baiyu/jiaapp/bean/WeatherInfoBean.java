package org.baiyu.jiaapp.bean;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * 城市信息
 * Created by baiyu on 2015-9-17.
 */
public class WeatherInfoBean extends DataSupport {
    private String cityName;
    private String temperatur;
    private String humidity;
    private String info;
    private String dataUptime;
    private Date updateTime;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTemperatur() {
        return temperatur;
    }

    public void setTemperatur(String temperatur) {
        this.temperatur = temperatur;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDataUptime() {
        return dataUptime;
    }

    public void setDataUptime(String dataUptime) {
        this.dataUptime = dataUptime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
