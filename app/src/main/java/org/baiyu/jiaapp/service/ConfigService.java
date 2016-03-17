package org.baiyu.jiaapp.service;

import org.baiyu.jiaapp.bean.ConfigBean;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by baiyu on 2015-9-23.
 * Config表数据库操作
 */
public class ConfigService {

    public static String getValue(String key) {
        List<ConfigBean> configBeanList = DataSupport.where("key = ?", key).find(ConfigBean.class);
        if (configBeanList.size() > 0) {
            return configBeanList.get(0).getValue();
        }
        return null;
    }

    public static boolean equalsValue(String key, String value, boolean defaultValue) {
        List<ConfigBean> configBeanList = DataSupport.where("key = ?", key).find(ConfigBean.class);
        if (configBeanList.size() > 0) {
            if (value.equalsIgnoreCase(configBeanList.get(0).getValue())) {
                return true;
            } else {
                return false;
            }
        }
        return defaultValue;
    }

    public static void saveOrUpdate(String key, String value) {
        List<ConfigBean> configBeanList = DataSupport.where("key = ?", key).find(ConfigBean.class);
        if (configBeanList.size() > 0) {
            ConfigBean configBean = configBeanList.get(0);
            configBean.setValue(value);
            configBean.save();
        } else {
            ConfigBean configBean = new ConfigBean();
            configBean.setKey(key);
            configBean.setValue(value);
            configBean.save();
        }
    }
}
