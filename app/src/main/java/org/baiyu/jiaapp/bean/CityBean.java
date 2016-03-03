package org.baiyu.jiaapp.bean;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by baiyu on 2015-10-10.
 */
public class CityBean extends DataSupport {
    private String cityName;
    private Date time;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
